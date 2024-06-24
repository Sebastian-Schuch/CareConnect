import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { catchError, debounceTime, Observable, of, switchMap } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { AppointmentService } from '../../../services/appointment.service';
import { OutpatientDepartmentService } from '../../../services/outpatient-department.service';
import { AppointmentDto, AppointmentPageDto } from '../../../dtos/appointment';
import { OutpatientDepartmentDto, OutpatientDepartmentPageDto } from '../../../dtos/outpatient-department';
import { ToastrService } from 'ngx-toastr';
import { getDate, getMonth, getYear } from "date-fns";
import { UserDto } from "../../../dtos/user";
import { UserService } from "../../../services/user.service";
import { Page } from "../../../dtos/page";

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
  patients: UserDto[] = [];
  filteredDepartments: Observable<OutpatientDepartmentDto[]>;
  filteredPatients: Observable<UserDto[]>;
  showPastAppointments: boolean = true;
  appointmentToBeCancelled: AppointmentDto | undefined;
  showFilters: boolean = true;
  appointmentFilterForm: FormGroup;
  pageSize: number = 3;
  currentPageFuture: number = 0;
  currentPagePast: number = 0;
  totalFutureAppointments: number = 0;
  totalPastAppointments: number = 0;
  @Input() customStyle!: boolean;

  constructor(
    private appointmentService: AppointmentService,
    private outpatientDepartmentService: OutpatientDepartmentService,
    private notification: ToastrService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.appointmentFilterForm = new FormGroup({
      departmentControl: new FormControl(''),
      startDateControl: new FormControl(null),
      endDateControl: new FormControl(null),
      patientControl: new FormControl(''),
    });

    this.initializeFiltering();
    this.filterAppointments();
  }

  getVisibleFutureAppointments(): AppointmentDto[] {
    return this.futureAppointments;
  }

  getVisiblePastAppointments(): AppointmentDto[] {
    return this.pastAppointments;
  }

  nextFuturePage(): void {
    this.currentPageFuture++;
    this.filterAppointments();
  }

  previousFuturePage(): void {
    if (this.currentPageFuture > 0) {
      this.currentPageFuture--;
      this.filterAppointments();
    }
  }

  nextPastPage(): void {
    this.currentPagePast++;
    this.filterAppointments();
  }

  previousPastPage(): void {
    if (this.currentPagePast > 0) {
      this.currentPagePast--;
      this.filterAppointments();
    }
  }

  public getDayString(day: any): string {
    return getDate(day) + '/' + (getMonth(day) + 1) + '/' + getYear(day);
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

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

   filterAppointments(): void {
    const selectedDepartment = this.appointmentFilterForm.get('departmentControl').value;
    const startDate = this.appointmentFilterForm.get('startDateControl').value;
    const endDate = this.appointmentFilterForm.get('endDateControl').value;
    const selectedPatient = this.appointmentFilterForm.get('patientControl').value;

    const departmentId = selectedDepartment.id;
    const patientId = selectedPatient.id;

    this.loadFutureAppointments(departmentId, patientId, startDate, endDate);
    if (this.showPastAppointments) {
      this.loadPastAppointments(departmentId, patientId, startDate, endDate);
    }
  }

  private loadFutureAppointments(departmentId: number | null = null, patientId: number | null = null, startDate: Date | null = null, endDate: Date | null = null): void {
    const futureStartDate = new Date();
    console.log(departmentId);
    this.appointmentService.getAllFilteredAppointments(patientId, departmentId, futureStartDate, endDate, this.currentPageFuture, this.pageSize).subscribe({
      next: (response: AppointmentPageDto) => {
        this.futureAppointments = response.appointments.map(appointment => {
          appointment.startDate = new Date(appointment.startDate);
          appointment.endDate = new Date(appointment.endDate);
          return appointment;
        });
        this.totalFutureAppointments = response.totalItems;
      },
      error: (err) => {
        console.error('Error loading appointments', err);
      }
    });
  }

  private loadPastAppointments(departmentId: number | null = null, patientId: number | null = null, startDate: Date | null = null, endDate: Date | null = null): void {
    const pastEndDate = new Date();
    this.appointmentService.getAllFilteredAppointments(patientId, departmentId, startDate, pastEndDate, this.currentPagePast, this.pageSize).subscribe({
      next: (response: AppointmentPageDto) => {
        this.pastAppointments = response.appointments.map(appointment => {
          appointment.startDate = new Date(appointment.startDate);
          appointment.endDate = new Date(appointment.endDate);
          return appointment;
        });
        this.totalPastAppointments = response.totalItems;
      },
      error: (err) => {
        console.error('Error loading appointments', err);
      }
    });
  }


  /**
   * Display the doctor in the select field with correct format
   * @param outpatientDepartment - the doctor as an object
   * @return string - the formatted doctor
   */
  displayOutPD(outpatientDepartment: any): string {
    return outpatientDepartment ? `${outpatientDepartment.name}` : '';
  }

  /**
   * Display the doctor in the select field with correct format
   * @param outpatientDepartment - the doctor as an object
   * @return string - the formatted doctor
   */
  displayPatient(patient: any): string {
    return patient ? `${patient.firstname + " " + patient.lastname}` : '';
  }

  private initializeFiltering(): void {
    this.filteredDepartments = this.appointmentFilterForm.get('departmentControl').valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      switchMap(value => this.loadFilteredOutPatDep(value))
    );

    this.filteredPatients = this.appointmentFilterForm.get('patientControl').valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      switchMap(value => this.loadFilteredPatients(value))
    );
  }

  private loadFilteredOutPatDep(value: string | null | undefined): Observable<OutpatientDepartmentDto[]> {
    const filterValue = this.getStringValue(value).toLowerCase();
    return this.outpatientDepartmentService.getOutpatientDepartmentPage(filterValue, 0, 50).pipe(
      map((page: OutpatientDepartmentPageDto) => page.outpatientDepartments)
    );
  }

  private loadFilteredPatients(value: string | null | undefined): Observable<UserDto[]> {
    const filterValue = this.getStringValue(value).toLowerCase();
    return this.userService.getPatients(filterValue, 0, 50).pipe(
      map((page: Page<UserDto>) => page.content)
    );
  }

  private getStringValue(value: any): string {
    if (typeof value === 'string') {
      return value;
    } else if (value && value.firstname && value.lastname) {
      return `${value.firstname} ${value.lastname}`;
    } else {
      return '';
    }
  }
}
