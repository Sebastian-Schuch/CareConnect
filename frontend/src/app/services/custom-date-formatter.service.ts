import {Injectable} from '@angular/core';
import {CalendarDateFormatter, DateFormatterParams} from "angular-calendar";
import {formatDate} from "date-fns";
import {de} from 'date-fns/locale';

@Injectable()
export class CustomDateFormatterService extends CalendarDateFormatter {

  public dayViewHour({date}: DateFormatterParams): string {
    return formatDate(date, 'HH:mm', {locale: de});
  }

  public weekViewHour({date, locale}: DateFormatterParams): string {
    return this.dayViewHour({date, locale});
  }
}
