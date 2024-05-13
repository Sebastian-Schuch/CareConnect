import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {StationCreateDto, StationDetailDto} from "../dtos/Station";
import {environment} from "../../environments/environment";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class StationService {
  private stationUri: string = this.globals.backendUri + '/station';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) { }

  public createStation(station: StationCreateDto): Observable<any> {
    return this.http.post(this.stationUri, station);
  }
}
