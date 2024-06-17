import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {StayDto, StayDtoCreate, StayDtoPage} from "../dtos/stays";

@Injectable({
  providedIn: 'root'
})
export class StaysService {
  private stayUri: string = this.globals.backendUri + '/stays';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) {
  }

  public getCurrentStay(patientId: number): Observable<StayDto> {
    let params = new HttpParams();
    params = params.set('id', patientId);

    return this.http.get<StayDto>(this.stayUri + `/current`, {params: params});
  }

  public getAllStays(id: number, page: number, size: number): Observable<StayDtoPage> {
    let params = new HttpParams();
    params = params.set('id', id);
    params = params.set('page', page);
    params = params.set('size', size);

    return this.http.get<StayDtoPage>(this.stayUri + `/all`, {params: params});
  }

  public createNewStay(stay: StayDtoCreate): Observable<StayDto> {
    return this.http.post<StayDto>(this.stayUri + `/arrival`, stay);
  }

  public endStay(stayId: number): Observable<StayDto> {
    let params = new HttpParams();
    params = params.set('id', stayId);
    return this.http.put<StayDto>(this.stayUri + `/discharge`, {params: params});
  }

  public update(stay: StayDto): Observable<StayDto> {
    return this.http.put<StayDto>(this.stayUri + `/update`, stay);
  }

}
