package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Calendar;
import java.util.Date;

public record OpeningHoursDto(
    Long id,
    OpeningHoursDayDto monday,
    OpeningHoursDayDto tuesday,
    OpeningHoursDayDto wednesday,
    OpeningHoursDayDto thursday,
    OpeningHoursDayDto friday,
    OpeningHoursDayDto saturday,
    OpeningHoursDayDto sunday
) {
    public OpeningHoursDayDto getWeekdayByDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Get the day of the week as an integer (1 = Sunday, 2 = Monday, ..., 7 = Saturday)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return switch (dayOfWeek) {
            case 1 -> sunday;
            case 2 -> monday;
            case 3 -> tuesday;
            case 4 -> wednesday;
            case 5 -> thursday;
            case 6 -> friday;
            case 7 -> saturday;
            default -> null;
        };

    }
}
