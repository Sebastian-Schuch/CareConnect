import {StationDto} from "./Station";

export interface StayDto {
  id: number;
  station: StationDto;
  arrival: string;
  discharge: string;
}

export interface StayDtoCreate {
  station: StationDto;
  patientId: string;
}

export interface StayDtoPage {
  content: StayDto[];
  totalElements: number;
}
