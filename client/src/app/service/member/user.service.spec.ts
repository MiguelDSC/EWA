import { TestBed, async } from '@angular/core/testing';

import { UserService } from './user.service';
import {HttpClient, HttpClientModule, HttpErrorResponse} from "@angular/common/http";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {asyncScheduler, of} from "rxjs";
import {JWT_OPTIONS, JwtHelperService} from "@auth0/angular-jwt";
import {User} from "../../models/user.model";
import {UserTeam} from "../../models/userTeam";
import {environment} from "../../../environments/environment";
import {local} from "d3";

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController
  let _urlRaw: string = `${environment.api}`;
  let _url: string = `${environment.api}/user`;


  beforeEach(async (() => {
    TestBed.configureTestingModule({
      providers: [JwtHelperService, {provide: JWT_OPTIONS, useValue: JWT_OPTIONS}],
      imports: [HttpClientModule, HttpClientTestingModule]
    })
      .compileComponents();
    service = TestBed.inject(UserService)
    httpMock = TestBed.inject(HttpTestingController);
    service.users = []
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
    //Act
    service.users.push(user);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('Should give me back 1 user', () => {
    // Arrange

    // Assert
    expect(service.findAll().length).toEqual(1);
  })

  it("Inviting an empty user should give me back an error", () => {
    //Arrange
    let user = new User();

    service.inviteNew(user).subscribe((res: User) => {console.log(res)}, (err) => {
      console.log(err);
      expect(err).toBeTruthy();
    });

    const req = httpMock.expectOne(`${_urlRaw}/invite/new`);
    expect(req.request.method).toBe("POST");

    req.flush({
      type: 'ERROR',
      status: 400
    });
  })

  it("Deleting an user should be making a delete request", () => {
    // Arrange
    let user = new User();
    user.id = 1;
    // localStorage.setItem('team', JSON.stringify({"id":3,"name":"Team Utrecht","greenhouse":null}));

    //Act
    service.deleteById(user.id).subscribe((res) => {console.log(res)}, (err => {
      expect(err).toBeTruthy();
    }));

    const req = httpMock.expectOne(`${_url}/1`);
    expect(req.request.method).toBe("DELETE");

    req.flush({
      type: 'SUCCES',
      status: 200,
    })

    httpMock.verify();
  })

  it("Testing an error for delete by id", () => {
    // Arrange
    let user = new User();
    user.id = 1;

    // Set error codes
    let actualError: HttpErrorResponse | undefined;
    const status = 500;
    const statusText = 'Server error';
    const errorEvent = new ErrorEvent('API error');

    //Act
    service.deleteById(user.id).subscribe(() => {
      fail("next handler must not be called");
    }, (error) => {
      actualError = error;
    }, () => {
      fail('Final handler should not be called')
    })

    const req = httpMock.expectOne(`${_url}/1`);
    req.error(errorEvent, {
      status, statusText
    })

    if(!actualError) {
      throw new Error('error needs to be defined');
    }

    // Assert: Check if the errors are right
    expect(actualError.error).toBe(errorEvent);
    expect(actualError.status).toBe(status);
    expect(actualError.statusText).toBe(statusText);
  });

  it("Check if email exists", () => {
    // Arrange
    let user = service.users[0];

    // Set error codes
    let actualError: HttpErrorResponse | undefined;
    const status = 500;
    const statusText = 'Server error';
    const errorEvent = new ErrorEvent('API error');

    //Act
    service.checkIfEmailExists(user).subscribe(() => {
      fail("next handler must not be called");
    }, (error) => {
      actualError = error;
    }, () => {
      fail('Final handler should not be called')
    })

    const req = httpMock.expectOne(`${_url}/email/check`);
    req.error(errorEvent, {
      status, statusText
    })

    if(!actualError) {
      throw new Error('error needs to be defined');
    }

    // Assert: Check if the errors are right
    expect(actualError.error).toBe(errorEvent);
    expect(actualError.status).toBe(status);
    expect(actualError.statusText).toBe(statusText);
  })
});
