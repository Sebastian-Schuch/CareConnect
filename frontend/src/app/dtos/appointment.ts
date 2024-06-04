import {UserDto} from "./user";
import {OutpatientDepartmentDto} from "./outpatient-department";

export interface AppointmentDtoCreate {
  patient: UserDto;
  outpatientDepartment: OutpatientDepartmentDto;
  startDate: Date;
  endDate: Date;
  notes: string;
}

export interface AppointmentDto {
  id: number;
  patient: UserDto;
  outpatientDepartment: OutpatientDepartmentDto;
  startDate: Date;
  endDate: Date;
  notes: string;
}

export interface AppointmentDtoSearch {
  outpatientDepartmentId: number;
  startDate: Date;
  endDate: Date;
}

export interface AppointmentDtoCalendar {
  outpatientDepartmentId: number;
  startDate: Date;
  endDate: Date;
  count: number;
}
