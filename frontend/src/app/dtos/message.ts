import {Timestamp} from "rxjs";

export interface MessageDtoCreate {
  content: string,
  treatmentId: number
}

export interface MessageDto {
  id?: number,
  content: string,
  displayname: string,
  treatmentId: number,
  timestamp: Date,
  senderEmail: string,
  read: boolean
}
