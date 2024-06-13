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

    this.updateDate.get('StartDate').setValue(new Date(this.data.arrival));
    this.updateDate.get('EndDate').setValue(new Date(this.data.discharge));

    // Format the time as HH:mm
    const arrivalTime = this.data.arrival;
    const dischargeTime = this.data.discharge;
    const arrivalTimeString = `${getHours(arrivalTime)}:${getMinutes(arrivalTime) < 10 ? '0' : ''}${getMinutes(arrivalTime)}`;
    const dischargeTimeString = `${getHours(dischargeTime)}:${getMinutes(dischargeTime) < 10 ? '0' : ''}${getMinutes(dischargeTime)}`;

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
      station: this.data.station
    }


    this.stayService.update(updateStay).subscribe({
      next: () => {
        this.notification.success('Stay edited successfully');
        this.dialogRef.close();
      },
      error: error => {
        this.notification.error(error?.error?.ValidationErrors.join(', '), 'Could not edit stay', {
          enableHtml: true,
          timeOut: 10000
        });
      }
    });
  }

  close() {
    this.dialogRef.close();
  }
}
