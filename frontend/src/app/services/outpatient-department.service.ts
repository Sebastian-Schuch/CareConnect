import {Injectable} from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {OutpatientDepartmentDto, OutpatientDepartmentDtoCreate} from "../dtos/outpatient-department";

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

  createOutpatientDepartment(outpatientDepartment: OutpatientDepartmentDtoCreate): Observable<OutpatientDepartmentDto> {
    return this.httpClient.post<OutpatientDepartmentDto>(this.outpatientDepartmentBaseUri, outpatientDepartment);
  }
}
