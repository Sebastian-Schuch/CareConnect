import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {TreatmentDto, TreatmentDtoCreate, TreatmentDtoSearch, TreatmentPageDto} from "../dtos/treatment";

@Injectable({
  providedIn: 'root'
})
export class TreatmentService {

  private treatmentBaseUri: string = this.globals.backendUri + '/treatments';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * create a new treatment in the system
   * @param treatmentDtoCreate the data for the treatment that should be created
   * @return an Observable for the created treatment
   */
  createTreatment(treatmentDtoCreate: TreatmentDtoCreate): Observable<TreatmentDto> {
    return this.httpClient.post<TreatmentDto>(this.treatmentBaseUri, treatmentDtoCreate);
  }

  /**
   * update a treatment in the system
   * @param id the ID of the treatment to update
   * @param treatmentDtoUpdate the data for the treatment that should be updated
   * @return an Observable for the updated treatment
   */
  updateTreatment(id: number, treatmentDtoUpdate: TreatmentDtoCreate): Observable<TreatmentDto> {
    return this.httpClient.put<TreatmentDto>(this.treatmentBaseUri + '/' + id, treatmentDtoUpdate);
  }

  /**
   * get all treatments in the system
   * @return an Observable for the treatments
   */
  getTreatmentById(id: number): Observable<TreatmentDto> {
    return this.httpClient.get<TreatmentDto>(this.treatmentBaseUri + '/' + id);
  }

  /**
   * get all treatments from patient matching the search terms
   *
   * @param searchTerm the search terms for the treatments
   */
  searchTreatments(searchTerm: TreatmentDtoSearch) {
    let params = new HttpParams();
    params = params.set('page', searchTerm.page);
    params = params.set('size', searchTerm.size);
    params = params.set('patientId', searchTerm.patientId);
    if (searchTerm.startDate != null && searchTerm.endDate != null) {
      params = params.set('startDate', new Date(searchTerm.startDate).toISOString());
      params = params.set('endDate', new Date(searchTerm.endDate).toISOString());
    }
    if (searchTerm.treatmentTitle != null && searchTerm.treatmentTitle != "") {
      params = params.set('treatmentTitle', searchTerm.treatmentTitle);
    }
    if (searchTerm.medicationName != null && searchTerm.medicationName != "") {
      params = params.set('medicationName', searchTerm.medicationName);
    }
    if (searchTerm.doctorName != null && searchTerm.doctorName != "") {
      params = params.set('doctorName', searchTerm.doctorName);
    }
    if (searchTerm.departmentName != null && searchTerm.departmentName != "") {
      params = params.set('departmentName', searchTerm.departmentName);
    }
    if (searchTerm.patientName != null && searchTerm.patientName != "") {
      params = params.set('patientName', searchTerm.patientName);
    }
    if (searchTerm.svnr != null && searchTerm.svnr != "" && searchTerm.svnr.trim().length === 10) {
      params = params.set('svnr', searchTerm.svnr.trim());
    }
    return this.httpClient.get<TreatmentPageDto>(this.treatmentBaseUri + '/search', {params: params});
  }

}
