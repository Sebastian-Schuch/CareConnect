import {Component, OnInit} from '@angular/core';
import {UserDto, UserDtoCreate, UserDtoUpdate} from "../../../dtos/user";
import {UserService} from "../../../services/user.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormControl, FormGroup, NgModel, Validators} from "@angular/forms";
import {debounceTime, map, Observable, startWith, switchMap} from "rxjs";
import {Role} from "../../../dtos/Role";
import {ToastrService} from 'ngx-toastr';
import {MedicationDto, MedicationPageDto} from "../../../dtos/medication";
import {MedicationService} from "../../../services/medication.service";
import {AllergyDto, AllergyPageDto} from "../../../dtos/allergy";
import {AllergyService} from "../../../services/allergy.service";
import {ErrorFormatterService} from "../../../services/error-formatter.service";
import {AuthService} from "../../../services/auth.service";
import {ChangePasswordFormModalComponent} from "../change-password-form-modal/change-password-form-modal.component";
import {ResetPasswordDialogComponent} from "../reset-password-dialog/reset-password-dialog.component";
import {MatDialog} from "@angular/material/dialog";

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
  user: UserDto = null;

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
    if (this.user) {
      return this.authService.getUserEmail() === this.user.email;
    } else {
      return false;
    }
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.route.params.subscribe(params => {

        if (data.role != null && data.mode != null) {
          this.userId = params['id'];
          this.role = data.role;
          this.mode = data.mode;
          this.generateForm();
          this.resetAllSearchInputs();
          console.log(this.filteredAllergyOptions);
          if (this.mode == UserCreateEditMode.edit || this.mode == UserCreateEditMode.view) {

            if (isNaN(params['id']) === false) {
              this.loadUser(this.userId);
            } else {
              this.router.navigate(['/']);
            }
          }
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
        this.user = user;
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
      debounceTime(300),
      switchMap(value => this.loadFilteredMedication(value))
    );


    this.filteredAllergyOptions = this.userForm.get('allergy').valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      switchMap(value => this.loadFilteredAllergy(value))
    );
  }

  private loadFilteredMedication(value: string | null | undefined): Observable<MedicationDto[]> {
    const filterValue = this.getStringValue(value).toLowerCase();
    return this.medicationService.searchMedications(filterValue, 0, 10).pipe(
      map((page: MedicationPageDto) => page.medications)
    );
  }

  private loadFilteredAllergy(value: string | null | undefined): Observable<AllergyDto[]> {
    const filterValue = this.getStringValue(value).toLowerCase();
    return this.allergyService.searchAllergies(filterValue, 0, 10).pipe(
      map((page: AllergyPageDto) => page.allergies)
    );
  }

  private getStringValue(value: any): string {
    if (typeof value === 'string') {
      return value;
    } else if (value && value.firstname && value.lastname) {
      return `${value.firstname} ${value.lastname}`;
    } else {
      return '';
    }
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
    this.userForm.get('medication').reset();
    this.filteredMedicationOptions = this.userForm.get('medication').valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      switchMap(value => this.loadFilteredMedication(value))
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
    this.userForm.get('allergy').reset();
    this.filteredAllergyOptions = this.userForm.get('allergy').valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      switchMap(value => this.loadFilteredAllergy(value))
    );
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

  openPasswordModal() {
    this.dialog.open(ChangePasswordFormModalComponent, {
      width: '500px', data: {email: this.user.email, mode: 'edit'}, disableClose: false
    });
  }

  openResetModal() {
    this.dialog.open(ResetPasswordDialogComponent, {
      width: '500px', data: {user: this.user}, disableClose: false
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
              this.routeUserCorrectly(this.role);
            },
            error: async error => {
              await this.errorFormatterService.printErrorToNotification(error, `Error creating user`, this.notification);
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
            error: async error => {
              await this.errorFormatterService.printErrorToNotification(error, 'Error updating user', this.notification);
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

  private routeUserCorrectly(role: Role) {
    switch (role) {
      case Role.admin:
        this.router.navigate(['/home/admin/users/admins']);
        break;
      case Role.doctor:
        this.router.navigate(['/home/admin/users/doctors']);
        break;
      case Role.secretary:
        this.router.navigate(['/home/admin/users/secretaries']);
        break;
      case Role.patient:
        this.router.navigate(['/home/secretary/patients']);
        break;
      default:
        this.router.navigate(['/']);
    }
  }
}
