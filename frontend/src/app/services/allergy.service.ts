import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {AllergyDto, AllergyDtoCreate, AllergyPageDto} from "../dtos/allergy";
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
  public createAllergy(allergyCreateDto: AllergyDtoCreate) {
    return this.http.post<AllergyDto>(this.allergyUri, allergyCreateDto);
  }

  /**
   * Updates an existing allergy.
   *
   * @param allergyDto the allergy to update
   * @return an Observable of the updated allergy
   */
  public updateAllergy(allergyDto: AllergyDto) {
    return this.http.put<AllergyDto>(`${this.allergyUri}/${allergyDto.id}`, allergyDto);
  }

  /**
   * Get an allergy by its id.
   *
   * @param id the id of the allergy
   * @return an Observable of the allergy
   */
  public getAllergyById(id: number) {
    return this.http.get<AllergyDto>(`${this.allergyUri}/${id}`);
  }

  /**
   * Get all allergies from the backend.
   *
   * @return an Observable of the allergies
   */
  getAllergiesAll() {
    return this.http.get<AllergyDto[]>(`${this.allergyUri}`);
  }

  /**
   * Get all allergies from the backend with pagination.
   *
   * @param allergyName the name of the allergy to search for
   * @param page the page number
   * @param size the size of the page
   * @return an Observable of the allergies
   */
  searchAllergies(allergyName: string, page: number, size: number) {
    let params = new HttpParams();
    params = params.set('page', page);
    params = params.set('size', size);
    if (allergyName != null && allergyName != "") {
      params = params.set('allergyName', allergyName.trim());
    }
    return this.http.get<AllergyPageDto>(`${this.allergyUri}/search`, {params: params});
  }

  /**
   * Deletes an allergy by its id.
   *
   * @param id the id of the allergy to delete
   */
  deleteAllergy(id: number) {
    return this.http.delete(`${this.allergyUri}/${id}`);
  }
}
