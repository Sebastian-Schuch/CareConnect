import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {UserCreateDto, UserDetailDto} from "../dtos/user";
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

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) {
  }

  /**
   * Create a new admin in the system.
   *
   * @param admin the data for the admin that should be created
   * @return an Observable for the created admin
   */
  createAdmin(admin: UserCreateDto): Observable<UserDetailDto> {
    return this.http.post<UserDetailDto>(
      this.adminBaseUri,
      admin
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
  createDoctor(doctor: UserCreateDto): Observable<UserDetailDto> {
    return this.http.post<UserDetailDto>(
      this.doctorBaseUri,
      doctor
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
  createSecretary(secretary: UserCreateDto): Observable<UserDetailDto> {
    return this.http.post<UserDetailDto>(
      this.secretaryBaseUri,
      secretary
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
   * Create a new patient in the system.
   *
   * @param patient the data for the patient that should be created
   * @return an Observable for the created patient
   */
  createPatient(patient: UserCreateDto): Observable<UserDetailDto> {
    return this.http.post<UserDetailDto>(
      this.patientBaseUri,
      patient
    );
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
   * Get all patients from the backend.
   *
   * @return an Observable of the patients
   */
  getAllPatients(): Observable<UserDetailDto[]> {
    return this.http.get<UserDetailDto[]>(`${this.patientBaseUri}`);
  }
}
