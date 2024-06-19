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

  constructor(
    public authService: AuthService, private router: Router,
    public allergieService: AllergyService,
    public medicationService: MedicationService,
    public departmentService: OutpatientDepartmentService
  ) {
  }

  public setupActive: boolean = true;


  ngOnInit() {
    console
  let allergiesInitialised: boolean = false;
  let medicationsInitialised: boolean = false;
  let departmentsInitialised: boolean = false;
    console.log("initialising landing admin component");
    forkJoin({
      allergiesCount: this.allergieService.countAllergies(),
      medicationsCount: this.medicationService.getMedicationCount(),
      departmentsCount: this.departmentService.getOutpatientDepartmentCount()
    }).subscribe({
      next: ({ allergiesCount, medicationsCount, departmentsCount }) => {
        if (allergiesCount > 0 && medicationsCount > 0 && departmentsCount > 0) {
          this.setupActive = false;
        }
      },
      error: err => {
        console.error('Error fetching counts:', err);
      }
    });
  }
}
