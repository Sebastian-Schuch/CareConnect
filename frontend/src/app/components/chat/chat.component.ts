import {Component, OnInit} from '@angular/core';
import {ChatListComponent} from "./chat-list/chat-list.component";
import {ChatMessagesComponent} from "./chat-messages/chat-messages.component";
import {AddChatComponent} from "./add-chat/add-chat.component";
import {MatToolbar} from "@angular/material/toolbar";
import {ChatDto} from "../../dtos/chat";
import {MessageService} from "../../services/message.service";

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    ChatListComponent,
    ChatMessagesComponent,
    AddChatComponent,
    MatToolbar
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
