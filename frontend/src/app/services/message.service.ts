import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';

import * as Stomp from '@stomp/stompjs';
import {MessageDto, MessageDtoCreate} from "../dtos/message";
import {AuthService} from "./auth.service";
import {Role} from "../dtos/Role";
import {ChatDto} from "../dtos/chat";
@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private messageBaseUri: string = 'ws://localhost:8080/api/v1/chat';

  private messageRestUri: string = this.globals.backendUri + '/messages';

  private connection: Stomp.CompatClient | undefined;

  private subscription: Stomp.StompSubscription | undefined;
  private messageSubscription: Observable<any> | undefined;

  constructor(private httpClient: HttpClient, private globals: Globals, private authService: AuthService) {
  }

  /**
   * Initializes the web socket connection
   */
  initWebSocket() : Promise<void>{
      return new Promise((resolve, reject) => {
        if(this.authService.isLoggedIn() && (this.authService.getUserRole()===Role.doctor||this.authService.getUserRole()===Role.patient)){
          this.connection = Stomp.Stomp.client(this.messageBaseUri);
          this.connection.connect({
            'Authorization': this.authService.getToken()
          }, () => {
            resolve();
          });
        }
      });
  }

  /**
   * listens to the messages
   * @param callback
   * @param chatId
   */
  listenToMessages(chatId: number,callback: (message: MessageDto) => void) {
    if(this.connection && this.connection.connected){
      this.subscription = this.connection.subscribe('/topic/messages/'+chatId, (message) => {
        callback(JSON.parse(message.body));
      });
    }
  }

  /**
   * sends Messages to the backend
   *
   * @param message to send
   * @param chatId
   */
  sendMessage(message: MessageDtoCreate) {
    if(this.connection && this.connection.connected){
      this.connection.publish({
        destination: '/app/chat/'+message.treatmentId,
        body: JSON.stringify(message)
      });
    }
  }

  /**
   * Get all messages from a specific chat
   * @param chatId
   */
  getChatMessages(chatId: number): Observable<ChatDto> {
    return this.httpClient.get<ChatDto>(`${this.messageRestUri}/${chatId}`);
  }

  getAvailableChats(): Observable<ChatDto[]> {
    return this.httpClient.get<ChatDto[]>(`${this.messageRestUri}/available`);
  }

  getActiveChats(): Observable<ChatDto[]> {
    return this.httpClient.get<ChatDto[]>(`${this.messageRestUri}/active`);
  }
}
