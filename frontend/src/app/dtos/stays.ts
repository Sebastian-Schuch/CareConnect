import {InpatientDepartmentDto} from "./inpatient-department";

export interface StayDto {
  id: number;
  inpatientDepartment: InpatientDepartmentDto;
  arrival: Date;
  discharge: Date;
}

export interface StayDtoCreate {
  inpatientDepartment: InpatientDepartmentDto;
  patientId: number;
}

export interface StayDtoPage {
  content: StayDto[];
  totalElements: number;
}
