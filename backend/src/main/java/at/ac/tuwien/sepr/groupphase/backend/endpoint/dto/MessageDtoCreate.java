package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record MessageDtoCreate(
    String content,
    Long treatmentId
) {
}
