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
import {AllergyDto} from "../../../dtos/allergy";

@Component({
  selector: 'app-allergy-delete',
  standalone: true,
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle
  ],
  templateUrl: './allergy-delete.component.html',
  styleUrls: ['./allergy-delete.component.scss', '../../../../styles.scss']
})
export class AllergyDeleteComponent {
  constructor(
    public dialogRef: MatDialogRef<AllergyDeleteComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { allergy: AllergyDto },
  ) {
  }
}
