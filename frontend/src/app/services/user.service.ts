import {environment} from "../../environments/environment";
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {UserCreateDto, UserDetailDto} from "../dtos/user";
import {Observable} from "rxjs";

const secretaryBaseUri = environment.backendUrl + '/api/secretaries';

@Injectable({
  providedIn: 'root'
})

export class UserService {

  constructor(
    private http: HttpClient,
  ) {
  }

  /**
   * Get the secretary with given ID.
   *
   * @param id the ID of the secretary to get
   * @return an Observable of the secretary
   */
  getSecretaryById(id: number): Observable<UserDetailDto> {
    return this.http.get<UserDetailDto>(`${secretaryBaseUri}/${id}`);
  }

  /**
   * Get all secretaries from the backend.
   *
   * @return an Observable of the secretary
   */
  getSecretariesAll(): Observable<UserDetailDto[]> {
    return this.http.get<UserDetailDto[]>(`${secretaryBaseUri}`);
  }

  /**
   * Create a new secretary in the system.
   *
   * @param secretary the data for the secretary that should be created
   * @return an Observable for the created secretary
   */
  createSecretary(secretary: UserCreateDto): Observable<UserDetailDto> {
    return this.http.post<UserDetailDto>(
      secretaryBaseUri,
      secretary
    );
  }
}
