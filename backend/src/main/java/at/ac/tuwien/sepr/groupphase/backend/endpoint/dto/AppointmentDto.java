package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Date;

public record AppointmentDto(
    Long id,

    PatientDto patient,

    OutpatientDepartmentDto outpatientDepartment,

    Date startDate,

    Date endDate,

    String notes
) {
}
