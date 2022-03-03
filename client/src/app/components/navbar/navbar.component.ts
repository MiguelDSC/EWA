import { AfterViewInit, Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { environment } from 'src/environments/environment';
import { SettingsService } from '../../service/settings/settings.service';
import { AuthorizationService } from '../../service/authorization/authorization.service';
import { NavigationEnd, Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, AfterViewInit {
  environment = environment;
  image: SafeUrl;

  constructor(
    private settingService: SettingsService,
    private sanitizer: DomSanitizer,
    private authService: AuthorizationService,
    private router: Router
  ) { }

  ngAfterViewInit(): void {
    this.fetchProfilePicture()
  }

  ngOnInit(): void {
    let sub = this.router.events.subscribe((val) => {
      if (val instanceof NavigationEnd) {
        const v: NavigationEnd = val as NavigationEnd
        if (this.authService.isLoggedIn() && !this.image) {
          this.fetchProfilePicture()
        }
      }
    })
  }

  fetchProfilePicture() {
    this.settingService.getProfilePicture(parseInt(localStorage.getItem("user"))).subscribe(x => {
      this.image = this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(x));
    });
  }
}
