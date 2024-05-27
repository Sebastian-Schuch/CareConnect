import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {OutpatientDepartmentDto} from "../../../dtos/outpatient-department";
import {forkJoin, map, Observable, startWith} from "rxjs";
import {OutpatientDepartmentService} from "../../../services/outpatient-department.service";
import {ToastrService} from "ngx-toastr";
import {Role} from "../../../dtos/Role";
import {UserDetailDto} from "../../../dtos/user";
import {UserService} from "../../../services/user.service";

@Component({
  selector: 'app-calendar-wrapper',
  templateUrl: './calendar-wrapper.component.html',
  styleUrl: './calendar-wrapper.component.scss'
})
export class CalendarWrapperComponent implements OnInit {
  @Input() mode: Role;
  appointmentForm: FormGroup;
  outpatientDepartments: OutpatientDepartmentDto[] = [];
  filteredOutPatDep: Observable<OutpatientDepartmentDto[]>;
  filteredPatientOptions: Observable<UserDetailDto[]>;
  patientOptions: UserDetailDto[] = [];
  patient: UserDetailDto;

  constructor(
    private outpatientDepartmentService: OutpatientDepartmentService,
    private notification: ToastrService,
    private userService: UserService) {
  }

  ngOnInit(): void {
    if (this.mode == Role.secretary) {
      this.appointmentForm = new FormGroup({
        outpatientDepartment: new FormControl('', Validators.required),
        patient: new FormControl('')
      });
      forkJoin({
        outpatientDepartment: this.outpatientDepartmentService.getOutpatientDepartment(),
        patients: this.userService.getAllPatients(),
      }).subscribe(({outpatientDepartment, patients}) => {
        this.patientOptions = patients;
        this.outpatientDepartments = outpatientDepartment;
        this.resetAllSearchInputs();
      });
    }
    if (this.mode == Role.patient) {
      this.appointmentForm = new FormGroup({
        outpatientDepartment: new FormControl('', Validators.required)
      });
      forkJoin({
        outpatientDepartment: this.outpatientDepartmentService.getOutpatientDepartment(),
        patient: this.userService.getUserCredentials(),
      }).subscribe(({outpatientDepartment, patient}) => {
        this.patient = patient;
        this.outpatientDepartments = outpatientDepartment;
        this.resetAllSearchInputs();
      });
    }
  }

  isRoleSecretary(): boolean {
    return this.mode === Role.secretary;
  }

  private resetAllSearchInputs() {
    this.filteredOutPatDep = this.appointmentForm.get('outpatientDepartment').valueChanges.pipe(
      startWith(''),
      map(value => this.filterOutPatDep(value))
    );
    if (this.isRoleSecretary()) {
      this.filteredPatientOptions = this.appointmentForm.get('patient').valueChanges.pipe(
        startWith(''),
        map(value => this.filterPatients(value))
      );
    }
  }


  /**
   * Filters the outpatient departments based on the input value
   * @param value the input value to filter for
   * @returns the filtered outpatient departments
   */

  private filterOutPatDep(value: string): OutpatientDepartmentDto[] {
    const filterValue = value.toString().toLowerCase();
    return this.outpatientDepartments.filter(option =>
      option.name.toLowerCase().includes(filterValue) ||
      option.description.toLowerCase().includes(filterValue)
    );
  }

  /**
   * Filters the patients based on the input value
   * @param value the input value to filter for
   * @returns the filtered patients
   */
  private filterPatients(value: string): UserDetailDto[] {
    const filterValue = value.toString().toLowerCase();
    return this.patientOptions.filter(option =>
      option.firstname.toString().toLowerCase().includes(filterValue) ||
      option.lastname.toString().toLowerCase().includes(filterValue) ||
      option.svnr.toString().includes(filterValue)
    );
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

  isActiveOutpatientDepartment(): boolean {
    return this.appointmentForm.get('outpatientDepartment').value != "";
  }

  GetActiveOutpatientDepartment(): OutpatientDepartmentDto {
    return this.appointmentForm.get('outpatientDepartment').value;
  }

  GetActivePatient(): UserDetailDto {
    if (this.isRoleSecretary()) {
      return this.appointmentForm.get('patient').value;
    } else {
      return this.patient;
    }
  }
}
