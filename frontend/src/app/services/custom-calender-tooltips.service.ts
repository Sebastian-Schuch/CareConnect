import {Injectable} from '@angular/core';
import {CalendarEvent, CalendarEventTitleFormatter} from "angular-calendar";

@Injectable()
export class CustomCalenderTooltipsService extends CalendarEventTitleFormatter {

  monthTooltip(event: CalendarEvent): string {
    return;
  }

  weekTooltip(event: CalendarEvent): string {
    return;
  }

  dayTooltip(event: CalendarEvent): string {
    return event.meta.tooltip;
  }
}
