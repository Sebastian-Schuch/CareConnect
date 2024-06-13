import {StationDto} from "./Station";

export interface StayDto {
  id: number;
  station: StationDto;
  arrival: Date;
  discharge: Date;
}

export interface StayDtoCreate {
  station: StationDto;
  patientId: number;
}

export interface StayDtoPage {
  content: StayDto[];
  totalElements: number;
}
