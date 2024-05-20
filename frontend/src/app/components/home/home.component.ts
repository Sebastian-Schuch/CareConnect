import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Role} from "../../dtos/Role";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  mode: Role = Role.admin;

  constructor(public authService: AuthService) {
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
}
