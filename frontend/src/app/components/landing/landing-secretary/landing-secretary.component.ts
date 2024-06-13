import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../services/auth.service";
import {RouterLink} from "@angular/router";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-landing-secretary',
  standalone: true,
  imports: [
    MatButton,
    RouterLink,
    MatIcon
  ],
  templateUrl: './landing-secretary.component.html',
  styleUrls: ['./landing-secretary.component.scss', './../landing.scss']
})
export class LandingSecretaryComponent implements OnInit {
  constructor(public authService: AuthService) {
  }

  ngOnInit() {
  }

  scroll(el: HTMLElement) {
    el.scrollIntoView();
  }
}
