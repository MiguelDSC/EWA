import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserSettings, UserSettingsResponse } from 'src/app/models/userSettings';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class SettingsService {

  private ENDPOINT = environment.api + "/settings";

  constructor(private http: HttpClient) { }


  //https://stackoverflow.com/a/52680514
  getSettings(userId: Number): Observable<UserSettings> {
    return this.http.get<UserSettings>(`${this.ENDPOINT}/${userId}`).pipe(
      map((settingsJson: any) => {
        //Unpack the number from graphPreferences into an array of ones and zeroes
        const bits: string = settingsJson.graphPreferences.toString(2).padStart(7, '0')
        //Initialize a new empty map
        let graphPrefs: Map<string, boolean> = new Map();
        UserSettings.possibleGraphPreferences.forEach((k, i) => graphPrefs.set(k, bits[i] === '1'));

        return new UserSettings(
          settingsJson.temperatureUnit,
          settingsJson.distanceUnit,
          settingsJson.displayName,
          settingsJson.displayStatus,
          graphPrefs
        )}));
  }

  async postSettings(userId: Number, newSettings: UserSettings) {
    let obj: UserSettingsResponse = UserSettingsResponse.fromUserSettings(newSettings);
    // console.log(obj);
    await this.http.post<UserSettingsResponse>(
      `${this.ENDPOINT}/${userId}`,
      obj, {}).toPromise();
  }

  // https://stackoverflow.com/a/52680514
  // https://stackoverflow.com/questions/55591871/view-blob-response-as-image-in-angular
  getProfilePicture(userId: Number): Observable<Blob> {
    return this.http.get(`${this.ENDPOINT}/${userId}/picture` , {responseType: 'blob'})
  }

  async postProfilePicture(userId: Number, file: File) {
    const formData = new FormData();
    formData.append('file', file, file.name);
    await this.http.put(`${this.ENDPOINT}/${userId}/picture`,
      formData, {}).toPromise().catch( e => {
        if (e.status !== 200) {
          console.log('Error: ', e);
      }
    })
  }
}
