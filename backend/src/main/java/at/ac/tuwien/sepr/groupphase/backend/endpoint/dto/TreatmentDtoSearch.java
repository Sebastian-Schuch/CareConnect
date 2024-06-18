package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record TreatmentDtoSearch(
    int page,
    int size,
    Long patientId,
    String startDate,
    String endDate,
    String treatmentTitle,
    String medicationName,
    String doctorName,
    String departmentName
) {
}
