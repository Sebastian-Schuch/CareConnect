import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {forkJoin, Observable, startWith} from "rxjs";
import {map} from "rxjs/operators";
import {MatPaginator} from "@angular/material/paginator";
import {MatTableDataSource} from "@angular/material/table";
import {TreatmentDto, TreatmentDtoCreate} from "../../dtos/treatment";
import {TreatmentMedicineDtoCreate, TreatmentMedicineSelection} from "../../dtos/treatmentMedicine";
import {OutpatientDepartmentDto} from "../../dtos/outpatient-department";
import {UserService} from "../../services/user.service";
import {OutpatientDepartmentService} from "../../services/outpatient-department.service";
import {UserDto} from "../../dtos/user";
import {TreatmentService} from "../../services/treatment.service";
import {ToastrService} from "ngx-toastr";
import {MatDialog} from "@angular/material/dialog";
import {MedicationFormModalComponent} from "./medication-form-modal/medication-form-modal.component";
import {ActivatedRoute} from "@angular/router";
import {TreatmentMedicineService} from "../../services/treatment-medicine.service";
import {ErrorFormatterService} from "../../services/error-formatter.service";
import {MatAutocomplete} from "@angular/material/autocomplete";
import {getDate, getMonth, getYear} from "date-fns";


export enum TreatmentCreateEditMode {
  log,
  edit,
  detail
}

@Component({
  selector: 'app-treatment',
  templateUrl: './treatment.component.html',
  styleUrls: ['./treatment.component.scss']
})

export class TreatmentComponent implements OnInit, AfterViewInit {

  treatmentForm: FormGroup;
  mode: TreatmentCreateEditMode = TreatmentCreateEditMode.log;
  existingTreatment: TreatmentDto;

  // Options for the select fields
  treatmentMedicationSelection: TreatmentMedicineSelection[] = [];
  doctorOptions: UserDto[] = [];
  patientOptions: UserDto[] = [];
  outpatientDepartmentOptions: OutpatientDepartmentDto[] = [];

  selectedDoctorOptions: UserDto[] = [];
  private treatmentMedicineToDeleteIds: number[] = [];

  // temp for filtered options for the select fields
  filteredDoctorOptions: Observable<UserDto[]>;
  filteredPatientOptions: Observable<UserDto[]>;
  filteredOutPatDep: Observable<OutpatientDepartmentDto[]>;

  displayedColumns: string[] = ['Medication', 'Amount', 'Unit', 'Date', 'Time', 'Delete'];
  dataSource: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('doctorAuto', {static: false}) doctorAuto: MatAutocomplete;
  dateToday: number = Date.now();
  treatmentDtoCreate: TreatmentDtoCreate = {
    treatmentTitle: null,
    treatmentStart: null,
    treatmentEnd: null,
    patient: null,
    outpatientDepartment: null,
    treatmentText: null,
    doctors: null,
    medicines: null
  };

  protected readonly TreatmentCreateEditMode = TreatmentCreateEditMode;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private outpatientDepartmentService: OutpatientDepartmentService,
    private treatmentService: TreatmentService,
    private treatmentMedicineService: TreatmentMedicineService,
    private notification: ToastrService,
    private dialog: MatDialog,
    private route: ActivatedRoute,
    private errorFormater: ErrorFormatterService
  ) {
    this.dataSource = new MatTableDataSource<any>();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.route.data.subscribe(data => {
        this.mode = data.mode;
        this.generateForm();
        this.loadAllOptionsForSelectFields();
        if (this.mode === TreatmentCreateEditMode.edit) {
          const treatmentId = params['id'];
          if (treatmentId) {
            this.loadExistingTreatment(treatmentId);
          }
        }
      });
    });
  }

  public getDayString(date): string {
    let outDate = new Date(date);
    return getDate(outDate) + "." + (getMonth(outDate) + 1) + "." + getYear(outDate);
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  /**
   * Open the medication form modal to add another medication to the treatment and subscribe to the submit event
   */
  openMedicationModal() {
    const dialogRef = this.dialog.open(MedicationFormModalComponent, {
      width: '500px'
    });

    dialogRef.componentInstance.submitMedication.subscribe((medicationData) => {
      this.handleMedicationSubmission(medicationData);
    });
  }

  deleteMedicationFromPersistence(trMedIdToDelete: number[]): void {
    for (let trMedId of trMedIdToDelete) {
      this.treatmentMedicineService.deleteTreatmentMedicine(trMedId).subscribe(
        () => {
          this.notification.success('Successfully deleted medication from treatment');
        },
        (error) => {
          console.error('Error deleting medication from treatment:', error);
        }
      );
    }
  }

  /**
   * Submit the form and create a new treatment in the database if the form is valid
   */
  submitForm(): void {
    if (this.treatmentForm.valid && this.validateDoctorSelection()) {
      this.mapTreatmentFormValuesForCreate();

      if (this.mode === TreatmentCreateEditMode.edit) {
        this.treatmentService.updateTreatment(this.existingTreatment.id, this.treatmentDtoCreate).subscribe({
          next: data => {
            this.deleteMedicationFromPersistence(this.treatmentMedicineToDeleteIds);
            this.notification.success('Successfully updated treatment ' + data.treatmentTitle);
            this.resetForm();
          },
          error: error => {
            this.errorFormater.printErrorToNotification(error, "Error updating treatment", this.notification);
            console.error('There was an error!', error);
          }
        });
        return;
      }

      this.treatmentService.createTreatment(this.treatmentDtoCreate).subscribe({
        next: data => {
          this.notification.success('Successfully created treatment ' + data.treatmentTitle);
          this.resetForm();
        },
        error: error => {
          this.errorFormater.printErrorToNotification(error, "Error creating treatment!", this.notification);
          console.error('There was an error!', error);
        }
      });
    }
  }

  /**
   * Display the patient in the select field with correct format
   * @param patient - the patient as an object
   * @return string - the formatted patient
   */
  displayPatient(patient: any): string {
    return patient ? `${patient.firstname} ${patient.lastname}` : '';
  }

  /**
   * Display the doctor in the select field with correct format
   * @param outpatientDepartment - the doctor as an object
   * @return string - the formatted doctor
   */
  displayOutPD(outpatientDepartment: any): string {
    return outpatientDepartment ? `${outpatientDepartment.name}` : '';
  }

  /**
   * Add a doctor to the selected doctors list and reset the search input
   * @param doctor - the doctor to add to the list
   */
  addDoctorToSelection(doctor: UserDto): void {
    if (!this.selectedDoctorOptions.some(d => d.id === doctor.id)) {
      this.selectedDoctorOptions.push(doctor);
    }
    this.resetAutocomplete();
  }

  /**
   * Remove a doctor from the selected doctors list
   * @param doctor - the doctor to remove from the list
   */
  removeDoctorFromSelection(doctor: UserDto): void {
    const index = this.selectedDoctorOptions.findIndex(d => d.id === doctor.id);
    if (index >= 0) {
      this.selectedDoctorOptions.splice(index, 1);
    }
  }

  /**
   * Reset the autocomplete control to its initial state
   */
  private resetAutocomplete(): void {
    this.treatmentForm.get('doctor').setValue('');
    this.treatmentForm.get('doctor').reset();

    this.filteredDoctorOptions = this.treatmentForm.get('doctor').valueChanges.pipe(
      startWith(''),
      map(value => this._filterDoctors(value))
    );
  }

  /**
   * Remove a medication from the treatment
   * deletes the medication from the table and the list of medications
   * @param element - the medication to remove
   */
  deleteMedicationFromTreatment(element: any): void {
    const index = this.dataSource.data.indexOf(element);
    if (index >= 0) {
      this.dataSource.data.splice(index, 1);
      this.dataSource._updateChangeSubscription();
      const treatmentMedicationToDelete = this.treatmentMedicationSelection.at(index).id;
      this.treatmentMedicineToDeleteIds.push(treatmentMedicationToDelete);
      this.treatmentMedicationSelection.splice(index, 1);
    }
  }

  /**
   * Validate if at least one doctor is selected for the treatment
   * @return boolean - true if at least one doctor is selected, false otherwise
   */
  private validateDoctorSelection(): boolean {
    if (this.selectedDoctorOptions.length !== 0) {
      return true;
    }
    this.notification.error('You have to add at least one doctor to the treatment!');
    return false;
  }

  /**
   * Reset the form and all input fields
   */
  private resetForm(): void {
    this.treatmentForm.reset();
    this.treatmentMedicationSelection = [];
    this.selectedDoctorOptions = [];
    this.dataSource.data = [];
    this.resetAllSearchInputs();
  }

  /**
   * helper function to combine date and time to a single date object
   * @param date - the date as a string
   * @param time - the time as a string
   * @return Date - the combined date and time
   */
  private combineDateAndTime(date: string, time: string): Date {
    const combinedDate = new Date(date);
    const [hours, minutes] = time.split(':');
    combinedDate.setHours(parseInt(hours), parseInt(minutes));
    return combinedDate;
  }

  /**
   * Load the existing treatment from the database
   * @param treatmentId - the id of the treatment to load
   */
  private loadExistingTreatment(treatmentId: number) {
    this.treatmentService.getTreatmentById(treatmentId).subscribe({
      next: (treatment: TreatmentDto) => {
        this.existingTreatment = treatment;
        this.populateFormWithExistingTreatment(treatment);
      },
      error: error => {
        console.error('Error loading treatment', error);
        this.notification.error('Failed to load existing treatment data');
      }
    });
  }

  /**
   * Populate the form with the existing treatment data
   * @param treatment - the treatment to populate the form with
   */
  private populateFormWithExistingTreatment(treatment: TreatmentDto) {
    this.treatmentForm.patchValue({
      treatmentTitle: treatment.treatmentTitle,
      patient: treatment.patient,
      outpatientDepartment: treatment.outpatientDepartment,
      treatmentStartDate: new Date(treatment.treatmentStart),
      treatmentStartTime: this.formatTime(new Date(treatment.treatmentStart)),
      treatmentEndDate: new Date(treatment.treatmentEnd),
      treatmentEndTime: this.formatTime(new Date(treatment.treatmentEnd)),
      treatmentText: treatment.treatmentText
    });

    this.selectedDoctorOptions = treatment.doctors;
    this.treatmentMedicationSelection = treatment.medicines;

    // Update the data source for the medications table
    this.dataSource.data = this.treatmentMedicationSelection.map(medication => ({
      ...medication,
      medicineDatePicker: new Date(medication.medicineAdministrationDate).toLocaleDateString(),
      medicineTimePicker: this.formatTime(new Date(medication.medicineAdministrationDate))
    }));
  }

  /**
   * helper function to format the time to a string
   */
  private formatTime(date: Date): string {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  /**
   * Load all options for the select fields (doctors, departments, patients) and clear the inputs
   */
  private loadAllOptionsForSelectFields() {
    forkJoin({
      doctors: this.loadDoctors(),
      departments: this.loadOutpatientDepartments(),
      patients: this.loadPatients()
    }).subscribe({
      next: ({doctors, departments, patients}) => {
        this.doctorOptions = doctors;
        this.outpatientDepartmentOptions = departments;
        this.patientOptions = patients;
        this.resetAllSearchInputs();
      },
      error: error => {
        console.error('Error while getting data from database', error);
        this.notification.error('Failed to load initial data');
      }
    });
  }

  /**
   * Handle the medication submission from the modal
   * @param medicationData - the data from the medication form modal
   * adds the medication to the list of medications and the table
   */


  private handleMedicationSubmission(medicationData) {
    console.log(medicationData.medication);
    console.log(medicationData.medication.unitOfMeasurement);
    const treatmentMedicineDtoCreate: TreatmentMedicineDtoCreate = {
      medication: medicationData.medication,
      amount: medicationData.amount,
      unitOfMeasurement: medicationData.medication.unitOfMeasurement,
      medicineAdministrationDate: this.combineDateAndTime(medicationData.medicineDatePicker, medicationData.medicineTimePicker)
    };
    this.treatmentMedicineService.createTreatmentMedicine(treatmentMedicineDtoCreate).subscribe(
      {
        next: (newTreatmentMedicineSelection: TreatmentMedicineSelection) => {
          medicationData.medicineDatePicker = this.getDayString(medicationData.medicineDatePicker);
          this.treatmentMedicationSelection.push(newTreatmentMedicineSelection);
          this.dataSource.data.push(medicationData);
          this.dataSource._updateChangeSubscription();
        },
        error: (error) => {
          this.errorFormater.printErrorToNotification(error, "Error creating treatment medicine", this.notification);
        }
      }
    )
  }

  /**
   * Reset all search inputs for the select fields
   */
  private resetAllSearchInputs() {
    this.filteredDoctorOptions = this.treatmentForm.get('doctor').valueChanges.pipe(
      startWith(''),
      map(value => this._filterDoctors(value))
    );

    this.filteredPatientOptions = this.treatmentForm.get('patient').valueChanges.pipe(
      startWith(''),
      map(value => this._filterPatients(value))
    );

    this.filteredOutPatDep = this.treatmentForm.get('outpatientDepartment').valueChanges.pipe(
      startWith(''),
      map(value => this._filterOutPatDep(value))
    );
  }

  /**
   * Generate the form for treatment creation with the necessary fields
   */
  private generateForm() {
    this.treatmentForm = this.fb.group({
      treatmentTitle: ['', Validators.required],
      doctor: [''],
      patient: ['', Validators.required],
      outpatientDepartment: ['', Validators.required],
      treatmentStartDate: ['', Validators.required],
      treatmentStartTime: ['', Validators.required],
      treatmentEndDate: ['', Validators.required],
      treatmentEndTime: ['', Validators.required],
      treatmentText: [''],
      treatmentMedicine: [''],
      deleteButton: ['']
    });
  }

  /**
   * Map the form values to the treatment dto
   */
  private mapTreatmentFormValuesForCreate(): void {
    this.treatmentDtoCreate = {
      treatmentTitle: this.treatmentForm.get('treatmentTitle').value,
      treatmentStart: this.combineDateAndTime(this.treatmentForm.get('treatmentStartDate').value, this.treatmentForm.get('treatmentStartTime').value),
      treatmentEnd: this.combineDateAndTime(this.treatmentForm.get('treatmentEndDate').value, this.treatmentForm.get('treatmentEndTime').value),
      patient: this.treatmentForm.get('patient').value,
      outpatientDepartment: this.treatmentForm.get('outpatientDepartment').value,
      treatmentText: this.treatmentForm.get('treatmentText').value,
      doctors: this.selectedDoctorOptions,
      medicines: this.treatmentMedicationSelection
    };
  }

  /**
   * Load all doctors from the database from the service and return them as an observable
   * @return Observable<UserDetailDto[]> - the list of all doctors
   */
  private loadDoctors(): Observable<UserDto[]> {
    return this.userService.getAllDoctors();
  }

  /**
   * Load all patients from the database from the service and return them as an observable
   * @return Observable<UserDetailDto[]> - the list of all patients
   */
  private loadPatients(): Observable<UserDto[]> {
    return this.userService.getAllPatients();
  }

  /**
   * Load all outpatient departments from the database from the service and return them as an observable
   * @return Observable<OutpatientDepartmentDto[]> - the list of all outpatient departments
   */
  private loadOutpatientDepartments(): Observable<OutpatientDepartmentDto[]> {
    return this.outpatientDepartmentService.getOutpatientDepartment();
  }


  /**
   * Filter the patients for the search input
   * @param value - the search input
   * @return UserDetailDto[] - the filtered list of patients
   */
  private _filterPatients(value: string): UserDto[] {
    const filterValue = (value || '').toString().toLowerCase();
    return this.patientOptions.filter(option =>
      option.firstname.toString().toLowerCase().includes(filterValue) ||
      option.lastname.toString().toLowerCase().includes(filterValue) ||
      option.svnr.toString().includes(filterValue)
    );
  }

  /**
   * Filter the doctors for the search input
   * @param value - the search input
   * @return UserDetailDto[] - the filtered list of doctors
   */
  private _filterDoctors(value: string): UserDto[] {
    const filterValue = (value || '').toString().toLowerCase();
    return this.doctorOptions.filter(option =>
      option.firstname.toLowerCase().includes(filterValue) ||
      option.lastname.toLowerCase().includes(filterValue)
    );
  }

  /**
   * Filter the outpatient departments for the search input
   * @param value - the search input
   * @return OutpatientDepartmentDto[] - the filtered list of outpatient departments
   */
  private _filterOutPatDep(value: string): OutpatientDepartmentDto[] {
    const filterValue = (value || '').toString().toLowerCase();
    return this.outpatientDepartmentOptions.filter(option =>
      option.name.toLowerCase().includes(filterValue) ||
      option.description.toLowerCase().includes(filterValue)
    );
  }
}
