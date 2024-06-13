package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.AssertTrue;

import java.util.Date;

public record StayDto(
    Long id,
    StationDto station,
    Date arrival,
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
        return discharge == null || !discharge.before(arrival);
    }
}
