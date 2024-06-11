import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpResponse} from "@angular/common/http";
import {StationDto, StationDtoCreate, StationPageDto} from "../dtos/Station";
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
  ) {
  }

  public createStation(station: StationDtoCreate): Observable<any> {
    return this.http.post(this.stationUri, station);
  }

  public getStations(searchTerm: string, page: number, size: number): Observable<StationPageDto> {
    let params = new HttpParams();
    params = params.set('searchTerm', searchTerm);
    params = params.set('page', page);
    params = params.set('size', size);
    return this.http.get<StationPageDto>(this.stationUri, {params: params});
  }

  public deleteStation(id: number): Observable<StationDto> {
    return this.http.delete<StationDto>(this.stationUri+`/${id}`);
  }

  public getStationById(id: number): Observable<StationDto> {
    return this.http.get<StationDto>(this.stationUri+`/${id}`);
  }

  editStation(update: StationDto) {
    return this.http.post<StationDto>(this.stationUri+`/${update.id}`, update);
  }
}
