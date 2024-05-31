import {MedicationDto} from "./medication";
import {AllergyDetailDto} from "./allergy";

export interface UserCreateDto {
  svnr?: string;
  email: string;
  firstname: string;
  lastname: string;
  medicines?: MedicationDto[];
  allergies?: AllergyDetailDto[];
}

export interface UserDetailDto {
  id: number;
  svnr?: string;
  email: string;
  firstname: string;
  lastname: string;
  password: string;
  medicines?: MedicationDto[];
  allergies?: AllergyDetailDto[];
}

export interface UserLoginDto {
  email: string;
  password: string;
}
