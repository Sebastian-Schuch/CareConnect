package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MedicationDtoCreate(
    @NotBlank(message = "Name cannot be empty")
    @Size(max = 255, message = "Name cannot be longer than 255 characters")
    String name,

    @NotBlank(message = "Name cannot be empty")
    String unitOfMeasurement
) {
}
