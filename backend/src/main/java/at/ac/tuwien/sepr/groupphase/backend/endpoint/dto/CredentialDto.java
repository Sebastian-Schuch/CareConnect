package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record CredentialDto(
    Long id,
    String email,
    String firstname,
    String lastname,
    String password,
    boolean active
) {
}

