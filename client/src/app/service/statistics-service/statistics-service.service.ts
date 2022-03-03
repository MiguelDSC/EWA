import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TimescaleInterface } from 'src/app/interfaces/timescale';
import { User } from 'src/app/models/user.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {

  private url: string = `${environment.api}/api/admin`;

  constructor(private http: HttpClient) { }

  /**
   * Get the amount of logins.
   * @returns get the login data.
   */
  public getLogins(): Observable<TimescaleInterface[]> {
    return this.http.get<TimescaleInterface[]>(`${this.url}/login`).pipe(
      map((timescales: TimescaleInterface[]) => {

        // Get array values.
        return timescales.map((entry: TimescaleInterface) => ({
          date: new Date(entry.date),  // String to date object.
          value: entry.value
        }))
      })
    );
  }

  public getChanges(): Observable<TimescaleInterface[]> {
    return this.http.get<TimescaleInterface[]>(`${this.url}/changes`).pipe(
      map((timescales: TimescaleInterface[]) => {

        // Get array values.
        return timescales.map((entry: TimescaleInterface) => ({
          date: new Date(entry.date),  // String to date object.
          value: entry.value
        }))
      })
    );
  }

  public getChangesRoles(): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/changes/roles`).pipe();
  }

  public getCaptains(): Observable<User[]> {
    return this.http.get<User[]>(`${this.url}/team-captains`).pipe();
  }
}
