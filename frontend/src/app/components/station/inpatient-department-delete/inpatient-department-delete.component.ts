import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef, MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField} from "@angular/material/form-field";
import {DialogData} from "../../chat/add-chat/new-chat-dialog/new-chat-dialog.component";
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
  styleUrl: './inpatient-department-delete.component.scss'
})
export class InpatientDepartmentDeleteComponent {
  constructor(
    public dialogRef: MatDialogRef<InpatientDepartmentDeleteComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { name: string },
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }
}
