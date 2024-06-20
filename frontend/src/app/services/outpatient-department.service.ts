import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { OutpatientDepartmentCapacityDto, OutpatientDepartmentDto, OutpatientDepartmentDtoCreate } from "../dtos/outpatient-department";
import { Globals } from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class OutpatientDepartmentService {
  private outpatientDepartmentBaseUri: string = this.globals.backendUri + '/outpatient-departments';

  constructor(private httpClient: HttpClient, private globals: Globals) {}

  getOutpatientDepartment(): Observable<OutpatientDepartmentDto[]> {
    return this.httpClient.get<OutpatientDepartmentDto[]>(this.outpatientDepartmentBaseUri);
  }

  getOutpatientDepartmentById(id: number): Observable<OutpatientDepartmentDto> {
    return this.httpClient.get<OutpatientDepartmentDto>(this.outpatientDepartmentBaseUri + '/' + id);
  }

  createOutpatientDepartment(outpatientDepartment: OutpatientDepartmentDtoCreate): Observable<OutpatientDepartmentDto> {
    return this.httpClient.post<OutpatientDepartmentDto>(this.outpatientDepartmentBaseUri, outpatientDepartment);
  }

  getOutpatientDepartmentCapacities(): Observable<OutpatientDepartmentCapacityDto[]> {
    return this.httpClient.get<OutpatientDepartmentCapacityDto[]>(this.outpatientDepartmentBaseUri + '/capacities');
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
