import { Injectable } from '@angular/core';
import {TreatmentMedicineDtoCreate, TreatmentMedicineSelection} from "../dtos/treatmentMedicine";
import {Observable} from "rxjs";
import {Globals} from "../global/globals";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})

export class TreatmentMedicineService {
  private treatmentMedicineBaseUri: string = this.globals.backendUri + '/treatmentMedicines';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  createTreatmentMedicine(treatmentMedicineDtoCreate: TreatmentMedicineDtoCreate): Observable<TreatmentMedicineSelection> {
    return this.httpClient.post<TreatmentMedicineSelection>(this.treatmentMedicineBaseUri, treatmentMedicineDtoCreate);
  }

  deleteTreatmentMedicine(id: number): Observable<any> {
    return this.httpClient.delete(this.treatmentMedicineBaseUri + '/' + id);
  }

}
