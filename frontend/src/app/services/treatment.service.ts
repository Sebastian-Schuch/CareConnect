import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {TreatmentDto, TreatmentDtoCreate} from "../dtos/treatment";

@Injectable({
  providedIn: 'root'
})
export class TreatmentService {

  private treatmentBaseUri: string = this.globals.backendUri + '/treatment';
  constructor(private httpClient: HttpClient, private globals: Globals) { }

  createTreatment(treatmentDtoCreate: TreatmentDtoCreate): Observable<TreatmentDto> {
    return this.httpClient.post<TreatmentDto>(this.treatmentBaseUri, treatmentDtoCreate);
  }
}
