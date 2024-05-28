import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {MatList, MatListItem, MatNavList} from "@angular/material/list";
import {MatLine} from "@angular/material/core";
import {AddChatComponent} from "../add-chat/add-chat.component";
import {ChatDto} from "../../../dtos/chat";
import {MessageService} from "../../../services/message.service";
import {MessageDto} from "../../../dtos/message";
import {AuthService} from "../../../services/auth.service";
import {Role} from "../../../dtos/Role";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-chat-list',
  standalone: true,
  imports: [
    NgForOf,
    MatList,
    MatListItem,
    MatNavList,
    MatLine,
    AddChatComponent,
    NgIf,
    MatIcon,
    NgClass
  ],
  templateUrl: './chat-list.component.html',
  styleUrl: './chat-list.component.scss'
})
export class ChatListComponent implements OnInit{
  @Output() chatSwitched = new EventEmitter<ChatDto>();
  chats: ChatDto[] = [];
  ownEmail: string;
  private selectedChat: number;
  constructor(private messageService: MessageService,
              private authService: AuthService) {
  }

  ngOnInit() {
    this.ownEmail = this.authService.getUserEmail();
    this.messageService.getActiveChats().subscribe(chats => {
      this.chats = chats;
      this.messageService.initWebSocket().then(() => {
        for(let chat of this.chats){
          this.messageService.listenToMessages(chat.id, this.handleNewChats.bind(this));
        }
      });
    });
  }

  addChat(chat: ChatDto) {
    this.chats.unshift(chat);
    this.selectChat(chat);
    this.chatSwitched.emit(chat);
    this.messageService.listenToMessages(chat.id, this.handleNewChats.bind(this));
  }

  handleNewChats(message: MessageDto){
    const chat = this.chats.find(chat => chat.id === message.treatmentId);
    if(chat){
      chat.messages.push(message);
      if(this.selectedChat === chat.id){
        this.chatSwitched.emit(chat);
      }
    }
  }

  selectChat(chat: ChatDto) {
    this.messageService.getChatMessages(chat.id).subscribe(messages => {
      const currentChatPos = this.chats.findIndex(chatArr => chatArr.id === chat.id);
      this.chats[currentChatPos] = messages;
      this.selectedChat = chat.id;
      this.chatSwitched.emit(messages);
    });
  }

  isPatient(): boolean {
    return this.authService.getUserRole() === Role.patient;
  }

  lastMessageRead(chat: ChatDto): boolean {
    return chat.messages[chat.messages.length-1].read;
  }

  previewMesssage(chat: ChatDto): string {
    const sender = chat.messages[chat.messages.length-1].senderEmail === this.ownEmail ? "Me" : chat.messages[chat.messages.length-1].displayname;
    const content = chat.messages[chat.messages.length-1].content;
    return sender + ": " + content;
  }
}
