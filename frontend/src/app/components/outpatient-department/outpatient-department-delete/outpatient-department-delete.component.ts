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

@Component({
  selector: 'app-outpatient-department-delete',
  standalone: true,
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle
  ],
  templateUrl: './outpatient-department-delete.component.html',
  styleUrls: ['./outpatient-department-delete.component.scss', '../../../../styles.scss']
})
export class OutpatientDepartmentDeleteComponent {
  constructor(
    public dialogRef: MatDialogRef<OutpatientDepartmentDeleteComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { name: string },
  ) {
  }
}
