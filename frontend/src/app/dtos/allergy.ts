export interface AllergyDtoCreate {
  name: string;
}

export interface AllergyDto {
  id: number;
  name: string;
}

export interface AllergyPageDto {
  allergies: AllergyDto[];
  totalItems: number;
}
