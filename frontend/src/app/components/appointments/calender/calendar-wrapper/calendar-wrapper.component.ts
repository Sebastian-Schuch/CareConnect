import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {OutpatientDepartmentDto, OutpatientDepartmentPageDto} from "../../../../dtos/outpatient-department";
import {debounceTime, forkJoin, map, Observable, startWith, switchMap} from "rxjs";
import {OutpatientDepartmentService} from "../../../../services/outpatient-department.service";
import {ToastrService} from "ngx-toastr";
import {Role} from "../../../../dtos/Role";
import {UserDto} from "../../../../dtos/user";
import {UserService} from "../../../../services/user.service";
import {AuthService} from "../../../../services/auth.service";
import {Page} from "../../../../dtos/page";
import {MatAutocompleteTrigger} from "@angular/material/autocomplete";

@Component({
  selector: 'app-calendar-wrapper',
  templateUrl: './calendar-wrapper.component.html',
  styleUrl: './calendar-wrapper.component.scss'
})
export class CalendarWrapperComponent implements OnInit {
  mode: Role;
  appointmentForm: FormGroup;
  filteredOutPatDep: Observable<OutpatientDepartmentDto[]>;
  filteredPatientOptions: Observable<UserDto[]>;
  patient: UserDto;

  constructor(
    private outpatientDepartmentService: OutpatientDepartmentService,
    private notification: ToastrService,
    private userService: UserService,
    private authService: AuthService) {
  }
  @ViewChild('patientInput', {static: false}) patientInput: ElementRef<HTMLInputElement>;
  @ViewChild('outpatientDepartmentInput', {static: false}) outpatientDepartmentInput: ElementRef<HTMLInputElement>;
  @ViewChild('patientTrigger', {static: false}) patientAutoTrigger: MatAutocompleteTrigger;
  @ViewChild('outpatientDepartmentTrigger', {static: false}) outpdepAutoTrigger: MatAutocompleteTrigger;
  ngOnInit(): void {
    this.mode = this.authService.getUserRole();
    if (this.mode == Role.secretary) {
      this.appointmentForm = new FormGroup({
        outpatientDepartment: new FormControl('', Validators.required),
        patient: new FormControl('')
      });
    }
    if (this.mode == Role.patient) {
      this.appointmentForm = new FormGroup({
        outpatientDepartment: new FormControl('', Validators.required)
      });
    }
    this.resetAllSearchInputs();
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Backspace') {
      if (event.target === this.patientInput.nativeElement) {
        this.appointmentForm.get('patient')?.setValue('');
        setTimeout(() => {
          if (this.patientAutoTrigger) {
            this.patientAutoTrigger.openPanel();
          }
        });
      }
      if (event.target === this.outpatientDepartmentInput.nativeElement) {
        this.appointmentForm.get('outpatientDepartment')?.setValue('');
        setTimeout(() => {
          if (this.outpdepAutoTrigger) {
            this.outpdepAutoTrigger.openPanel();
          }
        });
      }
    }
  }

  private loadFilteredPatients(value: string | null | undefined): Observable<UserDto[]> {
    const filterValue = this.getStringValue(value).toLowerCase();
    return this.userService.getPatients(filterValue).pipe(
      map((page: Page<UserDto>) => page.content)
    );
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
   * Checks if the current user is a secretary
   */
  isRoleSecretary(): boolean {
    return this.mode === Role.secretary;
  }

  /**
   * Checks if the current user is a secretary
   */
  isRolePatient(): boolean {
    return this.mode === Role.patient;
  }

  /**
   * Resets all search inputs
   *
   * @private helper method to reset all search inputs
   */
  private resetAllSearchInputs() {

    this.filteredOutPatDep = this.appointmentForm.get('outpatientDepartment').valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      switchMap(value => this.loadFilteredOutPatDep(value))
    );
    if (this.isRoleSecretary()) {
      this.filteredPatientOptions = this.appointmentForm.get('patient').valueChanges.pipe(
        startWith(''),
        debounceTime(300),
        switchMap(value => this.loadFilteredPatients(value))
      );
    }
  }

  /**
   * helper method to correctly display the outpatient department name in frontend
   * @param outpd the outpatient department object
   */

  displayOutPD(outpd: any): string {
    return outpd ? `${outpd.name}` : '';
  }

  /**
   * helper method to correctly display the patient name in frontend
   * @param patient the patient object
   */
  displayPatient(patient: any): string {
    return patient ? `${patient.firstname} ${patient.lastname}` : '';
  }

  /**
   * Checks if the outpatient department is set
   */
  isOutpatientDepartmentSet(): boolean {
    return typeof this.GetActiveOutpatientDepartment() !== 'string';
  }

  /**
   * Get the outpatient department
   */
  GetActiveOutpatientDepartment(): OutpatientDepartmentDto {
    return this.appointmentForm.get('outpatientDepartment').value;
  }

  /**
   * Get the active patient
   */
  GetActivePatient(): UserDto {
    if (this.isRoleSecretary()) {
      if (typeof this.appointmentForm.get('patient').value === 'string') {
        return null;
      } else {
        return this.appointmentForm.get('patient').value;
      }

    } else {
      return this.patient;
    }
  }
}
