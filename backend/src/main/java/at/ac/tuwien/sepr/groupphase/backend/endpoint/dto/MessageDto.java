package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;

public record MessageDto(
    Long id,
    String content,
    LocalDateTime timestamp,
    Long treatmentId,
    String displayname,
    String senderEmail,
    boolean read
) {
}
