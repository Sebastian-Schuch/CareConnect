import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {UserCreateDto, UserDetailDto, UserLoginDto} from "../dtos/user";
import {Observable} from "rxjs";
import {Globals} from "../global/globals";


@Injectable({
  providedIn: 'root'
})

export class UserService {
  private adminBaseUri: string = this.globals.backendUri + '/admins';
  private doctorBaseUri: string = this.globals.backendUri + '/doctors';
  private secretaryBaseUri: string = this.globals.backendUri + '/secretaries';
  private patientBaseUri: string = this.globals.backendUri + '/patients';
  private credentialBaseUri: string = this.globals.backendUri + '/credentials';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) {
  }

  /**
   * Get the information from the logged-in user
   *
   * @return an Observable for the UserDetailDto
   */
  getUserCredentials() {
    return this.http.get<UserDetailDto>(
      this.credentialBaseUri
    );
  }

  /**
   * Create a new admin in the system.
   *
   * @param admin the data for the admin that should be created
   * @return an Observable for the created admin
   */
  createAdmin(admin: UserCreateDto): Observable<Blob> {
    return this.http.post(
      this.adminBaseUri,
      admin,
      { responseType: 'blob' }
    );
  }

  /**
   * Get the admin with given ID.
   *
   * @param id the ID of the admin to get
   * @return an Observable of the admin
   */
  getAdminById(id: number): Observable<UserDetailDto> {
    return this.http.get<UserDetailDto>(`${this.adminBaseUri}/${id}`);
  }

  /**
   * Get all admins from the backend.
   *
   * @return an Observable of the admins
   */
  getAllAdmins(): Observable<UserDetailDto[]> {
    return this.http.get<UserDetailDto[]>(`${this.adminBaseUri}`);
  }

  /**
   * Create a new doctors in the system.
   *
   * @param doctor the data for the doctors that should be created
   * @return an Observable for the created doctors
   */
  createDoctor(doctor: UserCreateDto): Observable<Blob> {
    return this.http.post(
      this.doctorBaseUri,
      doctor,
      { responseType: 'blob' }
    );
  }

  /**
   * Get the doctor with given ID.
   *
   * @param id the ID of the doctor to get
   * @return an Observable of the doctor
   */
  getDoctorById(id: number): Observable<UserDetailDto> {
    return this.http.get<UserDetailDto>(`${this.doctorBaseUri}/${id}`);
  }

  /**
   * Get all doctors from the backend.
   *
   * @return an Observable of the doctors
   */
  getAllDoctors(): Observable<UserDetailDto[]> {
    return this.http.get<UserDetailDto[]>(`${this.doctorBaseUri}`);
  }

  /**
   * Create a new secretary in the system.
   *
   * @param secretary the data for the secretary that should be created
   * @return an Observable for the created secretary
   */
  createSecretary(secretary: UserCreateDto): Observable<Blob> {
    return this.http.post(
      this.secretaryBaseUri,
      secretary,
      { responseType: 'blob' }
    );
  }

  /**
   * Get the secretary with given ID.
   *
   * @param id the ID of the secretary to get
   * @return an Observable of the secretary
   */
  getSecretaryById(id: number): Observable<UserDetailDto> {
    return this.http.get<UserDetailDto>(`${this.secretaryBaseUri}/${id}`);
  }

  /**
   * Get all secretaries from the backend.
   *
   * @return an Observable of the secretaries
   */
  getAllSecretaries(): Observable<UserDetailDto[]> {
    return this.http.get<UserDetailDto[]>(`${this.secretaryBaseUri}`);
  }

  /**
   * Get the patient with given ID.
   *
   * @param id the ID of the patient to get
   * @return an Observable of the patient
   */
  getPatientById(id: number): Observable<UserDetailDto> {
    return this.http.get<UserDetailDto>(`${this.patientBaseUri}/${id}`);
  }

  /**
   * Get the patient with his current credentials.
   *
   * @return an Observable of the patient
   */
  getPatientLoggedIn(): Observable<UserDetailDto> {
    return this.http.get<UserDetailDto>(this.credentialBaseUri);
  }

  /**
   * Get all patients from the backend.
   *
   * @return an Observable of the patients
   */
  getAllPatients(): Observable<UserDetailDto[]> {
    return this.http.get<UserDetailDto[]>(`${this.patientBaseUri}`);
  }

  /**
   * Create a new patient in the system.
   *
   * @param patient the data for the patient that should be created
   * @return an Observable for the created patient
   */
  createPatient(patient: UserCreateDto): Observable<Blob> {
    return this.http.post(
      this.patientBaseUri,
      patient,
      { responseType: 'blob' }
    );
  }
}
