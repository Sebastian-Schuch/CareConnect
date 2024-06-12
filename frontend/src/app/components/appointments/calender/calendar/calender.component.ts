import {ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef, ViewChild,} from '@angular/core';
import {
  addMonths,
  endOfMonth,
  getDate,
  getDay,
  getHours,
  getMinutes,
  getMonth,
  getYear,
  isBefore,
  startOfMonth,
  subMonths,
} from 'date-fns';
import {forkJoin, Subject} from 'rxjs';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {
  CalendarDateFormatter,
  CalendarEvent,
  CalendarEventTimesChangedEvent,
  CalendarEventTitleFormatter,
  CalendarView,
  CalendarWeekViewBeforeRenderEvent,
} from 'angular-calendar';
import {WeekViewHourColumn} from 'calendar-utils';
import {CustomDateFormatterService} from "../../../../services/custom-date-formatter.service";
import {CustomCalenderTooltipsService} from "../../../../services/custom-calender-tooltips.service";
import {CalenderService} from "../../../../services/calender.service";
import {ToastrService} from "ngx-toastr";
import {AppointmentService} from "../../../../services/appointment.service";
import {OutpatientDepartmentDto} from "../../../../dtos/outpatient-department";
import {AppointmentDtoCalendar, AppointmentDtoCreate} from "../../../../dtos/appointment";
import {UserDto} from "../../../../dtos/user";
import {OpeningHoursDto} from "../../../../dtos/opening-hours";
import {Router} from "@angular/router";
import {ErrorFormatterService} from "../../../../services/error-formatter.service";


@Component({
  selector: 'app-calender',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: 'calender.component.html',
  providers: [
    {
      provide: CalendarDateFormatter,
      useClass: CustomDateFormatterService,

    },
    {
      provide: CalendarEventTitleFormatter,
      useClass: CustomCalenderTooltipsService,
    },
  ],
})
export class CalenderComponent implements OnInit {
  @Input() outpatientDepartment: OutpatientDepartmentDto;
  @Input() patient: UserDto;
  @ViewChild('modalContent', {static: true}) modalContent: TemplateRef<any>;


  view: CalendarView = CalendarView.Month;

  CalendarView = CalendarView;

  viewDate: Date = new Date();

  modalData: {
    action: string;
    event: CalendarEvent;
  };

  selectedDayViewDate: Date;

  hourColumns: WeekViewHourColumn[];

  refresh = new Subject<void>();

  events: CalendarEvent[] = [];

  activeDayIsOpen: boolean = false;

  bookedAppointments: AppointmentDtoCalendar[] = [];

  startDate: Date = new Date();
  endDate: Date = new Date();

  dayStartHour: number = 0;
  dayEndHour: number = 24;

  constructor(
    private modal: NgbModal,
    private service: CalenderService,
    private notification: ToastrService,
    private appointmentService: AppointmentService,
    private errorFormatterService: ErrorFormatterService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.setStartAndEndHour(this.outpatientDepartment.openingHours);
    // Since the ngOnChanges Method also calls, when the component is initialized, we don't need to call loadSlotsAndBookedAppointmentsOfMonth here
  }

  ngOnChanges(): void {
    this.loadSlotsAndBookedAppointmentsOfMonth(this.viewDate);
  }

  private setStartAndEndHour(openingHours: OpeningHoursDto) {
    let min = 24;
    let max = 0;
    for (let i = 0; i < 7; i++) {
      if (!(this.service.getDayOfWeek(openingHours, i) == null)) {
        let tmpmax = this.service.getCloseHourOfDay(this.service.getDayOfWeek(openingHours, i));
        let tmpmin = this.service.getOpenHourOfDay(this.service.getDayOfWeek(openingHours, i));
        if (tmpmax > max) {
          max = tmpmax;
        }
        if (tmpmin < min) {
          min = tmpmin;
        }
      }
      if (min > max) {
        this.dayStartHour = 0;
        this.dayEndHour = 24;
      } else {
        if (min > 0) {
          this.dayStartHour = min - 1;
        }
        if (max < 24) {
          this.dayEndHour = max + 1;
        }
      }
    }
  }

  /**
   * Load the slots and booked appointments for the given month and the surrounding months
   *
   * @param dateOfMonth the date of the month to load the slots and appointments for
   */
  public loadSlotsAndBookedAppointmentsOfMonth(dateOfMonth: Date) {
    this.startDate = startOfMonth(subMonths(dateOfMonth, 1));
    this.endDate = endOfMonth(addMonths(dateOfMonth, 1));
    if (this.patient) {
      forkJoin({
        bookedAppointments: this.appointmentService.getAppointmentsFromOutpatientDepartmentForTimePeriod(this.outpatientDepartment.id, this.startDate, this.endDate),
        patientAppointments: this.appointmentService.getAppointmentsFromPatient(this.patient.id),
      }).subscribe(({bookedAppointments, patientAppointments}) => {
        this.bookedAppointments = bookedAppointments;
        this.events = this.service.getCalendarEventDataForSpecifiedTime(this.outpatientDepartment, this.bookedAppointments, patientAppointments, this.startDate, this.endDate);
        this.refresh.next();
      });
    } else {
      let observable = this.appointmentService.getAppointmentsFromOutpatientDepartmentForTimePeriod(this.outpatientDepartment.id, this.startDate, this.endDate)
      observable.subscribe({
        next: (bookedAppointments) => {
          this.bookedAppointments = bookedAppointments;
          this.events = this.service.getCalendarEventDataForSpecifiedTime(this.outpatientDepartment, this.bookedAppointments, [], this.startDate, this.endDate);
          this.refresh.next();
        },
        error: error => {
          console.error(error);
          this.notification.error('Error', 'Could not load appointments');
        }
      });
    }
  }

  /**
   * Calculate the maximum slots for the given date
   *
   * @param date
   */
  public calcMaxSlots(date: Date): number {
    let currentday = this.events.filter(event => (event.start.getFullYear() === date.getFullYear() && event.start.getMonth() === date.getMonth() && event.start.getDate() === date.getDate() && event.meta.maxCapacity >= 0));
    return currentday.reduce((acc, event) => acc + event.meta.maxCapacity, 0);
  }

  /**
   * Set the badge color for the given date
   *
   * @param date the date to set the badge color for
   */
  public getBadgeColor(date: Date) {
    return this.service.getEventColorBadgeMonth(this.calcCurrentSlots(date), this.calcMaxSlots(date), date, this.service.getDayOfWeek(this.outpatientDepartment.openingHours, getDay(date))).primary;
  }

  public isInPast(date: Date): boolean {
    return this.service.isInPast(date, this.service.getDayOfWeek(this.outpatientDepartment.openingHours, getDay(date)));
  }

  /**
   * Calculate the current slots for the given date
   *
   * @param date the date to calculate the current slots for
   */
  public calcCurrentSlots(date: Date): number {
    let currentday = this.events.filter(event => (event.start.getFullYear() === date.getFullYear() && event.start.getMonth() === date.getMonth() && event.start.getDate() === date.getDate() && event.meta.curCapacity >= 0));
    return currentday.reduce((acc, event) => acc + event.meta.curCapacity, 0);
  }

  /**
   * Calculate the free slots for the given date
   *
   * @param date the date to calculate the free slots for
   */
  public calcFreeSlots(date: Date): number {
    let currentday = this.events.filter(event => (event.start.getFullYear() === date.getFullYear() && event.start.getMonth() === date.getMonth() && event.start.getDate() === date.getDate() && event.meta.maxCapacity >= 0 && event.meta.curCapacity >= 0 && !event.meta.pastTime));
    let maxCap = currentday.reduce((acc, event) => acc + event.meta.maxCapacity, 0);
    let curCap = currentday.reduce((acc, event) => acc + event.meta.curCapacity, 0);
    return maxCap - curCap;
  }

  /**
   * Book an appointment for the given Calendar event
   *
   * @param event the calendar event to book an appointment for
   */
  public bookAppointment(event: CalendarEvent) {
    if (event.meta.curCapacity < event.meta.maxCapacity) {
      let appointment: AppointmentDtoCreate = {
        startDate: event.start,
        endDate: event.end,
        outpatientDepartment: this.outpatientDepartment,
        patient: this.patient,
        notes: ''
      }
      let observable = this.appointmentService.createAppointment(appointment);
      observable.subscribe({
        next: (appointment) => {
          this.notification.success('Success', `Appointment booked at ${this.getStringDate(appointment.startDate.toString())}`);
          this.loadSlotsAndBookedAppointmentsOfMonth(this.viewDate);
        },
        error: async error => {
          await this.errorFormatterService.printErrorToNotification(error, `Could not book appointment`, this.notification, "Not patient selected");

        }
      });
    } else {
      this.notification.error('Error', 'No more slots available');
    }
  }

  /**
   * Cancel the appointment for the given Calendar event
   *
   * @param event the calendar event to cancel the appointment for
   */
  public cancelAppointment(event: CalendarEvent) {
    this.appointmentService.cancelAppointment(event.meta.appointmentId).subscribe({
      next: () => {
        this.notification.success('Success', 'Appointment cancelled');
        this.loadSlotsAndBookedAppointmentsOfMonth(this.viewDate);
      },
      error: error => {
        console.error(error);
        this.notification.error('Error', 'Could not cancel appointment');
      }
    });
  }

  /**
   * Changes the view to the given date
   *
   * @param date the date to change the view to
   * @param events the events for the given date
   */
  dayClicked({date, events}: { date: Date; events: CalendarEvent[] }): void {
    this.viewDate = date;
    this.view = CalendarView.Day;
  }

  eventTimesChanged({
                      event,
                      newStart,
                      newEnd,
                    }: CalendarEventTimesChangedEvent): void {
    this.events = this.events.map((iEvent) => {
      if (iEvent === event) {
        return {
          ...event,
          start: newStart,
          end: newEnd,
        };
      }
      return iEvent;
    });
    this.handleEvent('Dropped or resized', event);
  }

  handleEvent(action: string, event: CalendarEvent): void {
    if (event.cssClass !== 'not-open-time') {
      this.modalData = {event, action};
      this.modal.open(this.modalContent, {size: 'lg'});
    }
  }

  /**
   * Set the view to the given view
   *
   * @param view the view to set
   */
  setView(view: CalendarView) {
    this.view = view;
  }

  closeOpenMonthViewDay() {
    this.activeDayIsOpen = false;
    this.loadSlotsAndBookedAppointmentsOfMonth(this.viewDate);
  }

  hourSegmentClicked(date: Date) {
    this.selectedDayViewDate = date;
    //this.addSelectedDayViewClass();
  }

  beforeWeekOrDayViewRender(event: CalendarWeekViewBeforeRenderEvent) {
    this.hourColumns = event.hourColumns;
    this.addSelectedDayViewClass();
  }

  /**
   * Get the string representation of the given date
   *
   * @param date the date to get the string representation for
   */
  private getStringDate(date: string) {
    return this.getDayString(date) + ' ' + this.getHourString(date);
  }

  private getHourString(date: string) {
    let hours = getHours(date);
    let minutes = getMinutes(date);
    let output;
    if (hours < 10) {
      output = '0' + hours + ':';
    } else {
      output = hours + ':';
    }
    if (minutes < 10) {
      output += '0' + minutes;
    } else {
      output += minutes;
    }
    return output;
  }

  public getDayString(day: any): string {
    return getDate(day) + '/' + (getMonth(day) + 1) + '/' + getYear(day)
  }

  public getWeekdayString(i: number) {
    switch (i) {
      case 0:
        return 'Sunday';
      case 1:
        return 'Monday';
      case 2:
        return 'Tuesday';
      case 3:
        return 'Wednesday';
      case 4:
        return 'Thursday';
      case 5:
        return 'Friday';
      case 6:
        return 'Saturday';
    }
  }

  private addSelectedDayViewClass() {
    this.hourColumns.forEach((column) => {
      column.hours.forEach((hourSegment) => {
        hourSegment.segments.forEach((segment) => {
          delete segment.cssClass;
          if (
            this.selectedDayViewDate &&
            segment.date.getTime() === this.selectedDayViewDate.getTime()
          ) {
            segment.cssClass = 'cal-day-selected';
          }
        });
      });
    });
  }

  protected readonly isBefore = isBefore;
}
