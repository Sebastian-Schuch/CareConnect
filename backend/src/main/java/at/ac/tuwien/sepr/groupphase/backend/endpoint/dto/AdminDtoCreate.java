package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminDtoCreate(
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email is not a valid email address")
    @Size(max = 255, message = "Email cannot be longer than 255 characters")
    String email,

    @NotBlank(message = "Firstname cannot be empty")
    @Size(max = 255, message = "Firstname cannot be longer than 255 characters")
    String firstname,

    @NotBlank(message = "Lastname cannot be empty")
    @Size(max = 255, message = "Lastname cannot be longer than 255 characters")
    String lastname
) {
}
