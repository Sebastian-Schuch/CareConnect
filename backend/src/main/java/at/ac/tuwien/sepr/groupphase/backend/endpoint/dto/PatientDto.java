package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record PatientDto(
    long id,
    String svnr,
    String firstname,
    String lastname,
    String email,
    String password,
    boolean active
) {
}
