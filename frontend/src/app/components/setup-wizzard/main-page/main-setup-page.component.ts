import { Component } from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {AllergyService} from "../../../services/allergy.service";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../services/error-formatter.service";
import {Router} from "@angular/router";
import {CsvConverterService} from "../../../services/csv-converter.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {AllergyDto} from "../../../dtos/allergy";
import {MedicationService} from "../../../services/medication.service";
import {MedicationDto} from "../../../dtos/medication";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-setup-page.component.html',
  styleUrl: './main-setup-page.component.scss'
})
export class MainSetupPage {

  showFileUpload = false;
  public allergies: AllergyDto[] = [];
  public medication: MedicationDto[] = [];
  public error: string | null = null;

  constructor(
    private allergyService: AllergyService,
    private notification: ToastrService,
    private medService: MedicationService,
  ) {
  }

  ngOnInit(): void {
    this.loadAllergies();
    this.loadMedication();
  }

  toggleView() {
    this.showFileUpload = !this.showFileUpload;
  }

  loadMedication(){
    this.medService.getMedicationsAll().subscribe({
      next: data => {
        this.medication = data;
        console.log("medicationdata",data);
      },
      error: error => {
        this.notification.error('Error loading medications: ' + error.message);
      }
    });
  }

  loadAllergies(): void {
    this.allergyService.getAllergiesAll().subscribe({
      next: data => {
        this.allergies = data;
        console.log("reloading allergies!", this.allergies);

      },
      error: error => {
        this.notification.error('Error loading allergies: ' + error.message);
      }
    });
  }

  onAllergyCreated(): void {
    console.log('Allergy was successfully created.');
    this.loadAllergies();
  }

  checkExists(name: string): boolean {
    return this.allergies.find(allergy => allergy.name === name) !== undefined;
  }

  onMedicationCreated() {
    this.loadMedication();
  }
}
