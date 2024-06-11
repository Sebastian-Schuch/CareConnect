package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record StayDtoCreate(
    StationDto station,
    Long patientId
) {
}
