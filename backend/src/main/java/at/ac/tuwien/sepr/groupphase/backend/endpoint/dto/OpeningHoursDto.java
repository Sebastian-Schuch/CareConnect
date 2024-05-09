package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

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
}
