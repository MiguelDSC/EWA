import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {UserService} from "../../../service/member/user.service";
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {User} from "../../../models/user.model";
import {Observable} from 'rxjs';
import {TeamService} from "../../../service/team/team.service";
import {Team} from "../../../models/team";
import {GreenhouseInterface} from "../../../interfaces/greenhouse";
import {GreenhouseService} from "../../../service/greenhouse/greenhouse.service";

@Component({
  selector: 'app-admin-management',
  templateUrl: './admin-management.component.html',
  styleUrls: ['./admin-management.component.scss']
})
export class AdminManagementComponent implements OnInit {

  inviteForm!: FormGroup;
  editForm!: FormGroup;
  roles: string[] = ["Agronomist", "Botanist", "Geologist", "Hydrologist", "Climate Scientist", "Guest"];
  teams: Team[];
  teamForm: FormGroup;
  greenhouses: GreenhouseInterface[];
  editingTeam: Team;
  teamEditingForm: FormGroup;
  currentTeamUsers: User[];
  currentGreenhouseId: number;


  constructor(public userService: UserService, private fb: FormBuilder, private router: Router,
              private ref: ChangeDetectorRef, private http: HttpClient, private teamService: TeamService,
              private greenhouseService: GreenhouseService) {
  }

  ngOnInit(): void {
    this.initializeForm();
    this.getTeams();
    this.getGreenhouses();
    this.userService.getUsersAll();
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

  changeSelectedIdAndModal(id: number): void {
    this.userService.selectedUser = this.userService.findById(id)

    this.userService.selectedUserId = this.userService.selectedUser.id;
    this.router.navigate(['admin-management', this.userService.selectedUserId]);

    // TEMP FIX, THIS WILL GET FIXED
    let button = document.getElementById("editButtonTeamAdmin");
    setTimeout(() => {
      button.click();
    }, 100);
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
      teamleader: "",
    });

    this.editForm = this.fb.group({
      role: ""
    });
    this.teamForm = this.fb.group({
      team_name: "",
      greenhouse: ""
    });
    this.teamEditingForm = this.fb.group({
        greenhouse_edit: "",
        team_name_edit: ""
      }
    )
  }


  onEditSubmit(id: number): boolean {
    let tempMember = this.userService.findById(id);

    tempMember.role = this.editForm.value.role;
    // Update
    this.userService.updateUser(tempMember).subscribe((member) => {
      this.userService.getUsers().subscribe((members) => {
        console.log(members)
      });
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
    document.getElementById("succes-message--form")!.innerHTML = "";

    // Loop over values of reactive form
    for (const [key, value] of Object.entries(this.inviteForm.value)) {
      document.getElementById(key + "--error")!.innerHTML = "";
      if (!value && key !== "teamleader") {
        document.getElementById(key + "--error")!.innerHTML = "Value is empty";
        return false;
      }
    }

    //Dirt fix for id right now, will be changed when members come frmo database
    let members = this.userService.getUsersArray();

    // Add new member
    let member = new User();
    member.id = 0;
    member.role = this.inviteForm.value.role;
    member.firstname = this.inviteForm.value.first_name;
    member.surname = this.inviteForm.value.last_name;
    member.email = this.inviteForm.value.email;
    console.log(member);

    let o = this.inviteForm.value.teamleader ? this.userService.inviteTeamLeader(member) : this.userService.inviteNew(member);

    // Add
    o.subscribe((member) => {
      this.userService.invitedUsers.push(member.email)
      console.log(this.userService.getInvitedUsersArray())
    }, (error => {
      console.log(error)
    }));

    document.getElementById("succes-message--form")!.innerHTML = "Invite sent"
    return true;
  }

  deleteById(id: number): boolean {
    this.userService.deleteById(id).subscribe((member) => {
      let id = this.userService.users.indexOf(member);
      this.userService.getUsersArray().splice(id, 1)
    })
    return true;
  }

  getTeams() {
    this.teamService.getAll().subscribe((data) => {
      this.teams = data;
    })
  }

  onTeamSubmit() {
    document.getElementById("team_name--error").innerHTML = "";
    document.getElementById("greenhouse--error").innerHTML = "";
    if (this.teamForm.value["team_name"] == "") {
      document.getElementById("team_name--error").innerHTML = "A name for the team is required";
      return;
    }
    if (this.teamForm.value["greenhouse"] == "") {
      document.getElementById("greenhouse--error").innerHTML = "Please enter a valid greenhouse";
      return;
    }
    let thing: GreenhouseInterface = {
      airTemp: 0,
      color: "",
      groundTemp: 0,
      "greenhouseId": Number(this.teamForm.value["greenhouse"])
    }
    let team = new Team(this.teamForm.value["team_name"], 0, thing);
    console.log(team);
    this.teamService.addTeam(team).subscribe((data) => {
      if (data !== null) {
        this.getTeams();
        this.getGreenhouses();
        this.teamForm.get("greenhouse").setValue("");
      }
    });

  }

  getGreenhouses() {
    this.greenhouseService.getAll().subscribe((data) => {
      this.greenhouses = data;
    })
  }

  onEditTeam(team: Team) {
    console.log(team.greenhouse);
    this.editingTeam = team;
    let button = document.getElementById("editButtonTeam");
    setTimeout(() => {
      button.click()
    }, 100);
    this.teamService.getAllUsers(team.id).subscribe((data) => {
      this.currentTeamUsers = data;
    });
    console.log(this.currentTeamUsers);
    console.log(team);
    if (team.greenhouse != null) {
      this.currentGreenhouseId = team.greenhouse.greenhouseId;
      this.teamEditingForm.get("greenhouse_edit").setValue(this.editingTeam.greenhouse.greenhouseId)
    } else {
      this.currentGreenhouseId = -1;
    }
  }

  onEditTeamSubmit(): boolean {
    document.getElementById("team_name_edit--error").innerHTML = "";
    if (this.teamEditingForm.value["team_name_edit"] == "") {
      document.getElementById("team_name_edit--error").innerHTML = "A name for the team is required";
      return false;
    } else {
      let thing: GreenhouseInterface = {
        airTemp: 0,
        color: "",
        groundTemp: 0,
        "greenhouseId": Number(this.teamEditingForm.value["greenhouse_edit"])
      }
      console.log(this.teamEditingForm.value);
      let team = new Team(this.teamEditingForm.value["team_name_edit"], this.editingTeam.id, thing);
      console.log(team)
      this.teamService.updateTeam(team).subscribe((data) => {
        if (data.id != null) {
          this.getTeams();
          this.getGreenhouses();
        }
      });
      return true;
    }
  }

  onEditDelete(id: number) {
    this.teamService.deleteTeam(id).subscribe((data) => {
      if (data != null) {
        this.getTeams();
        this.getGreenhouses();
      }
    });
  }

  greenhousesAvailable() {
    if(this.greenhouses==null){
      return false;
    }
    else {
      return this.greenhouses.length!=0;
    }
  }
}

