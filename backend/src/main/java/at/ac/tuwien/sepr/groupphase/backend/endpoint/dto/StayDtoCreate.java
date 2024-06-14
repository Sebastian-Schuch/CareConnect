package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record StayDtoCreate(
    InpatientDepartmentDto inpatientDepartment,
    Long patientId
) {
}
