import {OpeningHoursDto, OpeningHoursDtoCreate} from "./opening-hours";

export class OutpatientDepartmentDtoCreate {
  name: string;
  description: string;
  capacity: number;
  openingHours: OpeningHoursDtoCreate;
}

export class OutpatientDepartmentDto {
  id: number;
  name: string;
  description: string;
  capacity: number;
  openingHours: OpeningHoursDto;
}

export interface CapacityDto {
  occupied: number;
  capacity: number;
}

export interface OutpatientDepartmentCapacityDto {
  outpatientDepartment: OutpatientDepartmentDto;
  capacityNowDto: CapacityDto;
  capacityDayDto: CapacityDto;
  capacityWeekDto: CapacityDto;
  capacityMonthDto: CapacityDto;
}





