import {ComponentFixture, fakeAsync, inject, TestBed, tick} from '@angular/core/testing';

import { TeamComponent } from './team.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {UserService} from "../../service/member/user.service";
import {User} from "../../models/user.model";
import {JwtHelperService, JWT_OPTIONS} from "@auth0/angular-jwt";
import {FormBuilder, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Router} from "@angular/router";
import {AppRoutingModule} from "../../app-routing.module";
import {BrowserModule, By} from "@angular/platform-browser";
import {AuthorizationService} from "../../service/authorization/authorization.service";
import {Observable, of} from "rxjs";
import {Component, OnInit} from "@angular/core";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {Team} from "../../models/team";
import {last} from "rxjs/operators";

@Component({
  selector: 'app-invited-users',
  template: '',
})
class FakeInviteComponent{

}

fdescribe('TeamComponent', () => {
  let component: TeamComponent;
  let fixture: ComponentFixture<TeamComponent>;
  let componentHtml = HTMLElement;

  function sendInput(inputElement: any, text: string) {
    inputElement.value = text;
    inputElement.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    return fixture.whenStable();
  }

  beforeEach( () => {
    // Create fakes, and turn off the functions that request a JWT, since we dont want to generate one right now
    let fakeAuthService = jasmine.createSpyObj<AuthorizationService>('AuthorizationService', {
      isAdmin: of(true)
    })
    let fakeUserService = jasmine.createSpyObj<UserService>('UserService', {
      getUsersByTeam: undefined,
      retrieveInvitedUsers: undefined,
      updateUserRole: undefined,
      findById: undefined,
    })

    TestBed.configureTestingModule({
      declarations: [
        TeamComponent, FakeInviteComponent
      ],
      imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        ReactiveFormsModule.
        withConfig(
          {warnOnNgModelWithFormControl:
              'never'}),
        HttpClientTestingModule,
        HttpClientModule,
        AppRoutingModule,
        ReactiveFormsModule,
      ],
      providers: [
        [{provide: UserService, useValue: fakeUserService}],
        [{provide: AuthorizationService, useValue: fakeAuthService}],
        [JwtHelperService, {provide: JWT_OPTIONS, useValue: JWT_OPTIONS}]
      ],
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TeamComponent);
    component = fixture.componentInstance;
    componentHtml = fixture.debugElement.nativeElement;
    fixture.detectChanges();

    // Set users for the userservice
    let user =new User();
    user.setMemberValues({
      "id": 1,
      "firstname": "Bart",
      "surname": "Salfischberger",
      "email": "bart@hva.nl",
      "imagePath": "nan",
      "role": 4,
      "role_name": "Agronomist",
      "password": "null",
      "level": 1,
      'team': 1,
    })
    component.userService.users = [];
    component.userService.users.push(user);
  });

  it('Should correctly check if the user gets the default image if they have no image path',
    () => {
      // Arrange (Getting the UI components)
      // We detect changes here because an user is pushed to the fake service
      fixture.detectChanges();
      const userImage: HTMLInputElement = fixture.debugElement.nativeElement.querySelector('#profile-picture-1');
      console.log(userImage);
      const userImageSrc = userImage.src;

      // Act (We get the user image here
      const userImageSrcPath = userImageSrc.substr(userImageSrc.indexOf("/assets"));

      // Assert, is the image here the right one
      expect(userImageSrcPath).toEqual("/assets/img/member/avatar.png");
    });

  it('Should should an error when you can not invite someone', fakeAsync(() => {
    // Arrange (Getting the UI components)
    // We detect changes here because an user is pushed to the fake service
    fixture.detectChanges();

    const firstName = fixture.debugElement.nativeElement.querySelector('#first_name--team_form');
    const inviteButton = fixture.debugElement.nativeElement.querySelector('#modal-invite__submit-button');

    // Set the roles in the teamcomponent
    const teamComponent = fixture.debugElement.injector.get(TeamComponent);
    teamComponent.roles = ["Hyrdologist", "Agronomist"];

    // spyOn(teamComponent, 'onInviteSubmit').and.callThrough();

    // Set the vallues, (this is still arranging)
    sendInput(firstName, "bjoris").then(() => {
      inviteButton.click();

      fixture.detectChanges();

      const lastNameError = fixture.debugElement.nativeElement.querySelector('#last_name--error');
      expect(lastNameError.innerHTML).toBe("Value is empty");
    })
  }));
});

