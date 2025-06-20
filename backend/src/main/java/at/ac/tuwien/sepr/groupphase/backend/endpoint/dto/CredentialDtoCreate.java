package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CredentialDtoCreate(
    @NotBlank
    @Email(message = "Please provide a valid email address")
    String email,

    @NotBlank(message = "Firstname cannot be empty")
    @Size(max = 255, message = "Firstname cannot be longer than 255 characters")
    String firstname,

    @NotBlank(message = "Lastname cannot be empty")
    @Size(max = 255, message = "Lastname cannot be longer than 255 characters")
    String lastname
) {
}

