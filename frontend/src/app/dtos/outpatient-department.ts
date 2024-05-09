import {OpeningHoursDto, OpeningHoursDtoCreate} from "./opening-hours";

export class OutpatientDepartmentDtoCreate{
  name: string;
  description: string;
  capacity: number;
  openingHours: OpeningHoursDtoCreate;
}

export class OutpatientDepartmentDto{
  id: number;
  name: string;
  description: string;
  capacity: number;
  openingHours: OpeningHoursDto;
}

