import {Component, OnInit} from '@angular/core';
import {UserDtoCreate, UserDtoUpdate} from "../../../dtos/user";
import {UserService} from "../../../services/user.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormControl, FormGroup, NgModel, Validators} from "@angular/forms";
import {forkJoin, map, Observable, startWith} from "rxjs";
import {Role} from "../../../dtos/Role";
import {ToastrService} from 'ngx-toastr';
import {MedicationDto} from "../../../dtos/medication";
import {MedicationService} from "../../../services/medication.service";
import {AllergyDto} from "../../../dtos/allergy";
import {AllergyService} from "../../../services/allergy.service";
import {ErrorFormatterService} from "../../../services/error-formatter.service";
import {AuthService} from "../../../services/auth.service";
import {MatDialog} from "@angular/material/dialog";
import {ChangePasswordFormModalComponent} from "../change-password-form-modal/change-password-form-modal.component";

export enum UserCreateEditMode {
  create,
  edit,
  view
}

@Component({
  selector: 'app-user-create',
  templateUrl: './user-create.component.html',
  styleUrls: ['./user-create.component.scss', '../../../../styles.scss']
})


export class UserCreateComponent implements OnInit {

  role: Role = null;

  mode: UserCreateEditMode = null;

  userId: number = null;
  userEmail: string = null;

  create: UserDtoCreate = {
    email: '',
    firstname: '',
    lastname: ''
  };

  update: UserDtoUpdate = {
    email: '',
    firstname: '',
    lastname: ''
  }

  selectedMedicationOptions: MedicationDto[] = [];
  filteredMedicationOptions: Observable<MedicationDto[]>;
  medicationOptions: MedicationDto[] = [];

  selectedAllergyOptions: AllergyDto[] = [];
  filteredAllergyOptions: Observable<AllergyDto[]>;
  allergyOptions: AllergyDto[] = [];

  userForm: FormGroup;

  constructor(
    private service: UserService,
    private route: ActivatedRoute,
    private router: Router,
    private notification: ToastrService,
    private medicationService: MedicationService,
    private allergyService: AllergyService,
    private errorFormatterService: ErrorFormatterService,
    private authService: AuthService,
    private dialog: MatDialog
  ) {
  }

  public get roleString(): string {
    switch (this.role) {
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

  public get modeString(): string {
    switch (this.mode) {
      case UserCreateEditMode.create:
        return 'Register';
      case UserCreateEditMode.edit:
        return 'Edit';
      case UserCreateEditMode.view:
        return 'View';
      default:
        return '?';
    }
  }

  get roleIsAdmin(): boolean {
    return this.role === Role.admin;
  }

  get roleIsDoctor(): boolean {
    return this.role === Role.doctor;
  }

  get roleIsSecretary(): boolean {
    return this.role === Role.secretary;
  }

  get roleIsPatient(): boolean {
    return this.role === Role.patient;
  }

  get modeIsCreate(): boolean {
    return this.mode === UserCreateEditMode.create;
  }

  get modeIsEdit(): boolean {
    return this.mode === UserCreateEditMode.edit;
  }

  get modeIsView(): boolean {
    return this.mode === UserCreateEditMode.view;
  }

  get isOwnEdit(): boolean {
    return this.authService.getUserEmail() === this.userEmail;
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.route.params.subscribe(params => {
        if (data.role != null && data.mode != null) {
          this.userId = params['id'];
          this.role = data.role;
          this.mode = data.mode;
          this.generateForm();
          forkJoin({
            allergies: this.loadAllergies(),
            medications: this.loadMedications()
          }).subscribe({
            next: ({allergies, medications}) => {
              this.allergyOptions = allergies;
              this.medicationOptions = medications;
              this.resetAllSearchInputs();
              if (this.mode == UserCreateEditMode.edit || this.mode == UserCreateEditMode.view) {
                this.loadUser(this.userId);
              }
            },
            error: error => {
              this.notification.error('Loading of resources failed', 'Error loading resources');
              console.error('error while getting data from database', error);
            }
          });
        }
      });
    });
  }

  private loadUser(userId: number) {
    let observable;
    switch (this.role) {
      case Role.admin:
        observable = this.service.getAdminById(userId);
        break;
      case Role.doctor:
        observable = this.service.getDoctorById(userId);
        break;
      case Role.secretary:
        observable = this.service.getSecretaryById(userId);
        break;
      case Role.patient:
        observable = this.service.getPatientById(userId);
        break;
      default:
        console.error('Unknown Role', this.role);
        return;
    }
    observable.subscribe({
      next: user => {
        this.userEmail = user.email;
        this.userForm.get('email').setValue(user.email);
        this.userForm.get('firstname').setValue(user.firstname);
        this.userForm.get('lastname').setValue(user.lastname);
        this.userForm.get('svnr').setValue(user.svnr);
        this.selectedMedicationOptions = user.medicines;
        this.selectedAllergyOptions = user.allergies;
      },
      error: error => {
        this.notification.error('Loading of user failed', 'Error loading user');
        console.error('error while getting data from database', error);
      }
    })
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
    if (this.modeIsView) {
      this.userForm.disable()
    }
  }

  private getCorrectFormControl(): FormControl {
    if (this.role === Role.patient) {
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
  addAllergyToSelection(allergy: AllergyDto): void {
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
  removeAllergyFromSelection(allergy: AllergyDto): void {
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

  openPasswordModal() {
    this.dialog.open(ChangePasswordFormModalComponent, {
      width: '500px', data: this.userEmail
    });
  }

  public submitForm(): void {
    switch (this.mode) {
      case UserCreateEditMode.create:
        if (this.userForm.valid) {
          this.create.medicines = this.selectedMedicationOptions;
          this.create.allergies = this.selectedAllergyOptions;
          this.create.email = this.userForm.get('email').value;
          this.create.firstname = this.userForm.get('firstname').value;
          this.create.lastname = this.userForm.get('lastname').value;
          this.create.svnr = this.userForm.get('svnr').value;
          let observable: Observable<Blob>;
          switch (this.role) {
            case Role.admin:
              observable = this.service.createAdmin(this.create);
              break;
            case Role.doctor:
              observable = this.service.createDoctor(this.create);
              break;
            case Role.secretary:
              observable = this.service.createSecretary(this.create);
              break;
            case Role.patient:
              observable = this.service.createPatient(this.create);
              break;
            default:
              console.error('Unknown Role', this.role);
              return;
          }
          observable.subscribe({
            next: (response) => {
              this.notification.success(`${this.roleString} ${this.create.firstname} ${this.create.lastname} successfully created.`);

              const url = window.URL.createObjectURL(response);
              window.open(url);
              this.router.navigate(['/']);
            },
            error: async error => {
              switch (error.status) {
                case 422:
                  this.notification.error(this.errorFormatterService.format(JSON.parse(await error.error.text()).ValidationErrors), `Could not create ${this.roleString}`, {
                    enableHtml: true,
                    timeOut: 10000
                  });
                  break;
                case 401:
                  this.notification.error(await error.error.text(), `Could not create ${this.roleString}`);
                  this.router.navigate(['/']);
                  break;
                default:
                  this.notification.error(await error.error.text(), `Could not create ${this.roleString}`);
                  break;
              }
            }
          });
        }
        break;
      case UserCreateEditMode.edit:
        if (this.userForm.valid) {
          this.update.medicines = this.selectedMedicationOptions;
          this.update.allergies = this.selectedAllergyOptions;
          this.update.email = this.userForm.get('email').value;
          this.update.firstname = this.userForm.get('firstname').value;
          this.update.lastname = this.userForm.get('lastname').value;
          this.update.svnr = this.userForm.get('svnr').value;
          let observableEdit;
          switch (this.role) {
            case Role.admin:
              observableEdit = this.service.updateAdmin(this.update, this.userId);
              break;
            case Role.doctor:
              observableEdit = this.service.updateDoctor(this.update, this.userId);
              break;
            case Role.secretary:
              observableEdit = this.service.updateSecretary(this.update, this.userId);
              break;
            case Role.patient:
              observableEdit = this.service.updatePatient(this.update, this.userId);
              break;
            default:
              console.error('Unknown Role', this.role);
              return;
          }
          observableEdit.subscribe({
            next: () => {
              this.notification.success(`Profile ${this.update.firstname} ${this.update.lastname} successfully updated.`);
              if (this.isOwnEdit) {
                // update the user information if the user updates his own profile
                this.authService.updateUserInformation();
              }
            },
            error: error => {
              this.notification.error('Could not update profile', 'Error updating profile');
              console.error('error while updating user', error);
            }
          });
        }
        break;
      case UserCreateEditMode.view:
        this.router.navigate([this.router.url + "/edit"]);
        break;
      default:
        console.error('Unknown mode', this.mode);
        return;
    }
  }
}
