import {Component, Inject, OnInit} from '@angular/core';
import {MatFormField, MatHint, MatLabel, MatSuffix} from "@angular/material/form-field";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from "@angular/material/datepicker";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {DatePipe} from "@angular/common";
import {StaysService} from "../../../services/stays.service";
import {StayDto} from "../../../dtos/stays";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../services/error-formatter.service";
import {getHours, getMinutes} from "date-fns";

@Component({
  selector: 'app-stays-edit',
  standalone: true,
  imports: [
    MatLabel,
    MatFormField,
    MatDialogContent,
    MatDialogTitle,
    MatDatepickerInput,
    FormsModule,
    MatDatepickerToggle,
    MatDatepicker,
    MatInput,
    MatDialogActions,
    MatButton,
    DatePipe,
    MatHint,
    MatSuffix,
    ReactiveFormsModule
  ],
  templateUrl: './stays-edit.component.html',
  styleUrls: ['./stays-edit.component.scss', '../../../../styles.scss']
})
export class StaysEditComponent implements OnInit {
  startDate: Date;
  endDate: Date;
  updateDate: FormGroup;

  constructor(public dialogRef: MatDialogRef<StaysEditComponent>,
              @Inject(MAT_DIALOG_DATA) public data: StayDto,
              private stayService: StaysService,
              private notification: ToastrService,
              private errorFormatterService: ErrorFormatterService) {
  }

  ngOnInit() {
    this.updateDate = new FormGroup({
      StartDate: new FormControl(),
      StartTime: new FormControl(),
      EndDate: new FormControl(),
      EndTime: new FormControl()
    });

    this.updateDate.get('StartDate').setValue(this.data.arrival);
    this.updateDate.get('EndDate').setValue(this.data.discharge);

    // Format the time as HH:mm
    const arrivalTimeString = `${getHours(this.data.arrival) < 10 ? '0' : ''}${getHours(this.data.arrival)}:${getMinutes(this.data.arrival) < 10 ? '0' : ''}${getMinutes(this.data.arrival)}`;
    const dischargeTimeString = `${getHours(this.data.discharge) < 10 ? '0' : ''}${getHours(this.data.discharge)}:${getMinutes(this.data.discharge) < 10 ? '0' : ''}${getMinutes(this.data.discharge)}`;

    // Set the time inputs with the content from the StayDto object
    this.updateDate.get('StartTime').setValue(arrivalTimeString);
    this.updateDate.get('EndTime').setValue(dischargeTimeString);
  }

  combineDateAndTime(date: string, time: string): Date {
    const combinedDate = new Date(date);
    const [hours, minutes] = time.split(':');
    combinedDate.setHours(parseInt(hours), parseInt(minutes));
    return combinedDate;
  }

  submit() {
    const StartDate = this.updateDate.get('StartDate').value;
    const StartTime = this.updateDate.get('StartTime').value;
    const EndDate = this.updateDate.get('EndDate').value;
    const EndTime = this.updateDate.get('EndTime').value;

    const combinedStartDate = this.combineDateAndTime(StartDate, StartTime)
    const combinedEndDate = this.combineDateAndTime(EndDate, EndTime)

    const updateStay: StayDto = {
      arrival: combinedStartDate,
      discharge: combinedEndDate,
      id: this.data.id,
      inpatientDepartment: this.data.inpatientDepartment
    }


    this.stayService.update(updateStay).subscribe({
      next: () => {
        this.notification.success('Stay edited successfully');
        this.dialogRef.close();
      },
      error: async error => {
        await this.errorFormatterService.printErrorToNotification(error, "Couldn't edit stay", this.notification);
      }
    });
  }

  close() {
    this.dialogRef.close();
  }
}
