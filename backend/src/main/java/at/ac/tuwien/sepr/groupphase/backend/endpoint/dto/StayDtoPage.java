package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public record StayDtoPage(
    List<StayDto> content,
    int totalElements
) {
}
