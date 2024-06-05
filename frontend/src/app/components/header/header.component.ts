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

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  name: string = ''
  isHandheld: boolean = false;
  user: UserDto;

  constructor(public authService: AuthService, private userService: UserService, private notification: ToastrService, private dialog: MatDialog) {
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
    if (this.authService.getUserRole() != Role.admin) {
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

  protected readonly Role = Role;
  protected readonly right = right;
  protected readonly end = end;
}
