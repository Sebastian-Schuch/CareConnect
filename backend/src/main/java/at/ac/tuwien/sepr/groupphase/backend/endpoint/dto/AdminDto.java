package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record AdminDto(
    long id,
    String firstname,
    String lastname,
    String email,
    String password,
    boolean isInitialPassword,
    boolean active
) {
}
