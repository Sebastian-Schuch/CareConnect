package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public record OpeningHoursDayDto(
    @NotNull
    LocalTime open,
    @NotNull
    LocalTime close
) {
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return open.format(formatter) + "-" + close.format(formatter);
    }

    public boolean isValid() {
        return open.isBefore(close);
    }
}
