package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OutpatientDepartmentDtoCreate(
    @NotBlank
    @NotNull
    @Size(max = 255, message = "Name must be less than or equal to 255 characters")
    String name,
    @NotNull
    @Size(max = 1000, message = "Description must be less than or equal to 1000 characters")
    String description,
    @Max(value = 1000, message = "Capacity must be less than or equal to 1000")
    int capacity,

    @NotNull(message = "Opening hours must not be null")
    OpeningHoursDtoCreate openingHours
) {
}