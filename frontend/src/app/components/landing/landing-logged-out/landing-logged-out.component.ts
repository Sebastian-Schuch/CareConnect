import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../services/auth.service";
import {Router, RouterLink} from "@angular/router";
import {Role} from "../../../dtos/Role";
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'app-landing-logged-out',
  standalone: true,
  imports: [
    MatButton,
    RouterLink
  ],
  templateUrl: './landing-logged-out.component.html',
  styleUrls: ['./landing-logged-out.component.scss', './../landing.scss']
})
export class LandingLoggedOutComponent implements OnInit {

  constructor(public authService: AuthService, private router: Router) {
  }

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      switch (this.authService.getUserRole()) {
        case Role.admin:
          this.router.navigate(['/home/admin']);
          break;
        case Role.doctor:
          this.router.navigate(['/home/doctor']);
          break;
        case Role.secretary:
          this.router.navigate(['/home/secretary']);
          break;
        case Role.patient:
          this.router.navigate(['/home/patient']);
          break;
      }
    }
  }
}
