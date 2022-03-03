import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {User} from "../../models/user.model";
import {catchError} from "rxjs/operators";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  public registerLinks = {};
  private _url: string =environment.api + "/invite";
  private _urlRaw: string = environment.api;
  constructor(private http: HttpClient) {

  }

  ngOnInit() {

  }

  add(member: User, hash: string): Observable<User> {
    return this.http.put<User>(`${this._urlRaw}/api/auth/register?hash=${hash}`, {
      password: member.password
    }, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    }).pipe(catchError((err) => {
      console.log(err);

      return throwError(err);
    }))
  }

  getUser(hash: string): Observable<any> {
    return this.http.get<any>(`${this._url}/user/${hash}/`);
  }

  // Delete an invite
  deleteInvite(hash: string): Observable<any> {
    return this.http.delete(`${this._urlRaw}/invite/accepted/${hash}`);
  }

  // Delete an invite and the userteam
  deleteInviteAndUserTeam(hash: string): Observable<any> {
    return this.http.delete(`${this._urlRaw}/invite/declined/${hash}`);
  }

  getInvite(hash: string): Observable<any> {
    return this.http.get<any>(`${this._url}/${hash}`)
  }

  joinTeamAsUser(id: number): Observable<any> {
    return this.http.post<any>(`${this._url}/${id}`, id);
  }
}
