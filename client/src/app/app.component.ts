// @ts-ignore

import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthorizationService } from './service/authorization/authorization.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent {
  constructor(public authService: AuthorizationService, private router: Router) {
  }

  title = 'climate-cleanup';

  show() {
     // now I might be wrong.. but isn't it better to hide the navbar only when
     // we're on the login page??
     if ( this.router.url.startsWith("/login")
          || this.router.url.startsWith("/register")
          || this.router.url.startsWith("/team-select")
        ) {
       return false;
     }

     return true;
  }
}
