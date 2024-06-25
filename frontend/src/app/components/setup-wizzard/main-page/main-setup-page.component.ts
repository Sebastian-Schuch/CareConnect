import {Component, ViewEncapsulation} from '@angular/core';
import {AllergyService} from "../../../services/allergy.service";
import {ToastrService} from "ngx-toastr";
import {AllergyDto} from "../../../dtos/allergy";
import {MedicationService} from "../../../services/medication.service";
import {MedicationDto} from "../../../dtos/medication";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-setup-page.component.html',
  styleUrl: './main-setup-page.component.scss',
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

  ngAfterViewInit(): void {
    this.changeBackgroundColor();

  }
  changeActiveTab(): void {
    this.removePreviousActiveTabClass();
    this.changeBackgroundColor();
  }

  changeBackgroundColor(): void {
    const activeTab = document.getElementsByClassName('mdc-tab--active')[0];
    (activeTab as HTMLElement).style.backgroundColor = 'transparent';
    (activeTab as HTMLElement).classList.add('active-tab');
  }

  removePreviousActiveTabClass(): void {
    const previousActiveTab = document.getElementsByClassName('active-tab')[0];
    (previousActiveTab as HTMLElement).style.backgroundColor = 'unset';
    previousActiveTab.classList.remove('active-tab');
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
