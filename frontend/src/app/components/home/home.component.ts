import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Role} from "../../dtos/Role";
import {CalendarWrapperComponent} from "../appointments/calender/calendar-wrapper/calendar-wrapper.component";
import {Router} from "@angular/router";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  mode: Role = Role.admin;

  constructor(public authService: AuthService, private router: Router) {
  }

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.loadRole();
    }
  }

  loadRole(): void {
    this.mode = this.authService.getUserRole();
  }

  isRoleAdmin(): boolean {
    if (this.mode == Role.admin) return true;
  }

  isRoleDoctor(): boolean {
    if (this.mode == Role.doctor) return true;
  }

  isRoleSecretary(): boolean {
    if (this.mode == Role.secretary) return true;
  }

  isRolePatient(): boolean {
    if (this.mode == Role.patient) return true;
  }

  protected readonly Role = Role;
  protected readonly CalendarWrapperComponent = CalendarWrapperComponent;

  changeToCalendar() {
    this.router.navigate(['/calendar']);
  }
}
