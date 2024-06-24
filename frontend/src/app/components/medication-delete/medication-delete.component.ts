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
import {MedicationDto} from "../../dtos/medication";

@Component({
  selector: 'app-medication-delete',
  standalone: true,
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle
  ],
  templateUrl: './medication-delete.component.html',
  styleUrls: ['./medication-delete.component.scss', '../../../styles.scss']
})
export class MedicationDeleteComponent {
  constructor(
    public dialogRef: MatDialogRef<MedicationDeleteComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { medication: MedicationDto },
  ) {
  }
}
