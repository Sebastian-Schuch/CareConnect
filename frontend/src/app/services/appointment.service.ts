import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {AppointmentDto, AppointmentDtoCalendar, AppointmentDtoCreate} from "../dtos/appointment";


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
   * .
   * @param id the id of the outpatient department
   * @param startDate the start date for which the appointments should be retrieved
   * @param endDate the end date for which the appointments should be retrieved
   * @return an Observable for the calendar appointments
   */
  getAppointmentsFromOutpatientDepartmentForTimePeriod(id: number, startDate: Date, endDate: Date): Observable<AppointmentDtoCalendar[]> {

    let params = new HttpParams();
    params = params.append('outpatientDepartmentId', id);
    params = params.append('startDate', startDate.toISOString());
    params = params.append('endDate', endDate.toISOString());
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
}
