import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Role} from "../../dtos/Role";
import {UserDto} from "../../dtos/user";
import {UserService} from "../../services/user.service";
import {ToastrService} from "ngx-toastr";
import {MatDrawerMode} from "@angular/material/sidenav";
import {debounceTime, fromEvent} from "rxjs";
import {BooleanInput} from "@angular/cdk/coercion";
import {end, right} from "@popperjs/core";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  name: string = ''
  isLogged: boolean = false;
  isHandheld: boolean = false;
  mdcBackdrop: BooleanInput = true;
  drawerMode: MatDrawerMode = "push";
  userId: number;

  constructor(public authService: AuthService, private userService: UserService, private notification: ToastrService) {
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
          this.userId = user.id;
          this.formatName(user);
        },
        error: error => {
          this.notification.error('Error', 'Error loading user credentials', error);
          console.error(error);
        }
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
    switch (this.authService.getUserRole()) {
      case Role.admin:
        return '/home/admin/' + this.userId;
      case Role.doctor:
        return '/home/doctor/' + this.userId;
      case Role.secretary:
        return '/home/secretary/' + this.userId;
      case Role.patient:
        return '/home/patient/' + this.userId;
      default:
        return '/';
    }
  }

  protected readonly Role = Role;
  protected readonly right = right;
  protected readonly end = end;
}
