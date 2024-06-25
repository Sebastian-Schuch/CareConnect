package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Date;

public record AppointmentDto(
    Long id,

    PatientDtoSparse patient,

    OutpatientDepartmentDto outpatientDepartment,

    Date startDate,

    Date endDate,

    String notes
) {
}
