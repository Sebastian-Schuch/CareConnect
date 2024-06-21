import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Role} from "../../dtos/Role";
import {UserDto} from "../../dtos/user";
import {UserService} from "../../services/user.service";
import {ToastrService} from "ngx-toastr";
import {debounceTime, fromEvent} from "rxjs";
import {end, right} from "@popperjs/core";
import {MatDialog} from "@angular/material/dialog";
import {
  ChangePasswordFormModalComponent
} from "../user/change-password-form-modal/change-password-form-modal.component";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  name: string = ''
  isHandheld: boolean = false;
  user: UserDto;

  constructor(public authService: AuthService, private userService: UserService, private notification: ToastrService, private dialog: MatDialog, private router: Router) {
  }

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.loadUser();
    }
    this.authService.cast.subscribe(isLogged => {
      if (!isLogged) {
        this.name = '';
      } else {
        this.loadUser();
      }
    });
    fromEvent(window, 'resize').pipe(debounceTime(20)).subscribe(() => this.isHandset());
    this.isHandset();
  }

  private loadUser() {
    let observable = this.getCorrectObservable();
    observable.subscribe({
      next: (user: UserDto) => {
        this.user = user;
        this.formatName(user);
        this.initialPassword();
      },
      error: error => {
        this.notification.error('Error', 'Error loading user credentials', error);
        console.error(error);
      }
    });
  }

  private initialPassword() {
    if (this.user.isInitialPassword) {
      this.dialog.open(ChangePasswordFormModalComponent, {
        width: '500px', data: {email: this.user.email, mode: 'initial'}, disableClose: true
      });
    }
  }

  public getRoleString() {
    switch (this.authService.getUserRole()) {
      case Role.admin:
        return 'Admin';
      case Role.doctor:
        return 'Doctor';
      case Role.secretary:
        return 'Secretary';
      case Role.patient:
        return 'Patient';
      default:
        return '?';
    }
  }

  private getCorrectObservable() {
    switch (this.authService.getUserRole()) {
      case Role.admin:
        return this.userService.getAdminCredentials();
      case Role.doctor:
        return this.userService.getDoctorCredentials()
      case Role.secretary:
        return this.userService.getSecretaryCredentials();
      case Role.patient:
        return this.userService.getPatientCredentials();
      default:
        return null;
    }
  }

  public isRolePatient(): boolean {
    return (this.authService.getUserRole() === Role.patient)
  }

  public isRoleSecretary(): boolean {
    return (this.authService.getUserRole() === Role.secretary)
  }

  public isRoleDoctor(): boolean {
    return (this.authService.getUserRole() === Role.doctor)
  }

  public isRoleAdmin(): boolean {
    return (this.authService.getUserRole() === Role.admin)
  }

  private formatName(user: UserDto) {
    this.name = (user.firstname + " " + user.lastname.charAt(0));
  }

  public isHandset() {
    this.isHandheld = window.innerWidth < 1100;
  }

  public getEditProfilePath() {
    if (this.user) {
      switch (this.authService.getUserRole()) {
        case Role.admin:
          return '/home/admin/' + this.user.id;
        case Role.doctor:
          return '/home/doctor/' + this.user.id;
        case Role.secretary:
          return '/home/secretary/' + this.user.id;
        case Role.patient:
          return '/home/patient/' + this.user.id;
        default:
          return '/';
      }
    } else {
      return '/';
    }

  }

  public patientNavigateToAppointmentBook() {
    this.router.navigate(['/home/patient/appointments/book']);
  }

  public patientNavigateToAppointment() {
    this.router.navigate(['/home/patient/appointments']);
  }

  public patientNavigateToOutpatientDepartment() {
    this.router.navigate(['/home/patient/outpatient-department']);
  }

  public patientNavigateToInpatientDepartment() {
    this.router.navigate(['/home/patient/inpatient-department']);
  }

  public patientNavigateToTelemedicine() {
    this.router.navigate(['/home/patient/telemedicine']);
  }

  public patientNavigateToTreatment() {
    this.router.navigate(['/home/patient/treatment']);
  }

  public patientNavigateToTreatmentSearch() {
    this.router.navigate(['/home/patient/treatment/search']);
  }

  public secretaryNavigateToAppointmentBook() {
    this.router.navigate(['/home/secretary/appointments/book']);
  }

  public secretaryNavigateToAppointment() {
    this.router.navigate(['/home/secretary/appointments']);
  }

  public secretaryNavigateToPatients() {
    this.router.navigate(['/home/secretary/patients']);
  }

  public secretaryNavigateToPatientsRegister() {
    this.router.navigate(['/home/secretary/patients/register']);
  }

  public secretaryNavigateToOutpatientDepartment() {
    this.router.navigate(['/home/secretary/outpatient-department']);
  }

  public secretaryNavigateToInpatientDepartment() {
    this.router.navigate(['/home/secretary/inpatient-department']);
  }

  public secretaryNavigateToTreatment() {
    this.router.navigate(['/home/secretary/treatment']);
  }

  public doctorNavigateToTreatmentLog() {
    this.router.navigate(['/home/doctor/treatment/log']);
  }

  public doctorNavigateToTelemedicine() {
    this.router.navigate(['/home/doctor/telemedicine']);
  }

  public doctorNavigateToOutpatientDepartment() {
    this.router.navigate(['/home/doctor/outpatient-department']);
  }

  public doctorNavigateToInpatientDepartment() {
    this.router.navigate(['/home/doctor/inpatient-department']);
  }

  public doctorNavigateToTreatment() {
    this.router.navigate(['/home/doctor/treatment']);
  }

  public adminNavigateToUsersAdmins() {
    this.router.navigate(['/home/admin/users/admins']);
  }

  public adminNavigateToUsersDoctors() {
    this.router.navigate(['/home/admin/users/doctors']);
  }

  public adminNavigateToUsersSecretaries() {
    this.router.navigate(['/home/admin/users/secretaries']);
  }

  public adminNavigateToRegisterAdmin() {
    this.router.navigate(['/home/admin/register/admin']);
  }

  public adminNavigateToRegisterDoctor() {
    this.router.navigate(['/home/admin/register/doctor']);
  }

  public adminNavigateToRegisterSecretary() {
    this.router.navigate(['/home/admin/register/secretary']);
  }

  public adminNavigateToRegisterOutpatientDepartment() {
    this.router.navigate(['/home/admin/register/outpatient-department']);
  }

  public adminNavigateToRegisterInpatientDepartment() {
    this.router.navigate(['/home/admin/register/inpatient-department']);
  }

  public adminNavigateToOutpatientDepartment() {
    this.router.navigate(['/home/admin/outpatient-department']);
  }

  public adminNavigateToInpatientDepartment() {
    this.router.navigate(['/home/admin/inpatient-department']);
  }

  public adminNavigateToRegisterMedicine() {
    this.router.navigate(['/home/admin/register/medicine']);
  }

  public adminNavigateToRegisterAllergy() {
    this.router.navigate(['/home/admin/register/allergy']);
  }

  public userNavigateToEditProfile() {
    this.router.navigate([this.getEditProfilePath()]);
  }

  protected readonly Role = Role;
  protected readonly right = right;
  protected readonly end = end;
}
