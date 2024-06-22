import {Component, EventEmitter, Inject, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {debounceTime, Observable, switchMap} from 'rxjs';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {map, startWith} from 'rxjs/operators';
import {MedicationDto} from '../../../dtos/medication';
import {MedicationService} from '../../../services/medication.service';

@Component({
  selector: 'app-medication-form-modal',
  templateUrl: './medication-form-modal.component.html',
  styleUrls: ['./medication-form-modal.component.scss', '../treatment.component.scss']
})
export class MedicationFormModalComponent implements OnInit {

  @Output() submitMedication = new EventEmitter<any>();

  medicationAdministeredForm: FormGroup;
  medicineOptions: MedicationDto[] = [];
  filteredMedicationOptions: Observable<MedicationDto[]>;
  dateToday: Date = new Date();

  constructor(
    private fb: FormBuilder,
    private medicineService: MedicationService,
    public dialogRef: MatDialogRef<MedicationFormModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.medicationAdministeredForm = this.fb.group({
      medication: ['', Validators.required],
      amount: ['', Validators.required],
      unitOfMeasurement: ['', Validators.required],
      medicineDatePicker: ['', [Validators.required, this.dateValidator.bind(this)]],
      medicineTimePicker: ['', Validators.required]
    });
  }

  ngOnInit(): void {

    this.filteredMedicationOptions = this.medicationAdministeredForm.get('medication').valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      switchMap(value => this._filterMedicine(value))
    );
  }

  // Helper methods to access form controls
  get medicationControl() {
    return this.medicationAdministeredForm.get('medication');
  }

  get amountControl() {
    return this.medicationAdministeredForm.get('amount');
  }

  get unitOfMeasurementControl() {
    return this.medicationAdministeredForm.get('unitOfMeasurement');
  }

  get dateControl() {
    return this.medicationAdministeredForm.get('medicineDatePicker');
  }

  get timeControl() {
    return this.medicationAdministeredForm.get('medicineTimePicker');
  }

  displayMedication(medication: any): string {
    return medication ? `${medication.name}` : '';
  }

  /**
   * Submit the medication form if the form is valid and close the dialog
   */
  onSubmit() {
    if (this.medicationAdministeredForm.valid) {
      this.submitMedication.emit(this.medicationAdministeredForm.value);
      this.dialogRef.close();
    }
  }

  /**
   * Validate the date selected by the user
   * @param control  the date control
   * @return: null if the date is valid, otherwise set the invalidDate flag to true
   */
  dateValidator(control) {
    const selectedDate = new Date(control.value);
    if (selectedDate > this.dateToday) {
      return {invalidDate: true};
    }
    return null;
  }

  /**
   * Filter the medicines based on the input value
   * @param value the value to filter the medicines
   * @return: the filtered medicines
   */
  private _filterMedicine(value: string): Observable<MedicationDto[]> {
    return this.medicineService.searchMedicationsByName(value);
  }
}
