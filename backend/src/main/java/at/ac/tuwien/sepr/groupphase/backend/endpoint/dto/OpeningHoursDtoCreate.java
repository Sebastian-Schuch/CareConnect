package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record OpeningHoursDtoCreate(
    OpeningHoursDayDto monday,
    OpeningHoursDayDto tuesday,
    OpeningHoursDayDto wednesday,
    OpeningHoursDayDto thursday,
    OpeningHoursDayDto friday,
    OpeningHoursDayDto saturday,
    OpeningHoursDayDto sunday
) {
}
