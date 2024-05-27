import {MessageDto} from "./message";

export interface ChatDto {
  id: number,
  name: string,
  messages: MessageDto[]
}
