import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { LoginService } from 'src/app/service/authentication/login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginError: boolean = false;

  constructor(public fb: FormBuilder, public router: Router, public cookieService: CookieService, public loginService: LoginService) {
  }

  form: FormGroup = this.fb.group({
    email: [, [Validators.required]],
    password: [, [Validators.required]]
  });

  ngOnInit(): void {
    this.cookieService.set('loggedin', 'false');

    console.log(localStorage.getItem('key') == null);

    document.body.classList.add('login-body');
  }

  ngOnDestroy(): void {
    document.body.classList.remove('login-body');
  }

  onSubmit() {
    this.loginService.login(this.form.getRawValue()).subscribe(data => {
      this.loginError = false;
      this.form.controls['password'].reset();
      this.loginService.checkLoginValid(data);
    }, e => {
      console.log('Login invalid');
      this.loginError = true;
      this.form.controls['password'].reset();
      this.form.controls['password'].markAsDirty();
    });
  }
}
