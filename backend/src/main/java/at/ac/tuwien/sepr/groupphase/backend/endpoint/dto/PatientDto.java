package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public record PatientDto(
    long id,
    String svnr,
    List<MedicationDto> medicines,
    List<AllergyDto> allergies,
    String firstname,
    String lastname,
    String email,
    String password,
    boolean active
) {
}
