import { TestBed } from '@angular/core/testing';

import { AuthorizationService } from './authorization.service';
import {HttpClient, HttpClientModule} from "@angular/common/http";
import {UserService} from "../member/user.service";
import {JwtHelperService} from "@auth0/angular-jwt";

describe('AuthorizationService', () => {
  let service: AuthorizationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ AuthorizationService ],
      imports: [JwtHelperService],
      providers: [HttpClientModule, JwtHelperService]
    });
    service = TestBed.inject(AuthorizationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
