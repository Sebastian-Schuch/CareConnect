import {Component, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {forkJoin, map, Observable, startWith} from "rxjs";
import {MatPaginator} from "@angular/material/paginator";
import {MatTableDataSource} from "@angular/material/table";
import {TreatmentDtoCreate} from "../../dtos/treatment";
import {OutpatientDepartmentDto} from "../../dtos/outpatient-department";
import {UserService} from "../../services/user.service";
import {MedicationService} from "../../services/medication.service";
import {OutpatientDepartmentService} from "../../services/outpatient-department.service";
import {UserDetailDto} from "../../dtos/user";
import {MedicationDto} from "../../dtos/medication";
import {TreatmentService} from "../../services/treatment.service";
import {TreatmentMedicineDtoCreate} from "../../dtos/treatmentMedicine";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-treatment',
  templateUrl: './treatment.component.html',
  styleUrl: './treatment.component.scss'
})

/**
 * Component for creating a new treatment
 */

export class TreatmentComponent implements OnInit {

  private readonly baseUrl = '/api/v1';
  treatmentForm: FormGroup;
  medicationAdministeredForm: FormGroup;
  doctorOptions: UserDetailDto[] = [];
  patientOptions: UserDetailDto[] = [];
  medicineOptions: MedicationDto[] = [];
  treatmentMedicines: TreatmentMedicineDtoCreate[] = [];
  outpatientDepartments: OutpatientDepartmentDto[] = [];

  selectedDoctorOptions: UserDetailDto[] = [];
  filteredDoctorOptions: Observable<UserDetailDto[]>;
  filteredMedicineOptions: Observable<MedicationDto[]>;
  filteredPatientOptions: Observable<UserDetailDto[]>;
  filteredOutPatDep: Observable<OutpatientDepartmentDto[]>;

  displayedColumns: string[] = ['Medication', 'Amount', 'Unit', 'Date', 'Time', 'Delete'];
  dataSource: MatTableDataSource<any>;

  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private userService: UserService,
              private medicineService: MedicationService,
              private outpatientDepartmentService: OutpatientDepartmentService,
              private treatmentService: TreatmentService,
              private notification: ToastrService
  ) {

    this.dataSource = new MatTableDataSource<any>();
  }

  ngOnInit(): void {
    this.generateForm();
    forkJoin({
      doctors: this.loadDoctors(),
      medicines: this.loadMedicines(),
      departments: this.loadOutpatientDepartments(),
      patients: this.loadPatients()
    }).subscribe({
      next: ({doctors, medicines, departments, patients}) => {
        this.doctorOptions = doctors;
        this.medicineOptions = medicines;
        this.outpatientDepartments = departments;
        this.patientOptions = patients;
        this.resetAllSearchInputs();
      },
      error: error => {
        console.error('error while getting data from database', error);
      }
    });
  }

  private resetAllSearchInputs() {
    this.filteredDoctorOptions = this.treatmentForm.get('doctor').valueChanges.pipe(
      startWith(''),
      map(value => this.filterDoctors(value))
    );

    this.filteredPatientOptions = this.treatmentForm.get('patient').valueChanges.pipe(
      startWith(''),
      map(value => this.filterPatients(value))
    );

    this.filteredOutPatDep = this.treatmentForm.get('outpatientDepartment').valueChanges.pipe(
      startWith(''),
      map(value => this.filterOutPatDep(value))
    );

    this.filteredMedicineOptions = this.medicationAdministeredForm.get('medicine').valueChanges.pipe(
      startWith(''),
      map(value => this.filterMedicine(value))
    );
  }

  /**
   * The treatmentDtoCreate object to create a new treatment (default null values)
   */
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

  /**
   * The treatmentMedicineDtoCreate object to create a new treatment Medicine (default null values)
   */
  treatmentMedicineDtoCreate: TreatmentMedicineDtoCreate = {
    medication: null,
    amount: null,
    unitOfMeasurement: null,
    medicineAdministrationDate: null,
  };


  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  /**
   * Submits the form and creates a new treatment
   */
  submitForm(): void {
    this.treatmentDtoCreate.treatmentTitle = this.treatmentForm.get('treatmentTitle').value;
    this.treatmentDtoCreate.treatmentStart = this.combineDateAndTime(this.treatmentForm.get('treatmentStartDate').value, this.treatmentForm.get('treatmentStartTime').value);
    this.treatmentDtoCreate.treatmentEnd = this.combineDateAndTime(this.treatmentForm.get('treatmentEndDate').value, this.treatmentForm.get('treatmentEndTime').value);
    this.treatmentDtoCreate.patient = this.treatmentForm.get('patient').value;
    this.treatmentDtoCreate.outpatientDepartment = this.treatmentForm.get('outpatientDepartment').value;
    this.treatmentDtoCreate.treatmentText = this.treatmentForm.get('treatmentText').value;
    this.treatmentDtoCreate.doctors = this.selectedDoctorOptions;
    this.treatmentDtoCreate.medicines = this.treatmentMedicines;

    if (this.treatmentForm.valid) {
      this.treatmentService.createTreatment(this.treatmentDtoCreate).subscribe({
        next: data => {
          this.notification.success('Successfully created treatment ' + data.treatmentTitle);
        },
        error: error => {
          this.notification.error('Failed creating treatment!')
          console.error('There was an error!', error);
        }
      })
    }
  }

  /**
   * Submits the form and adds a new medication to the treatment
   * maps the form values to the treatmentMedicineDtoCreate object
   */
  submitMedicationAdministered() {
    if (this.medicationAdministeredForm.valid) {
      const medicationData = this.medicationAdministeredForm.value;
      this.treatmentMedicineDtoCreate = null;
      this.treatmentMedicineDtoCreate = {
        medication: medicationData.medicine,
        amount: medicationData.amount,
        unitOfMeasurement: medicationData.unit,
        medicineAdministrationDate: this.combineDateAndTime(medicationData.medicineDatePicker, medicationData.medicineTimePicker)
      };

      medicationData.medicineDatePicker = new Date(medicationData.medicineDatePicker).toLocaleDateString();
      console.log(medicationData);
      this.treatmentMedicines.push(this.treatmentMedicineDtoCreate);
      this.dataSource.data.push(medicationData);
      this.dataSource._updateChangeSubscription();
      this.medicationAdministeredForm.reset();
      this.medicationAdministeredForm.markAsPristine();
      this.medicationAdministeredForm.markAsUntouched();


      this.filteredMedicineOptions = this.medicationAdministeredForm.get('medicine').valueChanges.pipe(
        startWith(''),
        map(value => this.filterMedicine(value))
      );

    }
  }

  /**
   * Generates the forms for creating a new treatment and adding a new medication
   */
  private generateForm() {
    this.treatmentForm = new FormGroup({
      treatmentTitle: new FormControl('', Validators.required),
      doctor: new FormControl(''),
      patient: new FormControl('', Validators.required),
      outpatientDepartment: new FormControl('', Validators.required),
      treatmentStartDate: new FormControl('', Validators.required),
      treatmentStartTime: new FormControl('', Validators.required),
      treatmentEndDate: new FormControl('', Validators.required),
      treatmentEndTime: new FormControl('', Validators.required),
      treatmentText: new FormControl(''),
      treatmentMedicine: new FormControl(''),
      deleteButton: new FormControl('')
    });

    this.medicationAdministeredForm = new FormGroup({
      medicine: new FormControl('', Validators.required),
      amount: new FormControl('', Validators.required),
      unit: new FormControl('', Validators.required),
      medicineTimePicker: new FormControl('', Validators.required),
      medicineDatePicker: new FormControl('', Validators.required)
    });
  }

  /**
   * helper method to correctly display the patient name in frontend
   * @param patient the patient object
   */
  displayPatient(patient: any): string {
    return patient ? `${patient.firstname} ${patient.lastname}` : '';
  }

  /**
   * helper method to correctly display the outpatient department name in frontend
   * @param outpd the outpatient department object
   */
  displayOutPD(outpd: any): string {
    return outpd ? `${outpd.name}` : '';
  }

  /**
   * helper method to correctly display the medication name in frontend
   * @param medicine the medication object
   */
  displayMedication(medicine: any): string {
    return medicine ? `${medicine.name}` : '';
  }

  /**
   * helper method to correctly add the values of date and time
   * @param date the date
   * @param time the time
   * @returns the combined date and time in one object
   */
  combineDateAndTime(date: string, time: string): Date {
    const combinedDate = new Date(date);
    const [hours, minutes] = time.split(':');
    combinedDate.setHours(parseInt(hours), parseInt(minutes));
    return combinedDate;
  }

  /**
   * Loads all doctors from the backend
   * @returns an observable with all doctors
   */
  private loadDoctors(): Observable<any> {
    return this.userService.getAllDoctors();
  }

  /**
   * Loads all medicines from the backend
   * @returns an observable with all medicines
   */
  private loadMedicines(): Observable<any> {
    return this.medicineService.getMedicationsAll();
  }

  /**
   * Loads all patients from the backend
   * @returns an observable with all patients
   */
  private loadPatients(): Observable<any> {
    return this.userService.getAllPatients();
  }

  /**
   * Loads all outpatient departments from the backend
   * @returns an observable with all outpatient departments
   */
  private loadOutpatientDepartments(): Observable<any> {
    return this.outpatientDepartmentService.getOutpatientDepartment();
  }


  /**
   * Filters the medicines based on the input value
   * @param value the input value to filter for
   * @returns the filtered medicines
   */
  private filterMedicine(value: string): MedicationDto[] {
    if (value != null) {
      const filterValue = value.toString().toLowerCase();
      return this.medicineOptions.filter(option =>
        option.name.toString().toLowerCase().includes(filterValue)
      );
    }
  }

  /**
   * Filters the patients based on the input value
   * @param value the input value to filter for
   * @returns the filtered patients
   */
  private filterPatients(value: string): UserDetailDto[] {
    const filterValue = value.toString().toLowerCase();
    return this.patientOptions.filter(option =>
      option.firstname.toString().toLowerCase().includes(filterValue) ||
      option.lastname.toString().toLowerCase().includes(filterValue) ||
      option.svnr.toString().includes(filterValue)
    );
  }

  /**
   * Filters the doctors based on the input value
   * @param value the input value to filter for
   * @returns the filtered doctors
   */
  private filterDoctors(value: string): UserDetailDto[] {
    const filterValue = value.toString().toLowerCase();
    return this.doctorOptions.filter(option =>
      option.firstname.toLowerCase().includes(filterValue) ||
      option.lastname.toLowerCase().includes(filterValue)
    );
  }

  /**
   * Filters the outpatient departments based on the input value
   * @param value the input value to filter for
   * @returns the filtered outpatient departments
   */
  private filterOutPatDep(value: string): OutpatientDepartmentDto[] {
    const filterValue = value.toString().toLowerCase();
    return this.outpatientDepartments.filter(option =>
      option.name.toLowerCase().includes(filterValue) ||
      option.description.toLowerCase().includes(filterValue)
    );
  }


  /**
   * helper method to add a selected doctor to the selection
   * resets the input field after adding the doctor
   * @param doctor the doctor to add
   */
  addDoctorToSelection(doctor: UserDetailDto): void {
    if (!this.selectedDoctorOptions.some(d => d.id === doctor.id)) {
      this.selectedDoctorOptions.push(doctor);
    }
    this.treatmentForm.get('doctor').setValue('');
    this.filteredDoctorOptions = this.treatmentForm.get('doctor').valueChanges.pipe(
      startWith(''),
      map(value => this.filterDoctors(value))
    );
  }

  /**
   * helper method to remove a doctor from the selection
   * @param doctor the doctor to remove
   */
  removeDoctorFromSelection(doctor: UserDetailDto): void {
    const index = this.selectedDoctorOptions.findIndex(d => d.id === doctor.id);
    if (index >= 0) {
      this.selectedDoctorOptions.splice(index, 1);
    }
  }

  dateToday: number = Date.now();

  /**
   * helper method to delete a row from the table and the createDto
   * @param element the element to delete
   */
  deleteRow(element: any): void {
    const index = this.dataSource.data.indexOf(element);
    if (index >= 0) {
      this.dataSource.data.splice(index, 1);
      this.dataSource._updateChangeSubscription();
      this.treatmentMedicines.splice(index, 1);
    }
  }

}
