package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record SecretaryDtoCreate(
    String email,
    String firstname,
    String lastname
) {
}

