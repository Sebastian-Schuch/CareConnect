import {Component, OnInit} from '@angular/core';
import {UserCreateDto} from "../../dtos/user";
import {UserService} from "../../services/user.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormControl, FormGroup, NgModel, Validators} from "@angular/forms";
import {forkJoin, map, Observable, startWith} from "rxjs";
import {Role} from "../../dtos/Role";
import {ToastrService} from 'ngx-toastr';
import {MedicationDto} from "../../dtos/medication";
import {MedicationService} from "../../services/medication.service";
import {AllergyDetailDto} from "../../dtos/allergy";
import {AllergyService} from "../../services/allergy.service";

//import {ErrorFormatterService} from "../../../service/error-formatter.service";

@Component({
  selector: 'app-user-create',
  templateUrl: './user-create.component.html',
  styleUrl: './user-create.component.scss'
})


export class UserCreateComponent implements OnInit {

  mode: Role = null;

  user: UserCreateDto = {
    email: '',
    firstname: '',
    lastname: ''
  };

  selectedMedicationOptions: MedicationDto[] = [];
  filteredMedicationOptions: Observable<MedicationDto[]>;
  medicationOptions: MedicationDto[] = [];

  selectedAllergyOptions: AllergyDetailDto[] = [];
  filteredAllergyOptions: Observable<AllergyDetailDto[]>;
  allergyOptions: AllergyDetailDto[] = [];

  userForm: FormGroup;

  constructor(
    private service: UserService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private medicationService: MedicationService,
    private allergyService: AllergyService
    //private errorFormatter: ErrorFormatterService
  ) {
  }

  public get role(): string {
    switch (this.mode) {
      case Role.admin:
        return 'Admin';
      case Role.doctor:
        return 'Doctor';
      case Role.secretary:
        return 'Secretary';
      case Role.patient:
        return 'Patient';
      default:
        return '?';
    }
  }

  get modeIsAdmin(): boolean {
    return this.mode === Role.admin;
  }

  get modeIsDoctor(): boolean {
    return this.mode === Role.doctor;
  }

  get modeIsSecretary(): boolean {
    return this.mode === Role.secretary;
  }

  get modeIsPatient(): boolean {
    return this.mode === Role.patient;
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      if (data.role != null) {
        this.mode = data.role;
        this.generateForm();
        forkJoin({
          allergies: this.loadAllergies(),
          medications: this.loadMedications()
        }).subscribe({
          next: ({allergies, medications}) => {
            this.allergyOptions = allergies;
            this.medicationOptions = medications;
            this.resetAllSearchInputs();
          },
          error: error => {
            console.error('error while getting data from database', error);
          }
        });
      }
    });
  }

  /**
   * Generates the forms for creating a new treatment and adding a new medication
   */
  private generateForm() {
    this.userForm = new FormGroup({
      firstname: new FormControl('', Validators.required),
      lastname: new FormControl('', Validators.required),
      email: new FormControl('', Validators.required),
      svnr: this.getCorrectFormControl(),
      medication: new FormControl(''),
      allergy: new FormControl(''),
    });
  }

  private getCorrectFormControl(): FormControl {
    if (this.mode === Role.patient) {
      return new FormControl('', Validators.required);
    }
    return new FormControl('');
  }

  private resetAllSearchInputs() {
    this.filteredMedicationOptions = this.userForm.get('medication').valueChanges.pipe(
      startWith(''),
      map(value => this.filterMedications(value))
    );

    this.filteredAllergyOptions = this.userForm.get('allergy').valueChanges.pipe(
      startWith(''),
      map(value => this.filterAllergies(value))
    );
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  /**
   * helper method to add a selected medication to the selection
   * resets the input field after adding the medication
   * @param medication the medication to add
   */
  addMedicationToSelection(medication: MedicationDto): void {
    if (!this.selectedMedicationOptions.some(d => d.id === medication.id)) {
      this.selectedMedicationOptions.push(medication);
    }
    this.userForm.get('medication').setValue('');
    this.filteredMedicationOptions = this.userForm.get('medication').valueChanges.pipe(
      startWith(''),
      map(value => this.filterMedications(value))
    );
  }

  /**
   * helper method to add a selected allergy to the selection
   * resets the input field after adding the allergy
   * @param allergy the allergy to add
   */
  addAllergyToSelection(allergy: AllergyDetailDto): void {
    if (!this.selectedAllergyOptions.some(a => a.id === allergy.id)) {
      this.selectedAllergyOptions.push(allergy);
    }
    this.userForm.get('allergy').setValue('');
    this.filteredAllergyOptions = this.userForm.get('allergy').valueChanges.pipe(
      startWith(''),
      map(value => this.filterAllergies(value))
    );
  }

  /**
   * Loads all medications from the backend
   * @returns an observable with all medications
   */
  private loadMedications(): Observable<any> {
    return this.medicationService.getMedicationsAll();
  }

  /**
   * Loads all allergies from the backend
   * @returns an observable with all allergies
   */
  private loadAllergies(): Observable<any> {
    return this.allergyService.getAllergiesAll();
  }

  /**
   * helper method to remove a medication from the selection
   * @param medication the medication to remove
   */
  removeMedicationFromSelection(medication: MedicationDto): void {
    const index = this.selectedMedicationOptions.findIndex(m => m.id === medication.id);
    if (index >= 0) {
      this.selectedMedicationOptions.splice(index, 1);
    }
  }

  /**
   * helper method to remove an allergy from the selection
   * @param allergy the allergy to remove
   */
  removeAllergyFromSelection(allergy: AllergyDetailDto): void {
    const index = this.selectedAllergyOptions.findIndex(a => a.id === allergy.id);
    if (index >= 0) {
      this.selectedAllergyOptions.splice(index, 1);
    }
  }

  /**
   * Filters the medications based on the input value
   * @param value the input value to filter for
   * @returns the filtered medications
   */
  private filterMedications(value: string): MedicationDto[] {
    const filterValue = value.toString().toLowerCase();
    return this.medicationOptions.filter(option =>
      option.name.toLowerCase().includes(filterValue)
    );
  }

  /**
   * Filters the allergies based on the input value
   * @param value the input value to filter for
   * @returns the filtered allergies
   */
  private filterAllergies(value: string): MedicationDto[] {
    const filterValue = value.toString().toLowerCase();
    return this.allergyOptions.filter(option =>
      option.name.toLowerCase().includes(filterValue)
    );
  }

  public submitForm(): void {
    this.user.medicines = this.selectedMedicationOptions;
    this.user.allergies = this.selectedAllergyOptions;
    this.user.email = this.userForm.get('email').value;
    this.user.firstname = this.userForm.get('firstname').value;
    this.user.lastname = this.userForm.get('lastname').value;
    this.user.svnr = this.userForm.get('svnr').value;
    if (this.userForm.valid) {
      let observable: Observable<Blob>;
      switch (this.mode) {
        case Role.admin:
          observable = this.service.createAdmin(this.user);
          break;
        case Role.doctor:
          observable = this.service.createDoctor(this.user);
          break;
        case Role.secretary:
          observable = this.service.createSecretary(this.user);
          break;
        case Role.patient:
          observable = this.service.createPatient(this.user);
          break;
        default:
          console.error('Unknown Role', this.mode);
          return;
      }
      observable.subscribe({
        next: (response) => {
          this.notification.success(`${this.role} ${this.user.firstname} ${this.user.lastname} successfully created.`);

          const url = window.URL.createObjectURL(response);
          window.open(url);
          //this.router.navigate(['/dashboard']);
        },
        error: error => {
          console.error('Error creating User', error);
          this.notification.error(`Could not create ${this.role}`);
        }
      });
    }

  }


}
