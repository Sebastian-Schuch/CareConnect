import {Component, OnInit} from '@angular/core';
import {AppointmentDetailDto} from "../../../dtos/appointment";
import {AppointmentService} from "../../../services/appointment.service";
import {UserService} from "../../../services/user.service";
import {ToastrService} from "ngx-toastr";
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';

@Component({
  selector: 'app-appointments-patient',
  templateUrl: './appointments-patient.component.html',
  styleUrls: ['./appointments-patient.component.scss']
})
export class AppointmentsPatientComponent implements OnInit {
  appointmentToBeCancelled: AppointmentDetailDto | undefined;
  patientAppointments: AppointmentDetailDto[] = [];
  futureAppointments: AppointmentDetailDto[] = [];
  pastAppointments: AppointmentDetailDto[] = [];
  outpatientDepartments: string[] = [];
  filteredDepartments: Observable<string[]>;
  departmentControl = new FormControl();
  showPastAppointments: boolean = false;

  constructor(
    private appointmentService: AppointmentService,
    private userService: UserService,
    private notification: ToastrService
  ) {
    this.filteredDepartments = this.departmentControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filterDepartments(value))
    );
  }

  ngOnInit(): void {
    this.loadPatientAppointments();
  }

  /**
   * Cancel the appointment
   */
  cancelAppointment(): void {
    if (this.appointmentToBeCancelled?.id != null) {
      this.appointmentService.cancelAppointment(this.appointmentToBeCancelled.id).subscribe({
        next: () => {
          this.notification.success(`Appointment successfully cancelled.`);
          this.loadPatientAppointments();
        },
        error: error => {
          console.error('Error deleting appointment', error);
          this.notification.error("Could not cancel Appointment");
        }
      });
    }
  }

  /**
   * toggle if past appointments should be shown
   */
  togglePastAppointments(): void {
    this.showPastAppointments = !this.showPastAppointments;
  }

  /**
   * Filter appointments by department
   * @param department the department to filter by
   */
  filterAppointments(department: string): void {
    if (department) {
      this.futureAppointments = this.patientAppointments.filter(appointment =>
        appointment.startDate > new Date() && appointment.outpatientDepartment.name === department
      );
      this.pastAppointments = this.patientAppointments.filter(appointment =>
        appointment.startDate <= new Date() && appointment.outpatientDepartment.name === department
      );
    } else {
      this.futureAppointments = this.patientAppointments.filter(appointment => appointment.startDate > new Date());
      this.pastAppointments = this.patientAppointments.filter(appointment => appointment.startDate <= new Date());
    }
  }

  /**
   * Load the appointments for the patient
   */
  private loadPatientAppointments(): void {
    this.userService.getPatientLoggedIn().subscribe({
      next: (patient) => {
        this.appointmentService.getAppointmentsFromPatient(patient.id).subscribe({
          next: (appointments) => {
            this.patientAppointments = appointments.map(appointment => {
              appointment.startDate = new Date(appointment.startDate);
              appointment.endDate = new Date(appointment.endDate);
              return appointment;
            });

            this.sortAppointments();
            this.futureAppointments = this.patientAppointments.filter(appointment => appointment.startDate > new Date());
            this.pastAppointments = this.patientAppointments.filter(appointment => appointment.startDate <= new Date());

            this.outpatientDepartments = [...new Set(this.patientAppointments.map(appointment => appointment.outpatientDepartment.name))];
            this.filteredDepartments = this.departmentControl.valueChanges.pipe(
              startWith(''),
              map(value => this._filterDepartments(value))
            );
          },
          error: (err) => {
            console.error('Error loading appointments', err);
          }
        });
      },
      error: (err) => {
        console.error('Error fetching patient details', err);
      }
    });
  }

  /**
   * Sort the appointments by start date
   */
  private sortAppointments(): void {
    this.patientAppointments.sort((a, b) => a.startDate.getTime() - b.startDate.getTime());
  }

  /**
   * Filter the outpatient departments
   * @param value the value to filter by
   */
  private _filterDepartments(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.outpatientDepartments.filter(department => department.toLowerCase().includes(filterValue));
  }
}
