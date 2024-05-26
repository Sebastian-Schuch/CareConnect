package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record MessageDto(
    Long id,
    String message,
    String timestamp,
    Long treatmentId,
    Long senderId,
    boolean read
) {
}
