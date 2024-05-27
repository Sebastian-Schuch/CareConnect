import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {MatButtonModule} from '@angular/material/button';
import {MatDialog} from "@angular/material/dialog";
import {NewChatDialogComponent} from "./new-chat-dialog/new-chat-dialog.component";
import {ChatDto} from "../../../dtos/chat";
import {MessageService} from "../../../services/message.service";


@Component({
  selector: 'app-add-chat',
  standalone: true,
  imports: [
    FormsModule,
    MatButtonModule,
    MatDividerModule,
    MatIconModule
  ],
  templateUrl: './add-chat.component.html',
  styleUrl: './add-chat.component.scss'
})
export class AddChatComponent implements OnInit{
  availableNewChats: ChatDto[] = [];
  @Output() chatAdded = new EventEmitter<ChatDto>();

  constructor(public dialog: MatDialog, private messageService: MessageService) {}

  ngOnInit() {
    this.messageService.getAvailableChats().subscribe({
      next: (data) => {
        this.availableNewChats = data
      },
      error: (err) => {
        console.error(err)
      }
    })
  }

  addChat(newChat: ChatDto) {
    if(newChat === undefined) {
      return;
    }

    if (newChat.name.trim()) {
      this.chatAdded.emit(newChat);
    }

    this.availableNewChats = this.availableNewChats.filter(item => item.name !== newChat.name);
  }

  openNewChatDialog(): void {
    const dialogRef = this.dialog.open(NewChatDialogComponent, {
      data: {available: this.availableNewChats},
    });

    dialogRef.afterClosed().subscribe(result => {
      this.addChat(result);
    });
  }
}
