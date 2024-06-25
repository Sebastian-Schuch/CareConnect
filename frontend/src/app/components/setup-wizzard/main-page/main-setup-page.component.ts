import {AfterViewInit, Component, OnInit} from '@angular/core';
import {AllergyService} from "../../../services/allergy.service";
import {ToastrService} from "ngx-toastr";
import {AllergyDto} from "../../../dtos/allergy";
import {MedicationService} from "../../../services/medication.service";
import {MedicationDto} from "../../../dtos/medication";
import {MedicationCreateEditMode} from "../../medication/medication-create/medication-create.component";
import {AllergyCreatEditMode} from "../../allergy/allergy.component";
import {ErrorFormatterService} from "../../../services/error-formatter.service";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-setup-page.component.html',
  styleUrl: './main-setup-page.component.scss',
})
export class MainSetupPage implements OnInit, AfterViewInit {

  showFileUpload = false;
  public allergies: AllergyDto[] = [];
  public medication: MedicationDto[] = [];
  public error: string | null = null;

  constructor(
    private allergyService: AllergyService,
    private notification: ToastrService,
    private medService: MedicationService,
    private errorFormatterService: ErrorFormatterService,
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

  loadMedication() {
    this.medService.getMedicationsAll().subscribe({
      next: data => {
        console.log(data);
        this.medication = data;
      },
      error: async error => {
        await this.errorFormatterService.printErrorToNotification(error, 'Error loading medications', this.notification);
      }
    });
  }

  loadAllergies(): void {
    this.allergyService.getAllergiesAll().subscribe({
      next: data => {
        this.allergies = data;
      },
      error: async error => {
        await this.errorFormatterService.printErrorToNotification(error, 'Error loading allergies', this.notification);
      }
    });
  }

  onAllergyCreated(): void {
    this.loadAllergies();
  }

  checkExists(name: string): boolean {
    return this.allergies.find(allergy => allergy.name === name) !== undefined;
  }

  onMedicationCreated() {
    this.loadMedication();
  }

  protected readonly MedicationCreateEditMode = MedicationCreateEditMode;
  protected readonly AllergyCreatEditMode = AllergyCreatEditMode;
}
