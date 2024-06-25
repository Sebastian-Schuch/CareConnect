package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;

public record StayDtoCreate(
    @NotNull
    InpatientDepartmentDto inpatientDepartment,
    @NotNull
    Long patientId
) {
}
