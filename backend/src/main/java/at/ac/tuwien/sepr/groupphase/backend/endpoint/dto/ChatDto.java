package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public record ChatDto(
    Long id,
    String name,
    List<MessageDto> messages
) {
}
