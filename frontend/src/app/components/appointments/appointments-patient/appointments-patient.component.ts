import { Component, Input, OnInit } from '@angular/core';
import { AppointmentDto, AppointmentPageDto } from "../../../dtos/appointment";
import { AppointmentService } from "../../../services/appointment.service";
import { UserService } from "../../../services/user.service";
import { ToastrService } from "ngx-toastr";
import { FormControl } from '@angular/forms';
import { debounceTime, Observable, switchMap } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { getDate, getMonth, getYear } from "date-fns";
import { OutpatientDepartmentDto, OutpatientDepartmentPageDto } from "../../../dtos/outpatient-department";
import { OutpatientDepartmentService } from "../../../services/outpatient-department.service";

@Component({
  selector: 'app-appointments-patient',
  templateUrl: './appointments-patient.component.html',
  styleUrls: ['./appointments-patient.component.scss']
})
export class AppointmentsPatientComponent implements OnInit {
  appointmentToBeCancelled: AppointmentDto | undefined;
  futureAppointments: AppointmentDto[] = [];
  pastAppointments: AppointmentDto[] = [];

  filteredDepartments: Observable<OutpatientDepartmentDto[]>;
  departmentControl = new FormControl();
  showPastAppointments: boolean = true;
  pageSize: number = 3;
  currentPageFuture: number = 0;
  currentPagePast: number = 0;
  totalFutureAppointments: number = 0;
  totalPastAppointments: number = 0;
  @Input() customStyle!: boolean;

  constructor(
    private appointmentService: AppointmentService,
    private userService: UserService,
    private notification: ToastrService,
    private outpatientDepartmentService: OutpatientDepartmentService
  ) {}

  ngOnInit(): void {
    this.filteredDepartments = this.departmentControl.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      switchMap(value => this.loadFilteredOutPatDep(value))
    );
    this.loadFutureAppointments();
    this.loadPastAppointments();
  }

  getVisibleFutureAppointments(): AppointmentDto[] {
    return this.futureAppointments;
  }

  getVisiblePastAppointments(): AppointmentDto[] {
    return this.pastAppointments;
  }

  nextFuturePage(): void {
    this.currentPageFuture++;
    this.loadFutureAppointments();
  }

  previousFuturePage(): void {
    if (this.currentPageFuture > 0) {
      this.currentPageFuture--;
      this.loadFutureAppointments();
    }
  }

  nextPastPage(): void {
    this.currentPagePast++;
    this.loadPastAppointments();
  }

  previousPastPage(): void {
    if (this.currentPagePast > 0) {
      this.currentPagePast--;
      this.loadPastAppointments();
    }
  }

  public getDayString(day: any): string {
    return getDate(day) + '/' + (getMonth(day) + 1) + '/' + getYear(day);
  }

  private loadFilteredOutPatDep(value: string | null | undefined): Observable<OutpatientDepartmentDto[]> {
    const filterValue = this.getStringValue(value).toLowerCase();
    return this.outpatientDepartmentService.getOutpatientDepartmentPage(filterValue, 0, 50).pipe(
      map((page: OutpatientDepartmentPageDto) => page.outpatientDepartments)
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

  /**
   * Cancel the appointment
   */
  cancelAppointment(): void {
    if (this.appointmentToBeCancelled?.id != null) {
      this.appointmentService.cancelAppointment(this.appointmentToBeCancelled.id).subscribe({
        next: () => {
          this.notification.success(`Appointment successfully cancelled.`);
          this.loadFutureAppointments();
          if (this.showPastAppointments) {
            this.loadPastAppointments();
          }
        },
        error: error => {
          console.error('Error deleting appointment', error);
          this.notification.error("Could not cancel Appointment");
        }
      });
    }
  }


  /**
   * Filter appointments by department
   */
  filterAppointments(department: OutpatientDepartmentDto): void {
    const departmentId = department ? department.id : null;
    this.loadFutureAppointments(departmentId);
    if (this.showPastAppointments) {
      this.loadPastAppointments(departmentId);
    }
  }

  /**
   * Load the future appointments for the patient
   */
  private loadFutureAppointments(departmentId: number | null = null): void {
    this.userService.getPatientCredentials().subscribe({
      next: (patient) => {
        this.appointmentService.getAppointmentsByPatient(patient.id, departmentId, new Date(), null, this.currentPageFuture, this.pageSize).subscribe({
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
      },
      error: (err) => {
        console.error('Error fetching patient details', err);
      }
    });
  }

  /**
   * Load the past appointments for the patient
   */
  private loadPastAppointments(departmentId: number | null = null): void {
    this.userService.getPatientCredentials().subscribe({
      next: (patient) => {
        this.appointmentService.getAppointmentsByPatient(patient.id, departmentId, null, new Date(), this.currentPagePast, this.pageSize).subscribe({
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
      },
      error: (err) => {
        console.error('Error fetching patient details', err);
      }
    });
  }

  /**
   * Display the outpatient department in the select field with correct format
   * @param outpatientDepartment - the outpatient department as an object
   * @return string - the formatted outpatient department
   */
  displayOutPD(outpatientDepartment: any): string {
    return outpatientDepartment ? `${outpatientDepartment.name}` : '';
  }
}
