import {Component, OnInit} from '@angular/core';
import {ChatListComponent} from "./chat-list/chat-list.component";
import {ChatMessagesComponent} from "./chat-messages/chat-messages.component";
import {AddChatComponent} from "./add-chat/add-chat.component";
import {MatToolbar} from "@angular/material/toolbar";
import {ChatDto} from "../../dtos/chat";
import {MessageService} from "../../services/message.service";
import {AsyncPipe, NgForOf} from "@angular/common";
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from "@angular/material/autocomplete";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {ReactiveFormsModule} from "@angular/forms";

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    ChatListComponent,
    ChatMessagesComponent,
    AddChatComponent,
    MatToolbar,
    AsyncPipe,
    MatAutocomplete,
    MatAutocompleteTrigger,
    MatFormField,
    MatInput,
    MatLabel,
    MatOption,
    NgForOf,
    ReactiveFormsModule
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit  {
  selectedChat: ChatDto;

  constructor(private messageService: MessageService) {
  }

  ngOnInit() {
  }

  onChatSelected(chat: ChatDto) {
    console.log(chat)
    this.selectedChat = chat;
  }
}
