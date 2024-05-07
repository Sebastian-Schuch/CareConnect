package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalTime;

public record OpeningHoursDto(
    Long id,
    LocalTime mondayStart,
    LocalTime mondayEnd,
    LocalTime tuesdayStart,
    LocalTime tuesdayEnd,
    LocalTime wednesdayStart,
    LocalTime wednesdayEnd,
    LocalTime thursdayStart,
    LocalTime thursdayEnd,
    LocalTime fridayStart,
    LocalTime fridayEnd,
    LocalTime saturdayStart,
    LocalTime saturdayEnd,
    LocalTime sundayStart,
    LocalTime sundayEnd
) {
}
