package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public record MedicationPageDto(
    List<MedicationDto> medications,
    int totalItems
) {
}
