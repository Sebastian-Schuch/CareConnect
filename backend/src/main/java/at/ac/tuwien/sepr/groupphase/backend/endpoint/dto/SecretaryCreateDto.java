package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record SecretaryCreateDto(
    String email,
    String firstname,
    String lastname
) {
}

