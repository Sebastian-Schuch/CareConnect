import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-landing-patient',
  templateUrl: './landing-patient.component.html',
  styleUrls: ['./landing-patient.component.scss', './../landing.scss']
})
export class LandingPatientComponent implements OnInit {

  constructor(public authService: AuthService, private router: Router) {
  }

  ngOnInit(): void {
  }


}
