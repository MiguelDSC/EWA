import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { GreenhouseInterface } from 'src/app/interfaces/greenhouse';
import { MeasurementInterface } from 'src/app/interfaces/measurement';
import { TimescaleInterface } from 'src/app/interfaces/timescale';
import { environment } from 'src/environments/environment';
import jwt_decode from "jwt-decode";
import { GreenhouseSettingInterface } from 'src/app/interfaces/greenhouse-setting';

@Injectable({
  providedIn: 'root'
})
export class GreenhouseService {

  private dailyReplay: Observable<any>;
  private hourlyReplay: Observable<any>;
  private id: number = 1;

  private url: string = `${environment.api}/api/greenhouse`;
  decodedToken: any = jwt_decode(localStorage.getItem('token'));

  private websocket: WebSocket;

  constructor(private http: HttpClient) {
    this.dailyReplay = forkJoin({
      co2Level: this.getAverages("daily", "co2-level"),
      airTemp: this.getAverages("daily", "air-temp"),
      soilTemp: this.getAverages("daily", "soil-temp"),
      airHumidity: this.getAverages("daily", "air-humidity"),
      soilHumidity: this.getAverages("daily", "soil-humidity"),
      waterPH: this.getAverages("daily", "water-ph")
    }).pipe(shareReplay(1));

    this.hourlyReplay = forkJoin({
      co2Level: this.getAverages("hourly", "co2-level"),
      airTemp: this.getAverages("hourly", "air-temp"),
      soilTemp: this.getAverages("hourly", "soil-temp"),
      airHumidity: this.getAverages("hourly", "air-humidity"),
      soilHumidity: this.getAverages("hourly", "soil-humidity"),
      waterPH: this.getAverages("hourly", "water-ph")
    }).pipe(shareReplay(1));
  }

  public getAverages(time: string, type: string): Observable<any> {

    // Pipe trough map to parse to date.
    return this.http.get<any>(`${this.url}/${this.id}/${type}/${time}/average`).pipe(
      map((timescales: TimescaleInterface[]) => {
        return Object.keys(timescales).map((d: any) => ({
          date: new Date(d),
          value: timescales[d]
        }));
      }));
  }

  public getReplay(index: number): Observable<any> {
    return (index == 0) ? this.dailyReplay : this.hourlyReplay;
  }

  public getCurrentMeasurments(): Observable<MeasurementInterface> {
    return this.http.get<MeasurementInterface>(`${this.url}/${this.id}/current`);
  }

  public getGreenhouseSetting(): Observable<GreenhouseSettingInterface> {
    return this.http.get<GreenhouseSettingInterface>(`${this.url}/${this.id}/settings`);
  }

  public setGreenhouseSetting(json: GreenhouseInterface): Observable<HttpResponse<GreenhouseInterface>> {
    return this.http.put<GreenhouseInterface>(`${this.url}/${this.id}/settings`, json, {observe: "response"});
  }
}