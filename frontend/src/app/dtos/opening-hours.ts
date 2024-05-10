export class OpeningHoursDto {
  id: number;
  monday: OpeningHoursDayDto;
  tuesday: OpeningHoursDayDto;
  wednesday: OpeningHoursDayDto;
  thursday: OpeningHoursDayDto;
  friday: OpeningHoursDayDto;
  saturday: OpeningHoursDayDto;
  sunday: OpeningHoursDayDto;
}

export class OpeningHoursDtoCreate {
  monday: OpeningHoursDayDto;
  tuesday: OpeningHoursDayDto;
  wednesday: OpeningHoursDayDto;
  thursday: OpeningHoursDayDto;
  friday: OpeningHoursDayDto;
  saturday: OpeningHoursDayDto;
  sunday: OpeningHoursDayDto;
}

export class OpeningHoursDayDto {
  open: Date;
  close: Date;
}
