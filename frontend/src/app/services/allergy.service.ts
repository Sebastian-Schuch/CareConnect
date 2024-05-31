import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AllergyCreateDto, AllergyDetailDto} from "../dtos/allergy";
import {Globals} from "../global/globals";

@Injectable({
  providedIn: 'root'
})

export class AllergyService {

  private allergyUri: string = this.globals.backendUri + '/allergies';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) {
  }

  /**
   * Creates a new allergy if it does not already exist in the db.
   *
   * @param allergyCreateDto
   */
  public createAllergy(allergyCreateDto: AllergyCreateDto) {
    return this.http.post<AllergyDetailDto>(this.allergyUri, allergyCreateDto);
  }

  /**
   * Get all allergies from the backend.
   *
   * @return an Observable of the allergies
   */
  getAllergiesAll() {
    return this.http.get<AllergyDetailDto[]>(`${this.allergyUri}`);
  }

}
