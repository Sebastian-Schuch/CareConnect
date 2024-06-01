import {Injectable} from '@angular/core';
import {
  addDays,
  addHours,
  addMinutes,
  getDate,
  getDay,
  getHours,
  getMinutes,
  getMonth,
  getYear,
  isAfter,
  isBefore,
  isEqual,
  setDay,
  setHours,
  setMinutes,
  startOfDay,
  subDays,
  subSeconds
} from "date-fns";
import {CalendarEvent} from "angular-calendar";
import {EventColor} from "calendar-utils";
import {OutpatientDepartmentDto} from "../dtos/outpatient-department";
import {AppointmentCalendarDto, AppointmentDetailDto} from "../dtos/appointment";
import {OpeningHoursDayDto, OpeningHoursDto} from "../dtos/opening-hours";

export const colors: Record<string, EventColor> = {
  red: {
    primary: '#ad2121',
    secondary: '#FAE3E3',
  },
  blue: {
    primary: '#1e90ff',
    secondary: '#D1E8FF',
  },
  yellow: {
    primary: '#e3bc08',
    secondary: '#FDF1BA',
  },
  green: {
    primary: 'rgba(25,236,79,0.33)',
    secondary: 'rgba(17,168,55,0.35)',
  },
  empty: {
    primary: 'rgba(17,168,55,0.5)',
    secondary: 'rgba(17,168,55,0.5)',
  },
  minimal: {
    primary: 'rgba(135,206,235,0.5)',
    secondary: 'rgba(135,206,235,0.5)',
  },
  half: {
    primary: 'rgba(255,215,0,0.5)',
    secondary: 'rgba(255,215,0,0.5)',
  },
  mostlyFull: {
    primary: 'rgba(255,140,0,0.5)',
    secondary: 'rgba(255,140,0,0.5)',
  },
  full: {
    primary: 'rgba(139,0,0,0.5)',
    secondary: 'rgba(139,0,0,0.5)',
  }
};

@Injectable({
  providedIn: 'root'
})
export class CalenderService {

  constructor() {
  }

  /**
   * This function returns test data for the calendar
   */
  public getCalenderEventTestData(): CalendarEvent[] {
    //Theses two events are the closed times of the ambulance
    let events: CalendarEvent[] = [
      {
        start: addHours(startOfDay(setDay(new Date(), 3)), 0),
        end: subSeconds(addHours(startOfDay(setDay(new Date(), 3)), 7), 1),
        title: 'Closed',
        color: colors.red,
        draggable: false,
        cssClass: 'not-open-time',
        meta: {
          tooltip: 'Closed my ass'
        }
      },
      {
        start: addHours(startOfDay(setDay(new Date(), 3)), 20),
        end: subSeconds(addHours(startOfDay(setDay(new Date(), 3)), 24), 1),
        title: 'Closed',
        color: colors.red,
        draggable: false,
        cssClass: 'not-open-time',
        meta: {
          tooltip: 'Closed my ass'
        }
      },
    ];

    //this forloop creates events for each half hour intervall during the day
    for (let i = 0; i < 26; i++) {
      let maxCap: number = 10;
      let curCap: number = Math.floor(Math.random() * maxCap) + 1;
      let ratio: number = curCap / maxCap;

      let tmp: CalendarEvent = {
        start: addMinutes(addHours(startOfDay(setDay(new Date(), 3)), 7), i * 30),
        end: addMinutes(addHours(startOfDay(setDay(new Date(), 3)), 7), 30 + i * 30),
        title: curCap + '/' + maxCap + ' ' + ((ratio < 1) ? 'Open' : 'Already booked out'),
        color: this.getEventColor(curCap, maxCap, addMinutes(addHours(startOfDay(setDay(new Date(), 3)), 7), i * 30)),
        draggable: false,
        cssClass: 'open-time',
        meta: {
          maxCapacity: 10,
          curCapacity: curCap,
          tooltip: curCap + '/' + maxCap
        }
      }
      events.push(tmp);
    }


    return events;
  }

  /**
   * Iterates over the entire time specified in the parameters
   *
   * @param outpatientDepartment the outpatient department for which the events should be created
   * @param bookedAppointments the booked appointments for the outpatient department
   * @param patientAppointment the appointments of the patient
   * @param startDate the start date of the time period
   * @param endDate the end date of the time period
   */
  public getCalendarEventDataForSpecifiedTime(outpatientDepartment: OutpatientDepartmentDto, bookedAppointments: AppointmentCalendarDto[], patientAppointment: AppointmentDetailDto[], startDate: Date, endDate: Date): CalendarEvent[] {
    let events: CalendarEvent[] = [];
    for (let currentDiff = 0; addDays(startDate, currentDiff) < endDate; currentDiff++) {
      let currentOpeningHours: OpeningHoursDayDto = this.getDayOfWeek(outpatientDepartment.openingHours, getDay(addDays(startDate, currentDiff)));
      let tmp = this.calculateSlotsOfDay(currentOpeningHours, outpatientDepartment.capacity, bookedAppointments, this.getPatientAppointmentFromOutpatientDepartment(outpatientDepartment.id, patientAppointment), addDays(startDate, currentDiff), getDay(addDays(startDate, currentDiff)));
      for (let i = 0; i < tmp.length; i++) {
        events.push(tmp[i])
      }
    }
    return events;
  }

  /**
   * This function returns the appointments of a patient for a specific outpatient department
   *
   * @param outpatientDepartmentId the id of the outpatient department
   * @param patientAppointment the appointments of the patient
   */
  private getPatientAppointmentFromOutpatientDepartment(outpatientDepartmentId: number, patientAppointment: AppointmentDetailDto[]): AppointmentDetailDto[] {
    let appointmentsFromThisOutpatientDepartment: AppointmentDetailDto[] = [];
    for (let i = 0; i < patientAppointment.length; i++) {
      if (patientAppointment[i].outpatientDepartment.id == outpatientDepartmentId) {
        appointmentsFromThisOutpatientDepartment.push(patientAppointment[i]);
      }
    }
    return appointmentsFromThisOutpatientDepartment;
  }

  /**
   * This function maps fullness to a color representation
   *
   * @param curCap is the current fullness
   * @param maxCap is the maximum fullness
   * @param date is the date of the event
   *
   * @private
   */
  public getEventColor(curCap: number, maxCap: number, date: Date): EventColor {
    const fullness = curCap / maxCap;
    if (isBefore(date, new Date())) {
      return colors.full;
    }
    if (fullness <= 0.1) {
      return colors.empty;
    } else if (fullness < 0.25) {
      return colors.minimal
    } else if (fullness < 0.5) {
      return colors.minimal;
    } else if (fullness < 1) {
      return colors.mostlyFull;
    } else if (fullness === 1) {
      return colors.full;
    } else return colors.green;
  }

  /**
   * This function maps fullness to a color representation
   *
   * @param curCap is the current fullness
   * @param maxCap is the maximum fullness
   * @param date is the date of the event
   * @param openingHoursDay is the opening and closing hours of the day
   *
   * @private
   */
  public getEventColorBadgeMonth(curCap: number, maxCap: number, date: Date, openingHoursDay: OpeningHoursDayDto): EventColor {
    const fullness = curCap / maxCap;
    if (openingHoursDay != null) {
      if (this.isInPast(date, openingHoursDay)) {
        return colors.full;
      }
    } else {
      if (isBefore(date, new Date())) {
        return colors.full;
      }
    }
    if (fullness <= 0.1) {
      return colors.empty;
    } else if (fullness < 0.25) {
      return colors.minimal
    } else if (fullness < 0.5) {
      return colors.minimal;
    } else if (fullness < 1) {
      return colors.mostlyFull;
    } else if (fullness === 1) {
      return colors.full;
    } else return colors.green;
  }

  public isInPast(date: Date, openingHoursDay: OpeningHoursDayDto): boolean {
    if (openingHoursDay == null) {
      return isBefore(date, new Date());
    }
    if (getDate(date) == getDate(new Date()) && getMonth(date) == getMonth(new Date()) && getYear(date) == getYear(new Date())) {
      return isAfter(addMinutes(new Date(), 30), setMinutes(setHours(new Date(), this.getCloseHourOfDay(openingHoursDay)), this.getCloseMinuteOfDay(openingHoursDay)));
    }
    if (isAfter(addDays(date, 1), new Date())) {
      return false;
    }
    if (isBefore(subDays(date, 1), new Date())) {
      return true;
    }
  }

  /**
   * This function returns the opening hours of a specific day
   *
   * @param openingHours the opening hours of the outpatient department
   * @param dayOfWeek the day of the week
   */
  getDayOfWeek(openingHours: OpeningHoursDto, dayOfWeek: number): OpeningHoursDayDto {
    switch (dayOfWeek) {
      case 0:
        return openingHours.sunday;
      case 1:
        return openingHours.monday;
      case 2:
        return openingHours.tuesday;
      case 3:
        return openingHours.wednesday;
      case 4:
        return openingHours.thursday;
      case 5:
        return openingHours.friday;
      case 6:
        return openingHours.saturday;
    }
    return null;
  }

  /**
   * This function calculates the slots of a specific day
   *
   * @param openingHours the opening hours on a specific day of the outpatient department
   * @param capacity the capacity of the outpatient department on that day
   * @param bookedAppointments the booked appointments of the outpatient department
   * @param patientAppointment the appointments of the patient
   * @param weekdate the date of the week
   * @param i the offset from the start of the week
   */
  private calculateSlotsOfDay(openingHours: OpeningHoursDayDto, capacity: number, bookedAppointments: AppointmentCalendarDto[], patientAppointment: AppointmentDetailDto[], weekdate: Date, i: number): CalendarEvent[] {
    let calendarEvents: CalendarEvent[] = [];
    if (openingHours == null || openingHours.isClosed) {
      // This case represents the outpatient department being closed
      calendarEvents.push({
        start: addHours(startOfDay(setDay(weekdate, i, {weekStartsOn: 1})), 0),
        end: subSeconds(addHours(startOfDay(setDay(weekdate, i, {weekStartsOn: 1})), 24), 1),
        title: 'Closed',
        color: colors.red,
        draggable: false,
        cssClass: 'not-open-time',
        meta: {
          tooltip: 'Outpatient Department is closed'
        }
      })
    } else {
      // This case represents the outpatient department being open

      // Calculate the start and end time of the outpatient department
      let startTime = addMinutes(addHours(startOfDay(setDay(weekdate, i, {weekStartsOn: 1})), this.getOpenHourOfDay(openingHours)), this.getOpenMinuteOfDay(openingHours));
      let closeTime = addMinutes(addHours(startOfDay(setDay(weekdate, i, {weekStartsOn: 1})), this.getCloseHourOfDay(openingHours)), this.getCloseMinuteOfDay(openingHours));
      // Add the closed times of the outpatient department
      calendarEvents.push({
        start: addHours(startOfDay(setDay(weekdate, i, {weekStartsOn: 1})), 0),
        end: addMinutes(addHours(startOfDay(setDay(weekdate, i, {weekStartsOn: 1})), this.getOpenHourOfDay(openingHours)), this.getOpenMinuteOfDay(openingHours)),
        title: 'Closed',
        color: colors.red,
        draggable: false,
        cssClass: 'not-open-time',
        meta: {
          tooltip: 'Outpatient Department is closed'
        }
      })
      calendarEvents.push({
        start: addMinutes(addHours(startOfDay(setDay(weekdate, i, {weekStartsOn: 1})), this.getCloseHourOfDay(openingHours)), this.getCloseMinuteOfDay(openingHours)),
        end: subSeconds(addHours(startOfDay(setDay(weekdate, i, {weekStartsOn: 1})), 24), 1),
        title: 'Closed',
        color: colors.red,
        draggable: false,
        cssClass: 'not-open-time',
        meta: {
          tooltip: 'Outpatient Department is closed'
        }
      })
      // Add the timeslots of the day for the outpatient department
      for (let currentDiff = 0; addMinutes(startTime, currentDiff) < closeTime; currentDiff += 30) {
        // Calculate all the information being saved into the metadata of an event
        let curCap: number = this.getCurrentCapacity(bookedAppointments, addMinutes(startTime, currentDiff));
        let ratio: number = curCap / capacity;
        let title: string = curCap + '/' + capacity + ' ' + ((ratio < 1) ? 'Open' : 'Already booked out');
        let appointmentId: number = -1;
        let isMyAppointment: boolean = false;
        let pastTime: boolean = false;
        let tooltip = curCap + '/' + capacity;
        if (isBefore(addMinutes(startTime, currentDiff), new Date())) {
          title = "Past timeslot";
          pastTime = true;
          tooltip = "This timeslot is in the past";
        }
        // Check if the current timeslot is an appointment of the patient
        for (let j = 0; j < patientAppointment.length; j++) {
          if (isEqual(addMinutes(startTime, currentDiff), patientAppointment[j].startDate)) {
            title = "Your appointment"
            isMyAppointment = true;
            appointmentId = patientAppointment[j].id;
          }
        }
        // Create the event
        let tmp: CalendarEvent = {
          start: addMinutes(startTime, currentDiff),
          end: addMinutes(startTime, currentDiff + 30),
          title: title,
          color: this.getEventColor(curCap, capacity, addMinutes(startTime, currentDiff)),
          draggable: false,
          cssClass: 'open-time',
          meta: {
            maxCapacity: capacity,
            curCapacity: curCap,
            tooltip: tooltip,
            isMyAppointment: isMyAppointment,
            appointmentId: appointmentId,
            pastTime: pastTime
          }
        }
        // If the event falls into the open timeframe of the outpatient department, add it to the calendar events
        // (This is done to eliminate an appointment being during the closing hours of the outpatient department)
        if (isBefore(addMinutes(startTime, currentDiff + 30), closeTime) || isEqual(addMinutes(startTime, currentDiff + 30), closeTime)) {
          calendarEvents.push(tmp);
        }
      }
    }
    return calendarEvents;
  }

  /**
   * This function returns the current capacity of the outpatient department
   *
   * @param bookedAppointments the booked appointments of the outpatient department
   * @param currentTime the current time
   */
  private getCurrentCapacity(bookedAppointments: AppointmentCalendarDto[], currentTime: Date): number {
    for (let i = 0; i < bookedAppointments.length; i++) {
      if (this.isSameTimeSlot(currentTime, bookedAppointments[i])) {
        return bookedAppointments[i].count;
      }
    }
    return 0;
  }

  /**
   * This function checks if the current time is the same as the booked appointment
   *
   * @param currentTime the current time
   * @param bookedAppointment the booked appointment
   */
  private isSameTimeSlot(currentTime: Date, bookedAppointment: AppointmentCalendarDto): boolean {
    if (getYear(currentTime) == getYear(bookedAppointment.startDate) && getYear(addMinutes(currentTime, 30)) == getYear(bookedAppointment.endDate)) {
      if (getMonth(currentTime) == getMonth(bookedAppointment.startDate) && getMonth(addMinutes(currentTime, 30)) == getMonth(bookedAppointment.endDate)) {
        if (getDate(currentTime) == getDate(bookedAppointment.startDate) && getDate(addMinutes(currentTime, 30)) == getDate(bookedAppointment.endDate)) {
          if (getHours(currentTime) == getHours(bookedAppointment.startDate) && getHours(addMinutes(currentTime, 30)) == getHours(bookedAppointment.endDate)) {
            if (getMinutes(currentTime) == getMinutes(bookedAppointment.startDate) && getMinutes(addMinutes(currentTime, 30)) == getMinutes(bookedAppointment.endDate)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * This function returns the opening hour of the day
   *
   * @param openingHours the opening hours of the outpatient department
   */
  getOpenHourOfDay(openingHours: OpeningHoursDayDto): number {
    let open: string = (openingHours.open + "").split(":")[0];
    return Number(open);
  }

  /**
   * This function returns the opening minute of the day
   *
   * @param openingHours the opening hours of the outpatient department
   */
  getOpenMinuteOfDay(openingHours: OpeningHoursDayDto): number {
    let open: string = (openingHours.open + "").split(":")[1];
    return Number(open);
  }

  /**
   * This function returns the closing hour of the day
   *
   * @param openingHours the opening hours of the outpatient department
   */
  getCloseHourOfDay(openingHours: OpeningHoursDayDto): number {
    let open: string = (openingHours.close + "").split(":")[0];
    return Number(open);
  }

  /**
   * This function returns the closing minute of the day
   *
   * @param openingHours the opening hours of the outpatient department
   */
  getCloseMinuteOfDay(openingHours: OpeningHoursDayDto): number {
    let open: string = (openingHours.close + "").split(":")[1];
    return Number(open);
  }
}
