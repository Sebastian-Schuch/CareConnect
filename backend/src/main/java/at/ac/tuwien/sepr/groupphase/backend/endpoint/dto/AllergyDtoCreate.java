package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AllergyDtoCreate(
    @NotEmpty
    @NotNull
    @Size(min = 1, max = 255, message = "Name is too long")
    String name
) {
}
