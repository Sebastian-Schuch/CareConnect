package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CredentialDto(
    Long id,

    @NotBlank
    @Email(message = "Please provide a valid email address")
    String email,

    @NotBlank(message = "cannot be empty")
    @Size(max = 255, message = "cannot be longer than 255 characters")
    String firstname,

    @NotBlank(message = "cannot be empty")
    @Size(max = 255, message = "cannot be longer than 255 characters")
    String lastname,

    @NotBlank(message = "cannot be empty")
    @Size(max = 255, message = "cannot be longer than 255 characters")
    String password,
    boolean isInitialPassword,
    boolean active

) {
}

