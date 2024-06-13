import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../services/auth.service";

@Component({
  selector: 'app-landing-patient',
  templateUrl: './landing-patient.component.html',
  styleUrls: ['./landing-patient.component.scss', './../landing.scss']
})
export class LandingPatientComponent implements OnInit {

  constructor(public authService: AuthService) {
  }

  ngOnInit(): void {
  }

  scroll(el: HTMLElement) {
    el.scrollIntoView();
  }
}
