export interface StationDtoCreate {
  name: string;
  capacity: number;
}

export interface StationDto {
  id: number;
  name: string;
  capacity: number;
}

export interface StationPageDto {
  stations: StationDto[];
  totalItems: number;
}
