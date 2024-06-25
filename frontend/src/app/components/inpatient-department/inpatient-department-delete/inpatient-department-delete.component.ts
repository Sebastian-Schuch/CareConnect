import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField} from "@angular/material/form-field";
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'app-inpatient-department-delete',
  standalone: true,
  imports: [
    MatDialogContent,
    MatFormField,
    MatDialogActions,
    MatDialogClose,
    MatButton,
    MatDialogTitle
  ],
  templateUrl: './inpatient-department-delete.component.html',
  styleUrls: ['./inpatient-department-delete.component.scss', '../../../../styles.scss']
})
export class InpatientDepartmentDeleteComponent {
  constructor(
    public dialogRef: MatDialogRef<InpatientDepartmentDeleteComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { name: string },
  ) {
  }
}
