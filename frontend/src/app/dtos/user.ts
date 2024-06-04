import {MedicationDto} from "./medication";
import {AllergyDto} from "./allergy";

export interface UserDtoCreate {
  svnr?: string;
  email: string;
  firstname: string;
  lastname: string;
  medicines?: MedicationDto[];
  allergies?: AllergyDto[];
}

export interface UserDto {
  id: number;
  svnr?: string;
  email: string;
  firstname: string;
  lastname: string;
  password: string;
  medicines?: MedicationDto[];
  allergies?: AllergyDto[];
  active?: boolean;
}

export interface UserDtoUpdate {
  svnr?: string;
  email: string;
  firstname: string;
  lastname: string;
  medicines?: MedicationDto[];
  allergies?: AllergyDto[];
  active?: boolean;
}

export interface UserDtoSearch {
  email?: string;
  firstname?: string;
  lastname?: string;
  limit?: number;
}

export interface UserDtoList {
  id: number,
  firstname: string,
  lastname: string,
  email: string,
  svnr?: string
}
