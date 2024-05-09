package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record OutpatientDepartmentDto(
    Long id,
    String name,
    String description,
    int capacity,
    OpeningHoursDto openingHours
) {
}
