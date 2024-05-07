package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record CredentialCreateDto(
    String email,
    String firstname,
    String lastname
) {
}

