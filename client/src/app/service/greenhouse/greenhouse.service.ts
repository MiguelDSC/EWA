import {Injectable} from '@angular/core';
import {GreenhouseInterface} from "../../interfaces/greenhouse";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class GreenhouseService {

  constructor(private http: HttpClient) {
  }

  getAll(): Observable<GreenhouseInterface[]> {
    return this.http.get<GreenhouseInterface[]>(`${environment.api}/api/greenhouse/free`);
  }
}
