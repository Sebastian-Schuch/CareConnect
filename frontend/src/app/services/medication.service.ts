import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {MedicationDto, MedicationDtoCreate} from "../dtos/medication";
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
   * Update the medication with the given ID.
   */
  getMedicationCount(): Observable<number> {
    return this.http.get<number>(`${this.medicationBaseUri}/count`);
  }
}
