import {HttpClient} from '@angular/common/http';
import {CoreEnvironment} from '@angular/compiler/src/compiler_facade_interface';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {Team} from "../../models/team";
import {GreenhouseInterface} from "../../interfaces/greenhouse";
import {User} from "../../models/user.model";

@Injectable({
  providedIn: 'root'
})
export class TeamService {

  constructor(private http: HttpClient) {
  }

  getAll(): Observable<Team[]> {
    return this.http.get<Team[]>(`${environment.api}/api/team/`);
  }

  addTeam(team: Team): Observable<Team> {
    return this.http.post<Team>(`${environment.api}/api/team/`, team);
  }

  getAllUsers(id: number): Observable<User[]> {
    return this.http.get<User[]>(`${environment.api}/api/team/${id}/users/`);
  }

  updateTeam(team: Team): Observable<Team> {
    return this.http.put<Team>(`${environment.api}/api/team/${team.id}`, team);
  }

  deleteTeam(id: number): Observable<Team> {
    return this.http.delete<Team>(`${environment.api}/api/team/${id}`);
  }
}
