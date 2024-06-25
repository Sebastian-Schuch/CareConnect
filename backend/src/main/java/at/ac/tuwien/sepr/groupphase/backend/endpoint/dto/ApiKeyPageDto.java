package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public record ApiKeyPageDto(
    List<ApiKeyDto> apiKeys,
    long totalElements
) {
}
