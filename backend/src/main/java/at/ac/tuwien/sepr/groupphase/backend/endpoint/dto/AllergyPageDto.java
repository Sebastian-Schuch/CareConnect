package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public record AllergyPageDto(
    List<AllergyDto> allergies,
    int totalItems
) {
}
