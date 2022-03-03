import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TeamSelectionInterface } from 'src/app/interfaces/teamselection';
import { Role } from 'src/app/models/role.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TeamSelectionService {

  constructor(private http: HttpClient, private router: Router) { }

  getTeams(id: number): Observable<any> {
    return this.http.get<any>(`${environment.api}/api/auth/team/${id}`).pipe(
      map((items: any[]) => {
        return items.map((item: any) => ({
          teamId: item.team.id,          
          teamName: item.team.name,
          roleName: item.role.name
        }));
      })
    );
  }

  authTeam(teamId: number): boolean {
    this.http.post<any>(`${environment.api}/api/auth/team/${teamId}`, {}, { observe: 'response' })
      .subscribe((data: any) => {
        let token = data['headers'].get('Authorization');

        if(token == null) return false;
  
        if (data.body.team == null) return false;
        
        localStorage.setItem("team", JSON.stringify(data.body.team));

        token = token.replace('Bearer ', '');
        localStorage.setItem('token', token);
        this.router.navigate([`/${data['headers'].get('Location').split('/').pop()}`]);
  
        return true;
      });

    return false;
  }
}
