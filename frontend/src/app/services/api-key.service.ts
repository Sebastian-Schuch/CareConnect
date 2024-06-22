import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {ApiKeyDto, ApiKeyDtoFirst, ApiKeyPageDto} from "../dtos/api-keys";

@Injectable({
  providedIn: 'root'
})
export class ApiKeyService {

  private apiKeyUri: string = this.globals.backendUri + '/api-keys';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) { }

  /**
   * Creates a new api key
   *
   * @param apiKeyCreateDto
   */
  public createApiKey(apiKeyCreateDto: any) {
    return this.http.post<ApiKeyDtoFirst>(this.apiKeyUri, apiKeyCreateDto);
  }

  /**
   * Get all api keys from the backend.
   *
   * @return an Observable of the api keys
   */
  getApiKeysAll(page: number, size: number) {
    return this.http.get<ApiKeyPageDto>(`${this.apiKeyUri}?page=${page}&size=${size}`);
  }

  /**
   * Deletes an api key by id
   *
   * @param id
   */
  deleteApiKey(id: number) {
    return this.http.delete(`${this.apiKeyUri}/${id}`);
  }
}
