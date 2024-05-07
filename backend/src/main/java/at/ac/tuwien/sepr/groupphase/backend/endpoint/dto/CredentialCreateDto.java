package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CredentialCreateDto(
    @NotBlank(message = "First name cannot be empty")
    @NotNull(message = "First name is required")
    @Max(value = 255, message = "First name cannot be longer than 255 characters")
    String email,

    @NotBlank
    @NotNull
    @Email(message = "Please provide a valid email address")
    @Pattern(regexp = "[^@]+@[^@]+\\.[^@.]+", message = "Please provide a valid email address")
    String firstname,

    @NotBlank(message = "Last name cannot be empty")
    @NotNull(message = "Last name is required")
    @Max(value = 255, message = "Last name cannot be longer than 255 characters")
    String lastname
) {
}
