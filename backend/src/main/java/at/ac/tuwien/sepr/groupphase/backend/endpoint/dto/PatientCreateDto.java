package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PatientCreateDto(
    @NotBlank(message = "Social security number cannot be empty")
    @Size(max = 10, min = 10, message = "Social security number must have 10 digits")
    String svnr,

    @NotBlank
    @Email(message = "Please provide a valid email address")
    String email,

    @NotBlank(message = "First name cannot be empty")
    @Size(max = 255, message = "First name cannot be longer than 255 characters")
    String firstname,

    @NotBlank(message = "Last name cannot be empty")
    @Size(max = 255, message = "Last name cannot be longer than 255 characters")
    String lastname
) {
    public CredentialCreateDto toCredentialCreateDto() {
        return new CredentialCreateDto(email, firstname, lastname);
    }
}
