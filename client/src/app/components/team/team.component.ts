import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {UserService} from "../../service/member/user.service";
import {User} from "../../models/user.model";
import {FormGroup, FormBuilder} from "@angular/forms";
import {Router} from "@angular/router";
import {catchError, last} from "rxjs/operators";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AuthorizationService} from "../../service/authorization/authorization.service";
import {environment} from "../../../environments/environment";
import {throwError} from "rxjs";
import {InvitedUsersComponent} from "./invited-users/invited-users.component";

@Component({
  selector: 'app-team',
  templateUrl: './team.component.html',
  styleUrls: ['./team.component.scss']
})
export class TeamComponent implements OnInit {

  environment = environment;
  level: number;
  inviteForm!: FormGroup;
  editForm!: FormGroup;
  roles: string[] = [];
  // The levels can be hardcoded since these values won't change, we start level 1 since 0 is admin and you cant
  // make that on team page
  levels: any = {1: "Team Captain", 2: "Researcher", 3: "Guest"};
  users: User[] = [];
  selectedUser = User;

  constructor(public userService: UserService, private fb: FormBuilder, private router: Router,
              private ref: ChangeDetectorRef, private authService: AuthorizationService, private http: HttpClient) { }

  ngOnInit(): void {
    this.initializeForm();
    this.authService.isAdmin().subscribe((data) => {
      this.level = data;
    });
    // Init the users
    this.userService.getUsersByTeam();
    // Get all roles
    this.getAllRoles();
  }

  ngAfterViewChecked() {
    this.ref.detectChanges();
  }

  /**
   * Change selected member and the id
   * @param id, to get the user
   */
  changeSelectedId(id: any): void {
    this.userService.selectedUser = this.userService.findById(id)
    this.userService.selectedUserId = this.userService.selectedUser.id;
    this.router.navigate(['team', this.userService.selectedUserId])
  }

  /**
   * Change the selected user, also open the modal for editing user
   * @param id
   */
  changeSelectedIdAndModal(id: number): void {
    this.userService.selectedUser = this.userService.findById(id)
    this.userService.selectedUserId = this.userService.selectedUser.id;

    // It needs time to set the button click, or else it will crash
    let button = document.getElementById("editButtonTeamAdmin");
    setTimeout(() => {
      button.click();
    }, 1);
  }

  /**
   * Init the reactive form
   */
  initializeForm(): void {
    this.inviteForm = this.fb.group({
      first_name: "",
      last_name: "",
      email: "",
      role: "",
      level: "",
    });

    this.editForm = this.fb.group({
      role: ""
    })
  }

  /**
   * When the edit button is clicked, update the user role, and change it in the front end and back end
   * @param id
   */
  onEditSubmit(id: number): boolean {
    let tempMember = this.userService.findById(id);

    tempMember.role = this.editForm.value.role;
    // Update
    this.userService.updateUserRole(tempMember).subscribe((member) => {
      // Search for the user in the users array, and change the role
      this.userService.users[this.userService.users.indexOf(tempMember)].role_name = this.editForm.value.role;
    }, (error => {
      console.log(error)
    }));
    return true;
  }
  /**
   * Submits the invite form and validates it
   */
  onInviteSubmit(): boolean {
    //Remove succes message
    document.getElementById("succes-message--form")!.innerHTML= "";

    // Loop over values of reactive form
    for (const [key, value] of Object.entries(this.inviteForm.value)) {
      document.getElementById(key + "--error")!.innerHTML = "";
      if (!value) {
        document.getElementById(key + "--error")!.innerHTML = "Value is empty";
        return false;
      }
    }

    // Add new member
    let member = new User();
    member.id = 0;
    // A front end check to see if they set the user level to guest.
    member.role = (this.inviteForm.value.level === "3") ? "Guest" : this.inviteForm.value.role;
    member.firstname = this.inviteForm.value.first_name;
    member.surname = this.inviteForm.value.last_name;
    member.email = this.inviteForm.value.email;
    member.imagePath = "/assets/img/member/avatar.png";
    member.team = JSON.parse(localStorage.getItem("team")).id;
    // Same for the level, it's a bit dirty.
    member.level = (this.inviteForm.value.role === "Guest") ? 3 : this.inviteForm.value.level;

    this.userService.checkIfEmailExists(member).pipe(catchError((err => {
      return throwError(err);
      }))).subscribe((data) => {
        console.log(data);
      if (data > 0) {
        // Go through all the users of this team, and check if the email for the new member is already part of one of
        // the users
        this.userService.users.map((user) => {
          if (user.email === member.email) {
            document.getElementById("danger-message--form")!.innerHTML= "This user is already part of your team!";
            return false;
          } else {
            // Invite an existing user to your team
            this.userService.inviteExisting(member).subscribe((member) => {
              document.getElementById("succes-message--form").innerHTML= "The user was invited!"
              this.userService.invitedUsers.push(member.email);
            }, (error => {
              console.log(error)
            }));
            return true;
          }
        })
      } else {
        // Once youre sure the user doesn't already exist, create an invite for a new user
        this.userService.inviteNew(member).subscribe((member) => {
          document.getElementById("succes-message--form").innerHTML= "The user was invited!"
          this.userService.invitedUsers.push(member.email)
        }, (error => {
          console.log(error)
        }));
      }
    })

    return true;
  }

  /**
   * Delete an user by their id
   * @param id
   */
  deleteById(id: number): boolean {
    this.userService.deleteById(id).subscribe((member) => {
      let id = this.userService.users.indexOf(member);
      this.userService.getUsersArray().splice(id, 1)
    })
    return true;
  }

  /**
   * Check if an user has an image, otherwise just do a default image path
   * @param user
   */
  defineImagePath(user: User): string {
    if (user.imagePath === "nan" || user.imagePath === null || user.imagePath === "null")
      return `/assets/img/member/avatar.png`;
    return user.imagePath;
  }

  /**
   * Get all the roles from the database and push it into the array of roles
   */
  getAllRoles(): void {
    this.http.get<any[]>(`${this.environment.api}/api/rest/role`).subscribe((data) => {
      data.forEach((roleName) => {
        this.roles.push(roleName.name);
      })
    })
  }

  /**
   * Delete an userTeam
   */
  deleteUserFromTeam(): void {
    this.http.delete<any>(`${this.environment.api}/userTeam/delete/${this.userService.selectedUser.id}
    /${JSON.parse(localStorage.getItem("team")).id}`).subscribe(() => {
      // Delete user from local array
      let index = this.userService.users.indexOf(this.userService.selectedUser);
      this.userService.users.splice(index, 1);
    })
  }
}
