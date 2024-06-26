import {Component, Inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {OutpatientDepartmentDto} from "../../../dtos/outpatient-department";
import {OpeningHoursDayDto} from "../../../dtos/opening-hours";

@Component({
  selector: 'app-outpatient-department-capacities-opening-hours-modal',
  standalone: true,
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle
  ],
  templateUrl: './outpatient-department-capacities-opening-hours-modal.component.html',
  styleUrls: ['./outpatient-department-capacities-opening-hours-modal.component.scss', '../../../../styles.scss']
})
export class OutpatientDepartmentCapacitiesOpeningHoursModalComponent {

  constructor(
    public dialogRef: MatDialogRef<OutpatientDepartmentCapacitiesOpeningHoursModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { outpatient: OutpatientDepartmentDto },
  ) {
  }

  formatDate(date: string): string {
    let times = date.split(":");
    return times[0] + ":" + times[1];
  }

  showOpeningHours(day: OpeningHoursDayDto): string {
    if (day == null || day.isClosed) {
      return "Closed";
    } else {
      return this.formatDate(day.open.toString()) + " - " + this.formatDate(day.close.toString());
    }
  }
}
