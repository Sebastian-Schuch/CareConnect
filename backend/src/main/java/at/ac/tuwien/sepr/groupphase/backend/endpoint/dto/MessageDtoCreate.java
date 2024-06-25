package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Size;

public record MessageDtoCreate(
    @Size(max = 1024)
    String content,
    Long treatmentId
) {
}
