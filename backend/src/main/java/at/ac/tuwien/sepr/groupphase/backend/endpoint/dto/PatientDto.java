package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record PatientDto(
    long uid,
    String svnr,
    String firstname,
    String lastname,
    String email,
    String password,
    boolean active
) {
}
