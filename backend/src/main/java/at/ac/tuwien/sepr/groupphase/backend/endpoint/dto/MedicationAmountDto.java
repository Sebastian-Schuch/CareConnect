package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record MedicationAmountDto(
    long id,
    String name,
    long amount,
    String unitOfMeasurement
) {
}
