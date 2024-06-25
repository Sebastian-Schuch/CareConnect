import {Component, Inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {ReactiveFormsModule} from "@angular/forms";

@Component({
  selector: 'app-api-key-view',
  standalone: true,
    imports: [
        MatButton,
        MatDialogActions,
        MatDialogContent,
        MatDialogTitle,
        MatFormField,
        MatInput,
        MatLabel,
        ReactiveFormsModule
    ],
  templateUrl: './api-key-view.component.html',
  styleUrl: './api-key-view.component.scss'
})
export class ApiKeyViewComponent {
  apikey: string = '';

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ApiKeyViewComponent>) {
    console.log(data)
    this.apikey = data.apiKey;
  }

  close(): void {
    this.dialogRef.close()
  }
}
