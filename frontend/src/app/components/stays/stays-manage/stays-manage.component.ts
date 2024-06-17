import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from "@angular/material/dialog";
import {MatFormField} from "@angular/material/form-field";
import {MatOption, MatSelect} from "@angular/material/select";
import {MatButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {MatAutocomplete, MatAutocompleteTrigger} from "@angular/material/autocomplete";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {catchError, of, switchMap} from "rxjs";
import {InpatientDepartmentDto} from "../../../dtos/inpatient-department";
import {InpatientDepartmentService} from "../../../services/inpatient-department.service";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {StaysService} from "../../../services/stays.service";
import {MatCard, MatCardActions, MatCardContent, MatCardTitle} from "@angular/material/card";

@Component({
  selector: 'app-stays-manage',
  standalone: true,
  imports: [
    MatDialogContent,
    MatFormField,
    MatSelect,
    MatOption,
    MatDialogActions,
    MatDialogClose,
    MatButton,
    MatDialogTitle,
    MatInput,
    MatAutocompleteTrigger,
    ReactiveFormsModule,
    MatAutocomplete,
    NgForOf,
    NgIf,
    DatePipe,
    MatCard,
    MatCardTitle,
    MatCardContent,
    MatCardActions
  ],
  templateUrl: './stays-manage.component.html',
  styleUrl: './stays-manage.component.scss'
})


export class StaysManageComponent implements OnInit {
  filteredInpatientDepartments: InpatientDepartmentDto[];
  myControl = new FormControl();
  isCheckedIn: boolean = false;
  currentStay = null;
  @Input() firstname: string;
  @Input() lastname: string;
  @Input() patientId: number;
  @Output() stayChanged = new EventEmitter<any>();

  constructor(private inpatientDepartmentService: InpatientDepartmentService,
              private staysService: StaysService) {

    this.myControl.valueChanges.pipe(
      switchMap(value => {
        let searchTerm = value?.name || value;

        return this.inpatientDepartmentService.getInpatientDepartments(searchTerm, 0, 5).pipe(
          catchError(() => of({inpatientDepartments: [], totalCount: 0}))
        );
      })
    ).subscribe(inpatientDepartments => this.filteredInpatientDepartments = inpatientDepartments.inpatientDepartments);
  }

  isInputEmpty(): boolean {
    return !this.myControl.value;
  }

  checkPatientIn() {
    this.staysService.createNewStay({
      patientId: this.patientId,
      inpatientDepartment: this.myControl.value
    }).subscribe(stay => {
      this.isCheckedIn = true;
      this.currentStay = stay;
    });
  }

  displayFn(inpatientDepartment: InpatientDepartmentDto): string {
    return inpatientDepartment && inpatientDepartment.name ? inpatientDepartment.name : '';
  }

  dischargePatient() {
    this.staysService.endStay(this.currentStay.id).subscribe(stay => {
      this.isCheckedIn = false;
      this.stayChanged.emit();
    });
  }

  ngOnInit(): void {
    this.inpatientDepartmentService.getInpatientDepartments('', 0, 5).subscribe(inpatientDepartments => this.filteredInpatientDepartments = inpatientDepartments.inpatientDepartments);
    this.staysService.getCurrentStay(this.patientId).subscribe(stay => {
      this.isCheckedIn = stay != null
      this.currentStay = stay;
    });
  }


}
