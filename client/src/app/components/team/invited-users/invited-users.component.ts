import { Component, OnInit } from '@angular/core';
import {UserService} from "../../../service/member/user.service";
import {AuthorizationService} from "../../../service/authorization/authorization.service";
import {User} from "../../../models/user.model";

@Component({
  selector: 'app-invited-users',
  templateUrl: './invited-users.component.html',
  styleUrls: ['./invited-users.component.scss']
})
export class InvitedUsersComponent implements OnInit {

  constructor(public userService: UserService, public authService: AuthorizationService) { }

  ngOnInit(): void {
    let currentTeam = JSON.parse(localStorage.getItem("team"))
    this.userService.retrieveInvitedUsers(currentTeam.id);
  }

}
