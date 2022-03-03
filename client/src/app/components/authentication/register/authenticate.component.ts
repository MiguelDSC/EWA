import { FormBuilder, FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Component, OnInit, OnDestroy, AfterContentInit } from '@angular/core';
import { PasswordvalidationService} from '../../../service/passwordValidation/passwordvalidation.service';
import { PasswordValidator} from '../../../Validators';
import { CookieService } from 'ngx-cookie-service';
import {ActivatedRoute, Router} from "@angular/router";
import {RegisterService} from "../../../service/member/register.service";
import {User} from "../../../models/user.model";
import {UserService} from "../../../service/member/user.service";
import * as d3 from "d3";


@Component({
  selector: 'app-authenticate',
  templateUrl: './authenticate.component.html',
  styleUrls: ['./authenticate.component.scss']
})
export class AuthenticateComponent implements OnInit {
  formRegister: FormGroup;
  submitted = false;
  userInfo: User;
  hash: string;

  constructor(private fb: FormBuilder,  private customValidator: PasswordvalidationService,
              public router: Router, private activatedRoute: ActivatedRoute,
              private registerService: RegisterService) {
    this.activatedRoute.paramMap.subscribe(params => {
      this.hash = params.get("hash")
      this.registerService.getUser(this.hash).subscribe((data) => {
        this.userInfo = data.result.user;
        this.userInfo.role = data.result.role;
        this.userInfo.team = data.result.team;
      });
    })
  }
  //
  ngOnInit(): void {
    document.body.classList.add('login-body');
    this.formRegister = this.fb.group({
        password: ['', Validators.compose([Validators.required, this.customValidator.patternValidator()])],
        confirmPassword: ['', Validators.compose([Validators.required])]
    },{
      validator: PasswordValidator('password', 'confirmPassword')
    });
  }

  ngOnDestroy(): void {
    document.body.classList.remove('login-body');
  }

  onSubmit() {
    this.submitted = true;
    if (this.formRegister.valid) {
      // Set member and invite
      this.userInfo.password = this.formRegister.value.password
      let member = new User();
      member.setMemberValues(this.userInfo);


      this.registerService.add(member, this.hash).subscribe((data) => {
        // this.memberService.deleteByHash(member.url_to_hash).subscribe((data) => {
        //
        // })
      })
    }
    this.generateAlert('User has been registered, redirecting to the login page!',
      'alert-success'
    );
    setTimeout(() => {
      this.router.navigate(['/login'])
    }, 3000);

  }

  get registerFormControl() {
    return this.formRegister.controls;
  }

  generateAlert(text: string, type: string) {
    let alertBox = d3.selectAll('.alert-box');
    alertBox.selectAll('*').remove();
    alertBox
      .append('div')
      .attr('class', 'alert show fade alert-dismissible ' + type)
      .text(text)
      .append('button')
      .attr('type', 'button')
      .attr('data-bs-dismiss', 'alert')
      .attr('class', 'btn-close');
    setTimeout(() => {
      alertBox.selectAll('*').remove();
    }, 5000);
  }

  password(formGroup: FormGroup) {
    const { value: password } = formGroup.get('password');
    const { value: confirmPassword } = formGroup.get('confirmpassword');
    return password === confirmPassword ? null : { passwordNotMatch: true };
  }

  PasswordValidator(controlName: string, matchingControlName: string){
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];
      if (matchingControl.errors && !matchingControl.errors.confirmedValidator) {
        return;
      }
      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ confirmedValidator: true });
      } else {
        matchingControl.setErrors(null);
      }
    };
  }

  /**
   * Accept the invite as an existing user
   */
  acceptInvite(): void {
    this.registerService.deleteInvite(this.hash).subscribe(() => {
      this.router.navigate(['team']);
    })
  }

  /**
   * Decline the invite and delete the invite and the user team entry
   */
  declineInvite(): void {
    this.registerService.deleteInviteAndUserTeam(this.hash).subscribe(() => {
      this.router.navigate(['team']);
    })
  }
}


