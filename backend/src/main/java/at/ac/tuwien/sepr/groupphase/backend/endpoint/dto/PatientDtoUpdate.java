package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PatientDtoUpdate(
    @NotBlank(message = "Social security number cannot be empty")
    @Size(max = 10, min = 10, message = "Social security number must have 10 digits")
    String svnr,

    List<MedicationDto> medicines,
    List<AllergyDto> allergies,

    @NotBlank(message = "Firstname cannot be empty")
    @Size(max = 255, message = "Firstname cannot be longer than 255 characters")
    String firstname,

    @NotBlank(message = "Lastname cannot be empty")
    @Size(max = 255, message = "Lastname cannot be longer than 255 characters")
    String lastname,

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email is not a valid email address")
    @Size(max = 255, message = "Email cannot be longer than 255 characters")
    String email,

    boolean isInitialPassword,
    boolean active
) {
}
