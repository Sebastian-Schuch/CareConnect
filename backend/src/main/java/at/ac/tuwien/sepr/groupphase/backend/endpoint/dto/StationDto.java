package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record StationDto(
    Long id,
    String name,
    int capacity
) {
}
