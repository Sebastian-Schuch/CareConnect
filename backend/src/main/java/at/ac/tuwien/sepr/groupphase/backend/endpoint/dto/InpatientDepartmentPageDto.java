package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public record InpatientDepartmentPageDto(
    List<InpatientDepartmentDto> inpatientDepartments,
    int totalItems
) {
}
