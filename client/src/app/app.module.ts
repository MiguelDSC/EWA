import { NgModule } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { TeamComponent } from './components/team/team.component';
import { UserComponent } from './components/team/user/user.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { DataPageComponent } from './components/data-page/data-page.component';
import { DataPageViewComponent } from './components/data-page/data-page-view/data-page-view.component';
import { DataPageEditComponent } from './components/data-page/data-page-edit/data-page-edit.component';
import { SettingsComponent } from './components/settings/settings.component';
import { NotePageComponent } from './components/note-page/note-page.component';
import { NoteOverviewComponent } from './components/note-page/note-overview/note-overview.component';
import { NoteDetailComponent } from './components/note-page/note-detail/note-detail.component';
import { AuthenticateComponent } from './components/authentication/register/authenticate.component';
import { LoginComponent } from './components/authentication/login/login.component';
import { LogoutComponent } from './components/authentication/logout/logout.component';
import { AdminDashboardComponent } from './components/dashboard/admin-dashboard/admin-dashboard.component';
import { GreenhouseService } from './service/greenhouse-data-service/greenhouse.service';
import { filterdNotesPipe } from './components/note-page/note-overview/filterdNotesPipe';
import { AuthInterceptor } from './service/authorization/auth.interceptor';

import { AdminManagementComponent } from './components/admin/admin-management/admin-management.component';

import { DataRawComponent } from './components/data-page/data-raw/data-raw.component';
import { TeamSwitchComponent } from './components/team-switch/team-switch.component';
import { NotFoundComponent } from './components/error/not-found/not-found.component';
import { JwtHelperService, JWT_OPTIONS } from "@auth0/angular-jwt";
import { InvitedUsersComponent } from './components/team/invited-users/invited-users.component';


@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    DashboardComponent,
    DataPageComponent,
    DataPageViewComponent,
    DataPageEditComponent,
    SettingsComponent,
    NavbarComponent,
    TeamComponent,
    UserComponent,
    NotePageComponent,
    NoteOverviewComponent,
    NoteDetailComponent,
    LoginComponent,
    AuthenticateComponent,
    LogoutComponent,
    AdminDashboardComponent,
    filterdNotesPipe,
    LogoutComponent,

    AdminManagementComponent,

    DataRawComponent,
    TeamSwitchComponent,
    NotFoundComponent,
    InvitedUsersComponent,

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule.
    withConfig(
      {warnOnNgModelWithFormControl:
        'never'}),
    HttpClientModule,
    AppRoutingModule,
    ReactiveFormsModule,
  ],
  providers: [
    GreenhouseService,
    CookieService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    {
      provide: JWT_OPTIONS,
      useValue: JWT_OPTIONS
    },
    JwtHelperService
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
