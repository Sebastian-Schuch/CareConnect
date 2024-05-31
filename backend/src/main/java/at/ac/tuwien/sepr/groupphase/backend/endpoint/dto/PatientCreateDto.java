package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PatientCreateDto(
    @NotBlank(message = "cannot be empty")
    @Size(max = 10, min = 10, message = "must have 10 digits")
    String svnr,

    @NotBlank
    @Email(message = "Please provide a valid email address")
    String email,

    @NotBlank(message = "cannot be empty")
    @Size(max = 255, message = "cannot be longer than 255 characters")
    String firstname,

    @NotBlank(message = "cannot be empty")
    @Size(max = 255, message = "cannot be longer than 255 characters")
    String lastname,

    List<MedicationDto> medicines,

    List<AllergyDto> allergies
) {
}
