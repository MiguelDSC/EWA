import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { shareReplay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private http: HttpClient, private router: Router, private cookie: CookieService) {
  }

  public login(form: any): Observable<any> {
    console.log(form);
    return this.http.post(`${environment.api}/api/auth/login`, form, { observe: 'response' });
  }


  public checkLoginValid(data: any): boolean {
    if(data.status < 200 || data.status >= 300) {
      return false;
    }

    console.log("Data", data);
    let token = data['headers'].get('Authorization');

    if (!token) return false;

      if (data.body.team != null)
        localStorage.setItem("team", JSON.stringify(data.body.team));
      localStorage.setItem("user", data.body.user.id);

    if (data.body.team != null)
      localStorage.setItem('team', data.body.team.name);
    localStorage.setItem('user', data.body.user.id);

    token = token.replace('Bearer ', '');
    localStorage.setItem('token', token);
    this.router.navigate(["/team-selection"])

    return true;
  }


}


