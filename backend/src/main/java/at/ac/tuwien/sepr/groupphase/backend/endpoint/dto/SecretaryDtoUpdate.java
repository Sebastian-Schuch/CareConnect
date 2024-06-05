package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record SecretaryDtoUpdate(
    String firstname,
    String lastname,
    String email,
    boolean isInitialPassword,
    boolean active
) {
}
