import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {TreatmentDto, TreatmentDtoCreate} from "../dtos/treatment";

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

}
