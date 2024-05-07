package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record PatientDto(
    long uid,
    String svnr,
    String lastname,
    String firstname,
    String email,
    String password,
    boolean active
) {
}
