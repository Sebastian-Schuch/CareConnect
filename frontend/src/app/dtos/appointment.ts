import {UserDetailDto} from "./user";
import {OutpatientDepartmentDto} from "./outpatient-department";

export interface AppointmentCreateDto {
  patient: UserDetailDto;
  outpatientDepartment: OutpatientDepartmentDto;
  startDate: Date;
  endDate: Date;
  notes: string;
}

export interface AppointmentDetailDto {
  id: number;
  patient: UserDetailDto;
  outpatientDepartment: OutpatientDepartmentDto;
  startDate: Date;
  endDate: Date;
  notes: string;
}

export interface AppointmentSearchDto {
  outpatientDepartmentId: number;
  startDate: Date;
  endDate: Date;
}

export interface AppointmentCalendarDto {
  outpatientDepartmentId: number;
  startDate: Date;
  endDate: Date;
  count: number;
}
