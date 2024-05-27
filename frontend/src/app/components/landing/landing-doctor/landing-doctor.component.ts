import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../services/auth.service";
import {Router, RouterLink} from "@angular/router";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-landing-doctor',
  standalone: true,
  imports: [
    MatButton,
    MatIcon,
    RouterLink
  ],
  templateUrl: './landing-doctor.component.html',
  styleUrls: ['./landing-doctor.component.scss', './../landing.scss']
})
export class LandingDoctorComponent implements OnInit {
  constructor(public authService: AuthService, private router: Router) {
  }

  ngOnInit() {
  }
}
