package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record MedicationDto(
    long id,
    String name,
    boolean active,
    String unitOfMeasurement
) {
}
