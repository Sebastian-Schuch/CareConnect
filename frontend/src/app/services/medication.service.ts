import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {MedicationDto, MedicationDtoCreate, MedicationPageDto} from "../dtos/medication";
import {Globals} from "../global/globals";

@Injectable({
  providedIn: 'root'
})

export class MedicationService {

  private medicationBaseUri: string = this.globals.backendUri + '/medications';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) {
  }

  /**
   * Get the medication with given ID.
   *
   * @param id the ID of the medication to get
   * @return an Observable of the medication
   */
  getMedicationById(id: number): Observable<MedicationDto> {
    return this.http.get<MedicationDto>(`${this.medicationBaseUri}/${id}`);
  }

  /**
   * Update the medication with the given data.
   *
   * @param medication the data for the medication that should be updated
   * @return an Observable of the updated medication
   */
  updateMedication(medication: MedicationDto): Observable<MedicationDto> {
    return this.http.put<MedicationDto>(`${this.medicationBaseUri}/${medication.id}`, medication);
  }

  /**
   * Get all medications from the backend.
   *
   * @return an Observable of the medications
   */
  getMedicationsAll(): Observable<MedicationDto[]> {
    return this.http.get<MedicationDto[]>(`${this.medicationBaseUri}`);
  }

  /**
   * Create a new medication in the system.
   *
   * @param medication the data for the medication that should be created
   * @return an Observable for the created medication
   */
  createMedication(medication: MedicationDtoCreate): Observable<MedicationDto> {
    return this.http.post<MedicationDto>(
      this.medicationBaseUri,
      medication
    );
  }

  /**
   * Get all medications from the backend with pagination.
   *
   * @param medicationName the name of the medication to search for
   * @param page the page number
   * @param size the size of the page
   * @return an Observable of the medications
   */
  searchMedications(medicationName: string, page: number, size: number) {
    let params = new HttpParams();
    params = params.set('page', page);
    params = params.set('size', size);
    if (medicationName != null && medicationName != "") {
      params = params.set('medicationName', medicationName.trim());
    }
    return this.http.get<MedicationPageDto>(`${this.medicationBaseUri}/search`, {params: params});
  }

  /**
   * Delete the medication with the given ID.
   *
   * @param id the ID of the medication to delete
   * @return an Observable
   */
  deleteMedication(id: number): Observable<any> {
    return this.http.delete(`${this.medicationBaseUri}/${id}`);
  }
}
