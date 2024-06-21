package at.ac.tuwien.sepr.groupphase.backend.service.util;

import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;


public class DateTimeUtil {

    /**
     * Get the start of the day for a specific date.
     *
     * @param date the date
     * @return the start of the day
     */
    public static Date getStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Get the end of the day for a specific date.
     *
     * @param date the date
     * @return the end of the day
     */
    public static Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}
