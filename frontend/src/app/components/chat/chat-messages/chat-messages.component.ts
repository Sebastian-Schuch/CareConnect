import {AfterViewInit, Component, ElementRef, Input, OnChanges, OnInit, ViewChild} from '@angular/core';
import {DatePipe, NgClass, NgForOf} from "@angular/common";
import {MatCard, MatCardContent} from "@angular/material/card";
import {ChatInputComponent} from "../chat-input/chat-input.component";
import {AddChatComponent} from "../add-chat/add-chat.component";
import {ChatDto} from "../../../dtos/chat";
import {MessageDto, MessageDtoCreate} from "../../../dtos/message";
import {AuthService} from "../../../services/auth.service";
import {MessageService} from "../../../services/message.service";

@Component({
  selector: 'app-chat-messages',
  standalone: true,
  imports: [
    NgForOf,
    MatCard,
    MatCardContent,
    ChatInputComponent,
    NgClass,
    DatePipe,
    AddChatComponent
  ],
  templateUrl: './chat-messages.component.html',
  styleUrl: './chat-messages.component.scss'
})
export class ChatMessagesComponent implements OnChanges, OnInit, AfterViewInit{
  @ViewChild("sectionSubscribe", { static: true }) sectionSubscribeDiv: ElementRef;
  @Input() chat: ChatDto;
  messages: MessageDto[] = [];
  ownEmail: string;


  constructor(private authService: AuthService, private messageService: MessageService) {
  }

  ngOnInit() {
    this.ownEmail = this.authService.getUserEmail();
  }

  ngAfterViewInit() {
    setTimeout(() => this.scrollToBottom(), 50);
  }

  ngOnChanges() {
    if(this.chat === undefined) {
      return
    }
    this.messages = this.chat.messages;
    setTimeout(() => this.scrollToBottom(), 50);
  }

  addMessage(message: string) {
    const newMessage : MessageDtoCreate = { content: message, treatmentId: this.chat.id};
    console.log(newMessage);
    this.messageService.sendMessage(newMessage);
    setTimeout(() => this.scrollToBottom(), 50);
  }

  getDateAsString(ts: Date) {
    const date = new Date(ts);

    const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Months are 0-based
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');

    return `${day}.${month}. ${hours}:${minutes}`;
  }

  getDisplayName(message: MessageDto) {
    return message.senderEmail === this.ownEmail ? "Me" : message.displayname;
  }

  private scrollToBottom(): void {
    this.sectionSubscribeDiv.nativeElement.scrollIntoView({
      behavior: "smooth",
      block: "start"
    });
  }
}
