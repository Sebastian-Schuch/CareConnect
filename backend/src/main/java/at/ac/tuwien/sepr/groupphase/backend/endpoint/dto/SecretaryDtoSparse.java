package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record SecretaryDtoSparse(
    long id,
    String firstname,
    String lastname,
    String email,
    boolean isInitialPassword
) {
}
