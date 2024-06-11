import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {StationDto} from "../dtos/Station";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {StayDto, StayDtoCreate, StayDtoPage} from "../dtos/stays";

@Injectable({
  providedIn: 'root'
})
export class StaysService {
  private stationUri: string = this.globals.backendUri + '/stays';
  constructor(
    private http: HttpClient,
    private globals: Globals
  ) { }

  public getCurrentStay(patientId: string): Observable<StayDto> {
    let params = new HttpParams();
    params = params.set('id', patientId);

    return this.http.get<StayDto>(this.stationUri+`/current`, {params: params});
  }

  public getAllStays(email: string, page: number, size: number): Observable<StayDtoPage> {
    let params = new HttpParams();
    params = params.set('id', email);
    params = params.set('page', page);
    params = params.set('size', size);

    return this.http.get<StayDtoPage>(this.stationUri+`/all`, {params: params});
  }

  public createNewStay(stay: StayDtoCreate): Observable<StayDto> {
    return this.http.post<StayDto>(this.stationUri+`/arrival`, stay);
  }

  public endStay(stay: StayDto): Observable<StayDto> {
    return this.http.put<StayDto>(this.stationUri+`/discharge`, stay);
  }

  public update(stay: StayDto): Observable<StayDto> {
    return this.http.put<StayDto>(this.stationUri+`/update`, stay);
  }

}
