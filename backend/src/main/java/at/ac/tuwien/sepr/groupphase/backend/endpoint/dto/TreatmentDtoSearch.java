package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Date;

public record TreatmentDtoSearch(
    int page,
    int size,
    Long patientId,
    Date startDate,
    Date endDate,
    String treatmentTitle,
    String medicationName,
    String doctorName,
    String patientName,
    String svnr,
    String departmentName
) {
}
