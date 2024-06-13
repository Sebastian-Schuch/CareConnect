import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {UserDto, UserDtoCreate, UserDtoList, UserDtoSearch, UserDtoUpdate} from "../dtos/user";
import {Observable} from "rxjs";
import {Globals} from "../global/globals";
import {AuthRequest} from "../dtos/auth-request";
import {Password} from "../dtos/password";


@Injectable({
  providedIn: 'root'
})

export class UserService {
  private adminBaseUri: string = this.globals.backendUri + '/admins';
  private doctorBaseUri: string = this.globals.backendUri + '/doctors';
  private secretaryBaseUri: string = this.globals.backendUri + '/secretaries';
  private patientBaseUri: string = this.globals.backendUri + '/patients';
  private credentialAdminBaseUri: string = this.globals.backendUri + '/credentials/admins';
  private credentialDoctorBaseUri: string = this.globals.backendUri + '/credentials/doctors';
  private credentialSecretaryBaseUri: string = this.globals.backendUri + '/credentials/secretaries';
  private credentialPatientBaseUri: string = this.globals.backendUri + '/credentials/patients';
  private credentialBaseUri: string = this.globals.backendUri + '/credentials';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) {
  }

  /**
   * Change the password of the user
   *
   * @param passwords User data with the new password
   */
  changePassword(passwords: Password): Observable<string> {
    return this.http.patch(this.credentialBaseUri, passwords, {responseType: 'text'});
  }

  /**
   * Reset the password of the user with the given email.
   *
   * @param email the email of the user that should be reset
   * @return an Observable for the email of the reset user
   */
  resetPassword(email: AuthRequest): Observable<Blob> {
    return this.http.post(
      `${this.credentialBaseUri}/reset`,
      email,
      {responseType: 'blob'}
    );
  }

  /**
   * Disable the user with the given email.
   *
   * @param email the email of the user that should be disabled
   * @return an Observable for the email of the disabled user
   */
  disableUser(email: AuthRequest): Observable<string> {
    return this.http.patch(
      `${this.credentialBaseUri}/disable`,
      email,
      {responseType: 'text'}
    );
  }

  /**
   * Search for admins in the system.
   *
   * @param searchParams the parameters for the search
   * @return an Observable for the found admins
   */
  searchAdmin(searchParams: UserDtoSearch): Observable<UserDtoList[]> {
    let params = this.createHttpParams(searchParams)
    return this.http.get<UserDtoList[]>(
      `${this.adminBaseUri}/search`,
      {params}
    );
  }

  /**
   * Create a new admin in the system.
   *
   * @param admin the data for the admin that should be created
   * @return an Observable for the created admin
   */
  createAdmin(admin: UserDtoCreate): Observable<Blob> {
    return this.http.post(
      this.adminBaseUri,
      admin,
      {responseType: 'blob'}
    );
  }

  /**
   * Get the admin with given ID.
   *
   * @param id the ID of the admin to get
   * @return an Observable of the admin
   */
  getAdminById(id: number): Observable<UserDto> {
    return this.http.get<UserDto>(`${this.adminBaseUri}/${id}`);
  }

  /**
   * Get the information from the admin that is logged in.
   *
   * @return an Observable for the UserDto
   */
  getAdminCredentials() {
    return this.http.get<UserDto>(
      this.credentialAdminBaseUri
    );
  }

  /**
   * Get all admins from the backend.
   *
   * @return an Observable of the admins
   */
  getAllAdmins(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(`${this.adminBaseUri}`);
  }

  /**
   * Update the admin with the given data.
   *
   * @param admin the data for the admin that should be updated
   * @param adminId the ID of the admin that should be updated
   * @return an Observable for the updated admin
   */
  updateAdmin(admin: UserDtoUpdate, adminId: number): Observable<UserDto> {
    return this.http.put<UserDto>(
      `${this.adminBaseUri}/${adminId}`,
      admin
    );
  }

  /**
   * Search for doctors in the system.
   *
   * @param searchParams the parameters for the search
   * @return an Observable for the found doctors
   */
  searchDoctor(searchParams: UserDtoSearch): Observable<UserDtoList[]> {
    let params = this.createHttpParams(searchParams)
    return this.http.get<UserDtoList[]>(
      `${this.doctorBaseUri}/search`,
      {params}
    );
  }

  /**
   * Create a new doctors in the system.
   *
   * @param doctor the data for the doctors that should be created
   * @return an Observable for the created doctors
   */
  createDoctor(doctor: UserDtoCreate): Observable<Blob> {
    return this.http.post(
      this.doctorBaseUri,
      doctor,
      {responseType: 'blob'}
    );
  }

  /**
   * Get the doctor with given ID.
   *
   * @param id the ID of the doctor to get
   * @return an Observable of the doctor
   */
  getDoctorById(id: number): Observable<UserDto> {
    return this.http.get<UserDto>(`${this.doctorBaseUri}/${id}`);
  }

  /**
   * Get the information from the doctor that is logged in.
   *
   * @return an Observable for the UserDto
   */
  getDoctorCredentials() {
    return this.http.get<UserDto>(
      this.credentialDoctorBaseUri
    );
  }

  /**
   * Get all doctors from the backend.
   *
   * @return an Observable of the doctors
   */
  getAllDoctors(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(`${this.doctorBaseUri}`);
  }

  /**
   * Update the doctor with the given data.
   *
   * @param doctor the data for the doctor that should be updated
   * @param doctorId the ID of the doctor that should be updated
   * @return an Observable for the updated doctor
   */
  updateDoctor(doctor: UserDtoUpdate, doctorId: number): Observable<UserDto> {
    return this.http.put<UserDto>(
      `${this.doctorBaseUri}/${doctorId}`,
      doctor
    );
  }

  /**
   * Search for secretaries in the system.
   *
   * @param searchParams the parameters for the search
   * @return an Observable for the found secretaries
   */
  searchSecretary(searchParams: UserDtoSearch): Observable<UserDtoList[]> {
    let params = this.createHttpParams(searchParams)
    return this.http.get<UserDtoList[]>(
      `${this.secretaryBaseUri}/search`,
      {params}
    );
  }

  /**
   * Create a new secretary in the system.
   *
   * @param secretary the data for the secretary that should be created
   * @return an Observable for the created secretary
   */
  createSecretary(secretary: UserDtoCreate): Observable<Blob> {
    return this.http.post(
      this.secretaryBaseUri,
      secretary,
      {responseType: 'blob'}
    );
  }

  /**
   * Get the secretary with given ID.
   *
   * @param id the ID of the secretary to get
   * @return an Observable of the secretary
   */
  getSecretaryById(id: number): Observable<UserDto> {
    return this.http.get<UserDto>(`${this.secretaryBaseUri}/${id}`);
  }

  /**
   * Get the information from the secretary that is logged in.
   *
   * @return an Observable for the UserDto
   */
  getSecretaryCredentials() {
    return this.http.get<UserDto>(
      this.credentialSecretaryBaseUri
    );
  }

  /**
   * Get all secretaries from the backend.
   *
   * @return an Observable of the secretaries
   */
  getAllSecretaries(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(`${this.secretaryBaseUri}`);
  }

  /**
   * Update the secretary with the given data.
   *
   * @param secretary the data for the secretary that should be updated
   * @param secretaryId the ID of the secretary that should be updated
   * @return an Observable for the updated secretary
   */
  updateSecretary(secretary: UserDtoUpdate, secretaryId: number): Observable<UserDto> {
    return this.http.put<UserDto>(
      `${this.secretaryBaseUri}/${secretaryId}`,
      secretary
    );
  }

  /**
   * Search for patients in the system.
   *
   * @param searchParams the parameters for the search
   * @return an Observable for the found patients
   */
  searchPatient(searchParams: UserDtoSearch): Observable<UserDtoList[]> {
    let params = this.createHttpParams(searchParams)
    return this.http.get<UserDtoList[]>(
      `${this.patientBaseUri}/search`,
      {params}
    );
  }

  /**
   * Get the patient with given ID.
   *
   * @param id the ID of the patient to get
   * @return an Observable of the patient
   */
  getPatientById(id: number): Observable<UserDto> {
    return this.http.get<UserDto>(`${this.patientBaseUri}/${id}`);
  }

  /**
   * Get the information from the patient that is logged in.
   *
   * @return an Observable for the UserDto
   */
  getPatientCredentials() {
    return this.http.get<UserDto>(
      this.credentialPatientBaseUri
    );
  }

  /**
   * Get all patients from the backend.
   *
   * @return an Observable of the patients
   */
  getAllPatients(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(`${this.patientBaseUri}`);
  }

  /**
   * Create a new patient in the system.
   *
   * @param patient the data for the patient that should be created
   * @return an Observable for the created patient
   */
  createPatient(patient: UserDtoCreate): Observable<Blob> {
    return this.http.post(
      this.patientBaseUri,
      patient,
      {responseType: 'blob'}
    );
  }

  /**
   * Update the patient with the given data.
   *
   * @param patient the data for the patient that should be updated
   * @param patientId the ID of the patient that should be updated
   * @return an Observable for the updated patient
   */
  updatePatient(patient: UserDtoUpdate, patientId: number): Observable<UserDto> {
    return this.http.put<UserDto>(
      `${this.patientBaseUri}/${patientId}`,
      patient
    );
  }

  private createHttpParams(searchParams: UserDtoSearch): HttpParams {
    let params = new HttpParams();
    if (searchParams.email) {
      params = params.append('email', searchParams.email);
    }
    if (searchParams.firstname) {
      params = params.append('firstName', searchParams.firstname);
    }
    if (searchParams.lastname) {
      params = params.append('lastName', searchParams.lastname);
    }
    if (searchParams.limit) {
      params = params.append('limit', searchParams.limit);
    }
    return params;
  }
}
