export interface MedicationDtoCreate {
  name: string;
}

export interface MedicationDto {
  id: number;
  name: string;
}

export interface MedicationPageDto {
  medications: MedicationDto[];
  totalItems: number;
}
