package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record ApiKeyDtoFirst(
    long id,
    String description,
    String apiKey,
    String created
) {
}
