import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { AuthorizationService } from '../../../service/authorization/authorization.service';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.scss']
})
export class LogoutComponent implements OnInit {

  constructor(private cookieService: CookieService, private router: Router, private authService: AuthorizationService) { }

  ngOnInit(): void {
    this.authService.logOut();
    this.router.navigate(['login']);
  }
}
