import { Component, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { DataPageComponent } from './components/data-page/data-page.component';
import { SettingsComponent } from './components/settings/settings.component';
import { TeamComponent } from './components/team/team.component';
import { UserComponent } from './components/team/user/user.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { NotePageComponent } from './components/note-page/note-page.component';
import { LoginComponent } from './components/authentication/login/login.component';
import { AuthenticateComponent } from './components/authentication/register/authenticate.component';
import { AuthGuard } from './guards/auth.guard';
import { CanRegisterGuard } from './guards/can-register.guard';
import { LogoutComponent } from './components/authentication/logout/logout.component';
import { NoteDetailComponent } from './components/note-page/note-detail/note-detail.component';
import { NoteOverviewComponent } from './components/note-page/note-overview/note-overview.component';
import { AdminDashboardComponent } from './components/dashboard/admin-dashboard/admin-dashboard.component';

import { AdminManagementComponent } from './components/admin/admin-management/admin-management.component';

import { DataRawComponent } from './components/data-page/data-raw/data-raw.component';
import { TeamSwitchComponent } from './components/team-switch/team-switch.component';
import { NotFoundComponent } from './components/error/not-found/not-found.component';
import { TeamGuard } from './guards/team.guard';


const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: "full" },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard, TeamGuard] },
  { path: 'dashboardadmin', component: AdminDashboardComponent, canActivate: [AuthGuard] },
  { path: 'team-selection', component: TeamSwitchComponent, canActivate: [AuthGuard] },
  { path: 'data/:set/:time', component: DataPageComponent, canActivate: [AuthGuard, TeamGuard] },
  { path: 'data/raw', component: DataRawComponent, canActivate: [AuthGuard, TeamGuard] },
  {
    path: 'team',
    component: TeamComponent,
    canActivate: [AuthGuard, TeamGuard],
    children: [
      {
        path: ':id',
        component: UserComponent,
      },
    ],
  },
  { path: 'settings', component: SettingsComponent, canActivate: [AuthGuard, TeamGuard] },
  {
    path: 'note-page',
    component: NoteOverviewComponent,
    canActivate: [AuthGuard, TeamGuard],

  },
  { path: 'admin-management', component: AdminManagementComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: ':id',
        component: UserComponent,
      },
    ],},
  { path: 'login', component: LoginComponent },
  { path: 'logout', component: LogoutComponent },
  { path: 'register/:hash', component: AuthenticateComponent},
  { path: '**', component: NotFoundComponent, canActivate: [AuthGuard] }
];

@NgModule({
  imports: [RouterModule.forRoot(routes), FormsModule],
  exports: [RouterModule],
})
export class AppRoutingModule {}
