import { Injectable } from '@angular/core';
import {User} from "../../models/user.model";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {catchError} from "rxjs/operators";
import {CookieService} from "ngx-cookie-service";
import { map } from 'rxjs/operators';
import {Role} from "../../models/role.model";
import {environment} from "../../../environments/environment";
import {Note} from "../../models/note";
import {UserTeam} from "../../models/userTeam";
import jwt_decode from "jwt-decode";
import {AuthorizationService} from "../authorization/authorization.service";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  public users: User[];
  public invitedUsers: string[];
  public selectedUser?: User;
  public selectedUserId!: number;

  private _url: string = `${environment.api}/user`;
  private _urlRaw: string = `${environment.api}`;
  private decodedToken: any;

  constructor(private http: HttpClient, public cookie: CookieService, private auth: AuthorizationService) {
    this.users = [];
    this.invitedUsers = [];
    if (localStorage.getItem('token') != null) {
       this.decodedToken = jwt_decode(localStorage.getItem('token'));
    }
  }

  findAll(): User[] {
    return this.users;
  }

  // Invite a new user to the team
  inviteNew(member: User): Observable<User> {
    return this.http.post<User>(`${this._urlRaw}/invite/new`, JSON.stringify(member), {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    }).pipe(catchError((err) => {
      console.log(err);

      return throwError(err);
    }))
  }

  // Invite a new user to the team
  inviteExisting(member: User): Observable<User> {
    return this.http.post<User>(`${this._urlRaw}/invite/existing`, JSON.stringify(member), {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    }).pipe(catchError((err) => {
      console.log(err);

      return throwError(err);
    }))
  }

  inviteTeamLeader(member: User): Observable<User> {
    return this.__invite(member, "/inviteTeamLeader/")
  }

  private __invite(member: User, url: string): Observable<User> {
    return this.http.post<User>(`${this._urlRaw}/invite/${url}`, JSON.stringify(member), {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    }).pipe(catchError((err) => {
      console.log(err);

      return throwError(err);
    }))
  }

  /**
   * Updates the user
   * @param member
   */
  updateUser(member: User): Observable<User> {
    return this.http.put<User>(this._url, JSON.stringify(member), {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    }).pipe(catchError((err) => {
      console.log(err);

      return throwError(err);
    }))
  }

  /**
   * Update only the user role in the specific team
   * @param member
   */
  updateUserRole(member: User): Observable<User> {
    return this.http.put<User>(`${this._urlRaw}/userTeam/update/${member.id}/${JSON.parse(localStorage.getItem("team")).id}`, member.role, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    }).pipe(catchError((err) => {
      console.log(err);

      return throwError(err);
    }))
  }

  getUrl(): string {
    return this._url;
  }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this._url);
  }

  // Get users in the same team as the current user their team
  getUsersByTeam(): void {
   this.http.get<any[]>(`${this._url}/team/${JSON.parse(localStorage.getItem("team")).id}`).subscribe((data) => {
     // Empty the array so the users don't add up
     this.users = [];
     data.forEach((data) => {
       let newUser = new User();
       newUser = data[0];
       newUser.role = data[1].id
       newUser.role_name = data[1].name;
       console.log(newUser);
       this.users.push(newUser);
     })
   });
  }

  getUsersAll(): void {
    this.http.get<User[]>(`${this._url}/all/`).subscribe((data) => {
      this.users = data;
    });
  }

  checkIfEmailExists(user: User): Observable<any> {
      return this.http.post(`${this._url}/email/check`, JSON.stringify(user), {
        headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }

  getMembersBE(): Observable<User[]> {
    return this.http.get<User[]>("http://localhost:8080/admin/member").pipe(map(data => {
      if ('status' in data) {
        const error: any = data;
        throw new HttpErrorResponse({
          error: error['message'],
          status: error['status']
        })
      }
      return data;
    }));
  }

  getTeamMembers(): Observable<User[]> {
    return this.http.get<User[]>("http://localhost:8080/admin/member/teamleader").pipe(map(data => {
      if ('status' in data) {
        const error: any = data;
        throw new HttpErrorResponse({
          error: error['message'],
          status: error['status']
        })
      }
      return data;
    }));
  }

  getApiCallPerTeam(): Observable<User[]> {
    return this.http.get<User[]>("http://localhost:8080/admin/apicall/today/team").pipe(map(data => {
      if ('status' in data) {
        const error: any = data;
        throw new HttpErrorResponse({
          error: error['message'],
          status: error['status']
        })
      }
      return data;
    }));
  }

  findById(memberId: number): User {
    return <User>this.users.find(member => (member.id === memberId));
  }

  findByEmail(memberEmail: string): User {
    return <User>this.users.find(member => (member.email === memberEmail));
  }

  // make it return the userTeam info
  getUserTeamInfo(userID: number): Observable<UserTeam> {
    return this.http.get<UserTeam>(`${this._urlRaw}/userTeam/${userID}`, {
      params: {
        'currentTeamId': this.decodedToken.team.toString()
      }
    });
  }

  deleteById(memberId: number): Observable<User> {
    return this.http.delete<User>(this._url + "/" + memberId, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    }).pipe(catchError((err) => {
      console.log(err);

      return throwError(err);
    }))
  }

  deleteByHash(hash: string): Observable<User> {
    return this.http.delete<User>(this._url + "/invite/" + hash, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    }).pipe(catchError((err) => {
      console.log(err);

      return throwError(err);
    }))
  }

  getUsersArray(): User[] {
    return this.users;
  }

  getInvitedUsersArray(): string[] {
    return this.invitedUsers;
  }

  findInvitedMembers(): string[] {
    return this.invitedUsers;
  }

  /**
   * Fills up the array with invited users to this team
   * @param id
   */
  retrieveInvitedUsers(id: number): void {
    this.http.get<string[]>(`${this._urlRaw}/invite/team/${id}`).subscribe((data) => {
      this.invitedUsers = data;
    })
  }
}
