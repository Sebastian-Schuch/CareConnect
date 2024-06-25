import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {
  InpatientDepartmentDto,
  InpatientDepartmentDtoCreate,
  InpatientDepartmentPageDto
} from "../dtos/inpatient-department";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class InpatientDepartmentService {
  private inpatientDepartmentUri: string = this.globals.backendUri + '/inpatient-departments';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) {
  }

  public createInpatientDepartment(inpatientDepartment: InpatientDepartmentDtoCreate): Observable<any> {
    return this.http.post(this.inpatientDepartmentUri, inpatientDepartment);
  }

  public getInpatientDepartments(searchTerm: string, page: number, size: number): Observable<InpatientDepartmentPageDto> {
    let params = new HttpParams();
    params = params.set('searchTerm', searchTerm);
    params = params.set('page', page);
    params = params.set('size', size);
    return this.http.get<InpatientDepartmentPageDto>(this.inpatientDepartmentUri, {params: params});
  }

  public deleteInpatientDepartment(id: number): Observable<InpatientDepartmentDto> {
    return this.http.delete<InpatientDepartmentDto>(this.inpatientDepartmentUri + `/${id}`);
  }

  public getInpatientDepartmentById(id: number): Observable<InpatientDepartmentDto> {
    return this.http.get<InpatientDepartmentDto>(this.inpatientDepartmentUri + `/${id}`);
  }

  editInpatientDepartment(update: InpatientDepartmentDto) {
    return this.http.post<InpatientDepartmentDto>(this.inpatientDepartmentUri + `/${update.id}`, update);
  }
}
