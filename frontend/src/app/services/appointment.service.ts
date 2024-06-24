import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {AppointmentDto, AppointmentDtoCalendar, AppointmentDtoCreate, AppointmentPageDto} from "../dtos/appointment";
import {OutpatientDepartmentDto} from "../dtos/outpatient-department";
import {UserDto} from "../dtos/user";


@Injectable({
  providedIn: 'root'
})

export class AppointmentService {
  private appointmentBaseUri: string = this.globals.backendUri + '/appointments';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) {
  }

  /**
   * Create a new Appointment for a user.
   *
   * @param appointment the data for the appointment that should be created
   * @return an Observable for the created appointment
   */
  createAppointment(appointment: AppointmentDtoCreate): Observable<AppointmentDto> {
    return this.http.post<AppointmentDto>(
      this.appointmentBaseUri,
      appointment
    );
  }

  /**
   * Get all appointments for a user.
   *
   * @return an Observable for the appointments
   */
  getAppointmentsFromPatient(id: number): Observable<AppointmentDto[]> {
    return this.http.get<AppointmentDto[]>(
      this.appointmentBaseUri + "/patients/" + id
    );
  }

  /**
   * Get all appointments
   *
   * @return an Observable for the appointments
   */
  getAppointments(): Observable<AppointmentDto[]> {
    return this.http.get<AppointmentDto[]>(
      this.appointmentBaseUri + "/all"
    );
  }

  /**
   * Get all appointments for an outpatient department for a specified month.
   *
   * @param outpatientDepartmentId the id of the outpatient department
   * @param startDate the start date of the time period
   * @param endDate the end date of the time period
   * @return an Observable for the calendar appointments
   */
  getAppointmentsFromOutpatientDepartmentForTimePeriod(outpatientDepartmentId: number, startDate: Date, endDate: Date): Observable<AppointmentDtoCalendar[]> {

    let params = new HttpParams();
    params = params.append('outpatientDepartmentId', outpatientDepartmentId);
    params = params.append('startDate', new Date(startDate).toString());
    params = params.append('endDate', new Date(endDate).toString());
    return this.http.get<AppointmentDtoCalendar[]>(
      this.appointmentBaseUri,
      {params}
    );
  }

  /**
   * Cancel an appointment.
   *
   * @param id the id of the appointment to be cancelled
   */
  cancelAppointment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.appointmentBaseUri}/${id}`);
  }

  /**
   * Get appointments by patient with optional filters for outpatient department, start date, and end date.
   * @param patientId the patient for whom to get appointments
   * @param outpatientDepartmentId optional outpatient department filter
   * @param startDate optional start date filter
   * @param endDate optional end date filter
   * @param page page number for pagination
   * @param size page size for pagination
   * @return an Observable for the paginated appointments
   */
  getAppointmentsByPatient(
    patientId: number,
    outpatientDepartmentId: number | null,
    startDate: Date | null,
    endDate: Date | null,
    page: number,
    size: number
  ): Observable<AppointmentPageDto> {
    let params = new HttpParams()
      .set('patientId', patientId.toString())
      .set('page', page.toString())
      .set('size', size.toString());

    if (outpatientDepartmentId !== null) {
      params = params.set('outpatientDepartmentId', outpatientDepartmentId.toString());
    }

    if (startDate !== null) {
      params = params.set('startDate', startDate.toString());
    }

    if (endDate !== null) {
      params = params.set('endDate', endDate.toString());
    }
    return this.http.get<AppointmentPageDto>(`${this.appointmentBaseUri}/patient`, { params });
  }


  /**
   * Get appointments by patient with optional filters for outpatient department, start date, and end date.
   * @param patientId the patient for whom to get appointments
   * @param outpatientDepartmentId optional outpatient department filter
   * @param startDate optional start date filter
   * @param endDate optional end date filter
   * @param page page number for pagination
   * @param size page size for pagination
   * @return an Observable for the paginated appointments
   */
  getAllFilteredAppointments(
    patientId: number,
    outpatientDepartmentId: number | null,
    startDate: Date | null,
    endDate: Date | null,
    page: number,
    size: number
  ): Observable<AppointmentPageDto> {
    let params = new HttpParams()

      .set('page', page.toString())
      .set('size', size.toString());

    if (outpatientDepartmentId !== null) {
      params = params.set('outpatientDepartmentId', outpatientDepartmentId.toString());
    }

    if (patientId !== null) {
      params = params.set('patientId', patientId.toString());
    }

    if (startDate !== null) {
      params = params.set('startDate', startDate.toString());
    }

    if (endDate !== null) {
      params = params.set('endDate', endDate.toString());
    }
    return this.http.get<AppointmentPageDto>(`${this.appointmentBaseUri}/all`, { params });
  }

}
