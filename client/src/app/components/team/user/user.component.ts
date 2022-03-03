import { Component, OnInit, Input, SimpleChanges } from '@angular/core';
import {UserService} from "../../../service/member/user.service";
import {User} from "../../../models/user.model";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-member',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {

  selectedUser!: User;

  constructor(public memberService: UserService, private router: Router, private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    // Checks if there is an ID to get an user
    this.activatedRoute.paramMap.subscribe(params => {
      let memberId = params.get('id')
      if (memberId != null) {
        this.memberService.selectedUserId = parseInt(memberId);
        this.memberService.selectedUser = this.memberService.findById(this.memberService.selectedUserId);
        this.selectedUser = this.memberService.selectedUser;
      }
    })
  }

}
