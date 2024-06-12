import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {catchError, forkJoin, Observable, of} from 'rxjs';
import {map, startWith, tap} from 'rxjs/operators';
import {AppointmentService} from '../../../services/appointment.service';
import {OutpatientDepartmentService} from '../../../services/outpatient-department.service';
import {AppointmentDto} from '../../../dtos/appointment';
import {OutpatientDepartmentDto} from '../../../dtos/outpatient-department';
import {ToastrService} from 'ngx-toastr';
import {getDate, getMonth, getYear} from "date-fns";

@Component({
  selector: 'app-appointments-secretary',
  templateUrl: './appointments-secretary.component.html',
  styleUrls: ['./appointments-secretary.component.scss']
})
export class AppointmentsSecretaryComponent implements OnInit {
  appointments: AppointmentDto[] = [];
  futureAppointments: AppointmentDto[] = [];
  pastAppointments: AppointmentDto[] = [];
  outpatientDepartments: OutpatientDepartmentDto[] = [];
  filteredDepartments: Observable<OutpatientDepartmentDto[]>;
  filteredPatients: Observable<string[]>;
  showPastAppointments: boolean = false;
  appointmentToBeCancelled: AppointmentDto | undefined;
  patients: string[] = [];
  showFilters: boolean = true;
  appointmentFilterForm: FormGroup;
  pageSize: number = 3;
  currentPageFuture: number = 1;
  currentPagePast: number = 1;

  constructor(
    private appointmentService: AppointmentService,
    private outpatientDepartmentService: OutpatientDepartmentService,
    private notification: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.appointmentFilterForm = new FormGroup({
      departmentControl: new FormControl(''),
      startDateControl: new FormControl(null),
      endDateControl: new FormControl(null),
      patientControl: new FormControl(''),
    });

    forkJoin({
      departments: this.loadOutpatientDepartments(),
      patients: this.loadPatients()
    }).subscribe({
      next: () => {
        this.initializeFiltering();
        this.filterAppointments();
      },
      error: err => {
        console.error('Error loading initial data', err);
      }
    });
  }

  getVisibleFutureAppointments(): AppointmentDto[] {
    const startIndex = (this.currentPageFuture - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    return this.futureAppointments.slice(startIndex, endIndex);
  }

  getVisiblePastAppointments(): AppointmentDto[] {
    const startIndex = (this.currentPagePast - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    return this.pastAppointments.slice(startIndex, endIndex);
  }

  nextFuturePage(): void {
    if ((this.currentPageFuture * this.pageSize) < this.futureAppointments.length) {
      this.currentPageFuture++;
    }
  }

  previousFuturePage(): void {
    if (this.currentPageFuture > 1) {
      this.currentPageFuture--;
    }
  }

  public getDayString(day: any): string {
    return getDate(day) + '/' + (getMonth(day) + 1) + '/' + getYear(day)
  }

  nextPastPage(): void {
    if ((this.currentPagePast * this.pageSize) < this.pastAppointments.length) {
      this.currentPagePast++;
    }
  }

  previousPastPage(): void {
    if (this.currentPagePast > 1) {
      this.currentPagePast--;
    }
  }

  /**
   * toggle if filter inputs are shown
   */
  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  /**
   * Filter the appointments based on the filter form
   */
  filterAppointments(): void {
    const selectedDepartment = this.appointmentFilterForm.get('departmentControl').value;
    const startDate = this.appointmentFilterForm.get('startDateControl').value;
    const endDate = this.appointmentFilterForm.get('endDateControl').value;
    const selectedPatient = this.appointmentFilterForm.get('patientControl').value;

    this.appointmentService.getAppointments().subscribe({
      next: (appointments) => {
        this.appointments = appointments.map(appointment => {
          appointment.startDate = new Date(appointment.startDate);
          appointment.endDate = new Date(appointment.endDate);
          return appointment;
        });

        let filteredAppointments = this.appointments;

        if (selectedDepartment) {
          filteredAppointments = filteredAppointments.filter(appointment =>
            appointment.outpatientDepartment.name === selectedDepartment
          );
        }

        if (startDate) {
          filteredAppointments = filteredAppointments.filter(appointment =>
            appointment.startDate >= new Date(startDate)
          );
        }

        if (endDate) {
          filteredAppointments = filteredAppointments.filter(appointment =>
            appointment.endDate <= new Date(endDate)
          );
        }

        if (selectedPatient) {
          filteredAppointments = filteredAppointments.filter(appointment =>
            (appointment.patient.firstname + ' ' + appointment.patient.lastname + ', ' + appointment.patient.svnr).includes(selectedPatient)
          );
        }

        this.futureAppointments = filteredAppointments.filter(appointment => appointment.startDate > new Date());
        this.pastAppointments = filteredAppointments.filter(appointment => appointment.startDate <= new Date());
        this.sortAppointments();
      },
      error: (err) => {
        console.error('Error loading appointments', err);
      }
    });
  }

  /**
   * Cancel an appointment
   */
  cancelAppointment(): void {
    if (this.appointmentToBeCancelled?.id != null) {
      this.appointmentService.cancelAppointment(this.appointmentToBeCancelled.id).subscribe({
        next: () => {
          this.notification.success(`Appointment successfully cancelled.`);
          this.filterAppointments();
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
   * Initialize the filtering for departments and patients with ''
   */
  private initializeFiltering(): void {
    this.filteredDepartments = this.appointmentFilterForm.get('departmentControl').valueChanges.pipe(
      startWith(''),
      map(value => this._filterDepartments(value))
    );

    this.filteredPatients = this.appointmentFilterForm.get('patientControl').valueChanges.pipe(
      startWith(''),
      map(value => this._filterPatients(value))
    );
  }

  /**
   * Load the appointments for the patient
   * @return observable of the appointments
   */
  private loadOutpatientDepartments(): Observable<OutpatientDepartmentDto[]> {
    return this.outpatientDepartmentService.getOutpatientDepartment().pipe(
      tap(departments => this.outpatientDepartments = departments),
      catchError(err => {
        console.error('Error loading outpatient departments', err);
        return of([]);
      })
    );
  }

  /**
   * Load the patients for the appointments
   * @return observable of the patients
   */
  private loadPatients(): Observable<string[]> {
    return this.appointmentService.getAppointments().pipe(
      map(appointments => {
        return Array.from(new Set(appointments.map(appointment =>
          appointment.patient.firstname + ' ' + appointment.patient.lastname + ', ' + appointment.patient.svnr
        )));
      }),
      tap(patients => this.patients = patients),
      catchError(err => {
        console.error('Error loading patients', err);
        return of([]);
      })
    );
  }

  /**
   * Sort the appointments by start date
   */
  private sortAppointments(): void {
    this.appointments.sort((a, b) => a.startDate.getTime() - b.startDate.getTime());
  }

  /**
   * Filter appointments by department
   * @param value the value to filter by
   * @return the filtered departments
   */
  private _filterDepartments(value: string): OutpatientDepartmentDto[] {
    const filterValue = value.toLowerCase();
    return this.outpatientDepartments.filter(department => department.name?.toLowerCase().includes(filterValue));
  }

  /**
   * Filter appointments by patient
   * @param value the value to filter by
   * @return the filtered patients
   */
  private _filterPatients(value: string): string[] {
    const filterValue = value ? value.toLowerCase() : '';
    return this.patients.filter(patient => patient.toLowerCase().includes(filterValue));
  }
}
