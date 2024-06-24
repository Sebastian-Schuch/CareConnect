import {Injectable} from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {
  OutpatientDepartmentCapacityDto,
  OutpatientDepartmentDto,
  OutpatientDepartmentDtoCreate,
  OutpatientDepartmentPageDto
} from "../dtos/outpatient-department";

@Injectable({
  providedIn: 'root'
})
export class OutpatientDepartmentService {

  private outpatientDepartmentBaseUri: string = this.globals.backendUri + '/outpatient-departments';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getOutpatientDepartment(): Observable<OutpatientDepartmentDto[]> {
    return this.httpClient.get<OutpatientDepartmentDto[]>(this.outpatientDepartmentBaseUri);
  }

  getOutpatientDepartmentById(id: number): Observable<OutpatientDepartmentDto> {
    return this.httpClient.get<OutpatientDepartmentDto>(this.outpatientDepartmentBaseUri + '/' + id);
  }

  getOutpatientDepartmentPage(searchTerm: string, page: number, size: number): Observable<OutpatientDepartmentPageDto> {
    let params = new HttpParams();
    params = params.set('searchTerm', searchTerm);
    params = params.set('page', page);
    params = params.set('size', size);
    return this.httpClient.get<OutpatientDepartmentPageDto>(this.outpatientDepartmentBaseUri + '/page', {params: params});
  }

  createOutpatientDepartment(outpatientDepartment: OutpatientDepartmentDtoCreate): Observable<OutpatientDepartmentDto> {
    return this.httpClient.post<OutpatientDepartmentDto>(this.outpatientDepartmentBaseUri, outpatientDepartment);
  }

  deleteOutpatientDepartment(id: number) {
    return this.httpClient.delete<OutpatientDepartmentDto>(this.outpatientDepartmentBaseUri + '/' + id);
  }

  editOutpatientDepartment(update: OutpatientDepartmentDto) {
    return this.httpClient.post<OutpatientDepartmentDto>(this.outpatientDepartmentBaseUri + '/' + update.id, update);
  }

  getOutpatientDepartmentCapacitiesForDay(date: string): Observable<OutpatientDepartmentCapacityDto[]> {
    return this.httpClient.get<OutpatientDepartmentCapacityDto[]>(`${this.outpatientDepartmentBaseUri}/capacities/day?date=${date}`);
  }

  getOutpatientDepartmentCapacitiesForWeek(startDate: string): Observable<OutpatientDepartmentCapacityDto[]> {
    return this.httpClient.get<OutpatientDepartmentCapacityDto[]>(`${this.outpatientDepartmentBaseUri}/capacities/week?startDate=${startDate}`);
  }

  getOutpatientDepartmentCapacitiesForMonth(date: string): Observable<OutpatientDepartmentCapacityDto[]> {
    return this.httpClient.get<OutpatientDepartmentCapacityDto[]>(`${this.outpatientDepartmentBaseUri}/capacities/month?date=${date}`);
  }
}
