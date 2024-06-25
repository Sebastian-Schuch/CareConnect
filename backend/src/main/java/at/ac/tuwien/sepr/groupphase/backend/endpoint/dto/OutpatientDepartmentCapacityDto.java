package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record OutpatientDepartmentCapacityDto(
    OutpatientDepartmentDto outpatientDepartment,
    CapacityDto capacityDto
) {
}
