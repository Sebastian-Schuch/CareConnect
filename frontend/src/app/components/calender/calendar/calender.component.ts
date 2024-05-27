import {ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef, ViewChild,} from '@angular/core';
import {addMonths, endOfMonth, startOfMonth, subMonths,} from 'date-fns';
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
import {CustomDateFormatterService} from "../../../services/custom-date-formatter.service";
import {CustomCalenderTooltipsService} from "../../../services/custom-calender-tooltips.service";
import {CalenderService} from "../../../services/calender.service";
import {ToastrService} from "ngx-toastr";
import {AppointmentService} from "../../../services/appointment.service";
import {OutpatientDepartmentDto} from "../../../dtos/outpatient-department";
import {AppointmentCalendarDto, AppointmentCreateDto} from "../../../dtos/appointment";
import {UserDetailDto} from "../../../dtos/user";


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
  @Input() patient: UserDetailDto;
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

  bookedAppointments: AppointmentCalendarDto[] = [];

  startDate: Date = new Date();
  endDate: Date = new Date();

  constructor(
    private modal: NgbModal,
    private service: CalenderService,
    private notification: ToastrService,
    private appointmentService: AppointmentService) {
  }

  ngOnInit(): void {
    this.loadSlotsAndBookedAppointmentsOfMonth(new Date())
  }

  ngOnChanges(): void {
    this.loadSlotsAndBookedAppointmentsOfMonth(this.viewDate);
  }

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
          this.notification.error('Error', 'Could not load appointments');
        }
      });
    }
  }

  public calcMaxSlots(data: Date): number {
    let currentday = this.events.filter(event => (event.start.getFullYear() === data.getFullYear() && event.start.getMonth() === data.getMonth() && event.start.getDate() === data.getDate() && event.meta.maxCapacity >= 0));
    return currentday.reduce((acc, event) => acc + event.meta.maxCapacity, 0);
  }

  public getBadgeColor(date: Date) {
    return this.service.getEventColor(this.calcCurrentSlots(date), this.calcMaxSlots(date)).primary;
  }

  public calcCurrentSlots(data: Date): number {
    let currentday = this.events.filter(event => (event.start.getFullYear() === data.getFullYear() && event.start.getMonth() === data.getMonth() && event.start.getDate() === data.getDate() && event.meta.curCapacity >= 0));
    return currentday.reduce((acc, event) => acc + event.meta.curCapacity, 0);
  }

  public calcFreeSlots(date: Date): number {
    let currentday = this.events.filter(event => (event.start.getFullYear() === date.getFullYear() && event.start.getMonth() === date.getMonth() && event.start.getDate() === date.getDate() && event.meta.maxCapacity >= 0 && event.meta.curCapacity >= 0));
    let maxCap = currentday.reduce((acc, event) => acc + event.meta.maxCapacity, 0);
    let curCap = currentday.reduce((acc, event) => acc + event.meta.curCapacity, 0);
    return maxCap - curCap;
  }

  public bookAppointment(event: CalendarEvent) {
    if (event.meta.curCapacity < event.meta.maxCapacity) {
      let appointment: AppointmentCreateDto = {
        startDate: event.start,
        endDate: event.end,
        outpatientDepartment: this.outpatientDepartment,
        patient: this.patient,
        notes: ''
      }
      let observable = this.appointmentService.createAppointment(appointment);
      observable.subscribe({
        next: (appointment) => {
          this.notification.success('Success', 'Appointment booked');
          this.loadSlotsAndBookedAppointmentsOfMonth(this.viewDate);
        },
        error: error => {
          this.notification.error('Error', 'Could not book appointment');
        }
      });
    } else {
      this.notification.error('Error', 'No more slots available');
    }
  }

  public cancelAppointment(event: CalendarEvent) {
    console.log("Cancel Appointment ", event);
    //TODO: Cancel Appointment
  }


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
}
