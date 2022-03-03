import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {JwtHelperService} from "@auth0/angular-jwt";
import {CookieService} from 'ngx-cookie-service';
import {Observable} from 'rxjs';
import {share} from 'rxjs/operators';
import {environment} from '../../../environments/environment';
import {User} from "../../models/user.model";

@Injectable({
  providedIn: 'root'
})
export class AuthorizationService {

  currentUser: User = null;

  constructor(private cookieService: CookieService, private httpClient: HttpClient, private jwtService: JwtHelperService) {
    this.updateUserInformation();
  }

  //private token: string = "eyJhbbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImtlZXMifQ.LGSX9Jc_bLy6QKoHs4a1Qz-2eowLNF1Bytu1Bg6ILCQ";

  getToken(): string {
    const token = localStorage.getItem('token');
    return token;
  }

  refreshToken(): Observable<any> {

    const observable = this.httpClient.post(`${environment.api}/api/auth/login/refresh`, {}, {
      headers: new HttpHeaders({Authorization: this.getToken()}),
      observe: 'response'
    }).pipe(share());

    observable.subscribe(data => {

        let refreshedToken = data['headers'].get('Authorization');

        if (refreshedToken == null) {
          throw new Error('token was not present in the response');
        }

        refreshedToken = refreshedToken.replace('Bearer ', '');

        localStorage.setItem('token', refreshedToken);

        this.updateUserInformation();
      },
      (err) => {
        this.logOut();
      });
    return observable;
  }

  logOut(): void {
    localStorage.clear();
  }

  isLoggedIn() {
    return (this.getToken() != null);
  }

  public isAdmin(): Observable<any> {
    return this.httpClient.get(`${environment.api}/user/level/${localStorage.getItem("user")}/
            ${JSON.parse(localStorage.getItem("team")).id}`);
  }

  // TODO
  private updateUserInformation(): void {

    if (this.getToken()) {

      const decodedToken = this.jwtService.decodeToken(this.getToken());

      this.currentUser = new User();
      this.currentUser.id = decodedToken.jti;
      this.currentUser.team = decodedToken.team;
    } else {
      this.currentUser = null;
    }

  }

}
