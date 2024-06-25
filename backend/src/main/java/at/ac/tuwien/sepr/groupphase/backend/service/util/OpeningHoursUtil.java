package at.ac.tuwien.sepr.groupphase.backend.service.util;

import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OpeningHoursUtil {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    /**
     * Calculate the number of slots for a specific day.
     *
     * @param hours    the opening hours
     * @param capacity the capacity
     * @return the number of slots
     */
    public static int calculateSlotsForDay(String hours, int capacity) {
        if (hours == null || hours.isEmpty()) {
            return 0;
        }

        String[] parts = hours.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid opening hours format: " + hours);
        }

        try {
            Date startTime = TIME_FORMAT.parse(parts[0]);
            Date endTime = TIME_FORMAT.parse(parts[1]);

            long diffInMillis = endTime.getTime() - startTime.getTime();
            long diffInMinutes = diffInMillis / (1000 * 60);
            int slots = (int) (diffInMinutes / 30);

            return slots * capacity;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid time format in opening hours: " + hours, e);
        }
    }

    /**
     * Get the opening hours for a specific day.
     *
     * @param openingHours the opening hours
     * @param dayOfWeek    the day of the week
     * @return the opening hours for the day
     */
    public static String getOpeningHoursForDay(OpeningHours openingHours, int dayOfWeek) {
        return switch (dayOfWeek) {
            case Calendar.MONDAY -> openingHours.getMonday();
            case Calendar.TUESDAY -> openingHours.getTuesday();
            case Calendar.WEDNESDAY -> openingHours.getWednesday();
            case Calendar.THURSDAY -> openingHours.getThursday();
            case Calendar.FRIDAY -> openingHours.getFriday();
            case Calendar.SATURDAY -> openingHours.getSaturday();
            case Calendar.SUNDAY -> openingHours.getSunday();
            default -> "";
        };
    }

    /**
     * Calculate the total number of slots for a day.
     *
     * @param openingHours the opening hours
     * @param date         the date
     * @param capacity     the capacity
     * @return the total number of slots
     */
    public static int calculateTotalSlotsForDay(OpeningHours openingHours, Date date, int capacity) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String hours = getOpeningHoursForDay(openingHours, dayOfWeek);
        return calculateSlotsForDay(hours, capacity);
    }

    /**
     * Calculate the total number of slots for a week.
     *
     * @param openingHours the opening hours
     * @param startDate    the start date
     * @param endDate      the end date
     * @param capacity     the capacity
     * @return the total number of slots
     */
    public static int calculateTotalSlotsForWeek(OpeningHours openingHours, Date startDate, Date endDate, int capacity) {
        return calcSlotsInTimeFrame(openingHours, startDate, endDate, capacity);
    }

    /**
     * Calculate the total number of slots for a month.
     *
     * @param openingHours the opening hours
     * @param startDate    the start date
     * @param endDate      the end date
     * @param capacity     the capacity
     * @return the total number of slots
     */
    public static int calculateTotalSlotsForMonth(OpeningHours openingHours, Date startDate, Date endDate, int capacity) {
        return calcSlotsInTimeFrame(openingHours, startDate, endDate, capacity);
    }

    /**
     * Calculate the total number of slots in a given time frame.
     *
     * @param openingHours the opening hours
     * @param startDate    the start date
     * @param endDate      the end date
     * @param capacity     the capacity
     * @return the total number of slots
     */
    private static int calcSlotsInTimeFrame(OpeningHours openingHours, Date startDate, Date endDate, int capacity) {
        int totalSlots = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (calendar.getTime().before(endDate)) {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            String hours = getOpeningHoursForDay(openingHours, dayOfWeek);
            totalSlots += calculateSlotsForDay(hours, capacity);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return totalSlots;
    }
}
