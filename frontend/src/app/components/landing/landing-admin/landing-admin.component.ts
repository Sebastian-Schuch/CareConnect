import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../services/auth.service";
import {Router, RouterLink} from "@angular/router";
import {MatCard} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'app-landing-admin',
  standalone: true,
  imports: [
    MatCard,
    MatIcon,
    RouterLink,
    MatButton
  ],
  templateUrl: './landing-admin.component.html',
  styleUrls: ['./landing-admin.component.scss', '../landing.scss', './../../../../styles.scss']
})
export class LandingAdminComponent implements OnInit {

  constructor(public authService: AuthService, private router: Router) {
  }

  ngOnInit() {
  }
}
