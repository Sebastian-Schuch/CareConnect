import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../services/auth.service";
import {Router, RouterLink} from "@angular/router";
import {MatCard} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";
import {AllergyService} from "../../../services/allergy.service";
import {MedicationService} from "../../../services/medication.service";
import {OutpatientDepartmentService} from "../../../services/outpatient-department.service";
import {forkJoin} from "rxjs";
import {NgClass, NgIf} from "@angular/common";

@Component({
  selector: 'app-landing-admin',
  standalone: true,
  imports: [
    MatCard,
    MatIcon,
    RouterLink,
    MatButton,
    NgClass,
    NgIf
  ],
  templateUrl: './landing-admin.component.html',
  styleUrls: ['./landing-admin.component.scss', '../landing.scss', './../../../../styles.scss']
})
export class LandingAdminComponent implements OnInit {

  constructor(
    public authService: AuthService, private router: Router,
    public allergieService: AllergyService,
    public medicationService: MedicationService,
    public departmentService: OutpatientDepartmentService
  ) {
  }

  public setupActive: boolean = true;
  public meds=0;
  public allergies=0;
  public stations=0;

  ngOnInit() {
    forkJoin({
      allergiesCount: this.allergieService.countAllergies(),
      medicationsCount: this.medicationService.getMedicationCount(),
      departmentsCount: this.departmentService.getOutpatientDepartmentCount()
    }).subscribe({
      next: ({ allergiesCount, medicationsCount, departmentsCount }) => {
        this.allergies = allergiesCount;
        this.meds = medicationsCount;
        this.stations = departmentsCount;

        if (allergiesCount > 0 && medicationsCount > 0 && departmentsCount > 0) {
          this.setupActive = false;
        }

      },
      error: err => {
        console.error('Error fetching counts:', err);
      }
    });
  }

  scroll(el: HTMLElement) {
    el.scrollIntoView();
  }
}
