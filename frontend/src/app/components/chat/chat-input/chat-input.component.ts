import {Component, EventEmitter, Output} from '@angular/core';
import {MatFormField} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {FormsModule} from "@angular/forms";
import {MatIconButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";

@Component({
  selector: 'app-chat-input',
  standalone: true,
  imports: [
    MatFormField,
    MatIcon,
    FormsModule,
    MatIconButton,
    MatInput
  ],
  templateUrl: './chat-input.component.html',
  styleUrl: './chat-input.component.scss'
})
export class ChatInputComponent {
  @Output() sendMessage = new EventEmitter<string>();
  message: string = '';

  send() {
    if (this.message && this.message.trim() && this.message.trim().length <= 1024) {
      this.sendMessage.emit(this.message.trim());
      this.message = '';
    }
  }
}
