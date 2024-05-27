import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef, MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatOption} from "@angular/material/autocomplete";
import {MatSelect} from "@angular/material/select";
import {FormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {ChatDto} from "../../../../dtos/chat";

export interface DialogData {
  available: ChatDto[];
}

@Component({
  selector: 'app-new-chat-dialog',
  standalone: true,
  imports: [
    MatDialogContent,
    MatFormField,
    MatDialogActions,
    MatDialogClose,
    MatOption,
    MatSelect,
    FormsModule,
    MatDialogTitle,
    MatButton,
    MatLabel
  ],
  templateUrl: './new-chat-dialog.component.html',
  styleUrl: './new-chat-dialog.component.scss'
})
export class NewChatDialogComponent {
  selectedNewChat: string;
  constructor(
    public dialogRef: MatDialogRef<NewChatDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

}
