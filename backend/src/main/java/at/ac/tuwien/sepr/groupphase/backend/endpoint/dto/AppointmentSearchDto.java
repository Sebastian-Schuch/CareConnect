package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public record AppointmentSearchDto(
    Long outpatientDepartmentId,
    @NotNull
    String startDate,
    @NotNull
    String endDate
) {
    @AssertTrue(message = "Start date must be before end date")
    private boolean isValidTimeRange() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(startDate().split("T")[0]).before(sdf.parse(endDate().split("T")[0]));
        } catch (ParseException e) {
            return false;
        }
    }
}
