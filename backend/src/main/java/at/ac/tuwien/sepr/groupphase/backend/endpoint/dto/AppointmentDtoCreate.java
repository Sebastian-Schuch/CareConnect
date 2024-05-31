package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

import static at.ac.tuwien.sepr.groupphase.backend.config.TimeSlotConfig.MIN_APPOINTMENT_LENGTH_IN_MINUTES;

public record AppointmentDtoCreate(
    @NotNull(message = "is required")
    PatientDto patient,

    @NotNull(message = "is required")
    OutpatientDepartmentDto outpatientDepartment,

    @NotNull(message = "cannot be empty")
    @FutureOrPresent(message = "has to be Date after today")
    Date startDate,

    @NotNull(message = "cannot be empty")
    @FutureOrPresent(message = "has to be Date after today")
    Date endDate,

    @NotNull(message = "cannot be empty")
    @Size(max = 1024, message = "cannot be longer than 1024 characters")
    String notes
) {
    @AssertTrue(message = "Appointment startDate must be before endDate")
    public boolean isValidTimeRange() {
        return startDate.before(endDate);
    }


    @AssertTrue(message = "Appointment has to be 30 minutes long")
    public boolean isAtLeastHalfHourLong() {
        long diffInMin = (endDate.getTime() - startDate.getTime()) / 60000;
        return diffInMin >= MIN_APPOINTMENT_LENGTH_IN_MINUTES;
    }

    @AssertTrue(message = "Appointment must be during opening hours")
    public boolean isDuringOpeningHours() {
        OpeningHoursDayDto weekday = outpatientDepartment.openingHours().getWeekdayByDate(startDate);
        if (weekday.open().getHour() > startDate.getHours()) {
            return false;
        }
        if (weekday.close().getHour() < endDate.getHours()) {
            return false;
        }
        if (weekday.open().getHour() == startDate.getHours() && weekday.open().getMinute() > startDate.getMinutes()) {
            return false;
        }
        return weekday.close().getHour() != endDate.getHours() || weekday.close().getMinute() >= endDate.getMinutes();
    }
}
