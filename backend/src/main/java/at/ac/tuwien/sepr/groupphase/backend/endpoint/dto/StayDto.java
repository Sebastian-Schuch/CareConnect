package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.AssertTrue;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public record StayDto(
    Long id,
    StationDto station,
    LocalDateTime arrival,
    LocalDateTime discharge
) {
    @AssertTrue(message = "discharge must be in the past or present")
    private boolean isValidDischargeTimeNow() {
        if (discharge != null && discharge.isAfter(LocalDateTime.now(ZoneOffset.UTC))) {
            return false;
        }
        return true;
    }

    @AssertTrue(message = "arrival must be in the past or present")
    private boolean isValidArrivalTimeNow() {
        if (arrival != null && arrival.isAfter(LocalDateTime.now(ZoneOffset.UTC))) {
            return false;
        }
        return true;
    }

    @AssertTrue(message = "discharge must be after arrival")
    private boolean isValidDischargeTime() {
        if (discharge != null && discharge.isBefore(arrival)) {
            return false;
        }
        return true;
    }
}
