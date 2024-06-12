package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StationDtoCreate(
    @NotNull
    @NotEmpty
    @Size(max = 255, message = "Name is too long")
    String name,

    @Min(value = 0, message = "Capacity must be greater than 0")
    int capacity
) {
}
