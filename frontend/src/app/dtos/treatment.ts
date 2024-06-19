import {TreatmentMedicineDtoCreate, TreatmentMedicineSelection} from "./treatmentMedicine";
import {UserDto} from "./user";
import {OutpatientDepartmentDto} from "./outpatient-department";

export interface TreatmentDtoCreate {
  treatmentTitle: string;
  treatmentStart: Date;
  treatmentEnd: Date;
  patient: UserDto;
  outpatientDepartment: OutpatientDepartmentDto;
  treatmentText?: string;
  doctors: UserDto[];
  medicines?: TreatmentMedicineDtoCreate[];
}

export interface TreatmentDto {
  id: number;
  treatmentTitle: string;
  treatmentStart: Date;
  treatmentEnd: Date;
  patient: UserDto;
  outpatientDepartment: OutpatientDepartmentDto;
  treatmentText?: string;
  doctors: UserDto[];
  medicines?: TreatmentMedicineSelection[];
}

export interface TreatmentDtoSearch {
  page: number,
  size: number,
  patientId: number,
  startDate: Date,
  endDate: Date,
  treatmentTitle: string,
  medicationName: string,
  doctorName: string,
  patientName: string,
  departmentName: string
}

export interface TreatmentPageDto {
  treatments: TreatmentDto[];
  totalItems: number;
}
