package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record UserDtoSearch(
    String email,
    String firstName,
    String lastName
) {
}
