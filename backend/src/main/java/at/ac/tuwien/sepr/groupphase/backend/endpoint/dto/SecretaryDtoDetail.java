package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record SecretaryDtoDetail(
    long uid,
    String lastname,
    String firstname,
    String email,
    String password,
    boolean active
) {
}
