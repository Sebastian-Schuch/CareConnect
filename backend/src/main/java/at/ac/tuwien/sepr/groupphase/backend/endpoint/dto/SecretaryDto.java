package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record SecretaryDto(
    long id,
    String firstname,
    String lastname,
    String email,
    String password,
    boolean active
) {
}
