package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public record TreatmentPageDto(
    List<TreatmentDto> inpatientDepartments,
    int totalItems
) {
}
