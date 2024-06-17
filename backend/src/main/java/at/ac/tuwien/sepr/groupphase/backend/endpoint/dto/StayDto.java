package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record StayDto(
    @NotNull
    Long id,
    @NotNull
    InpatientDepartmentDto inpatientDepartment,
    @NotNull
    Date arrival,
    @NotNull
    Date discharge
) {
    @AssertTrue(message = "discharge must be in the past or present")
    private boolean isValidDischargeTimeNow() {
        return discharge == null || !discharge.after(new Date());
    }

    @AssertTrue(message = "arrival must be in the past or present")
    private boolean isValidArrivalTimeNow() {
        return arrival == null || !arrival.after(new Date());
    }

    @AssertTrue(message = "discharge must be after arrival")
    private boolean isValidDischargeTime() {
        if (arrival == null) {
            return false;
        }
        return discharge == null || !discharge.before(arrival);
    }
}
