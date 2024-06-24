package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public record AppointmentPageDto(
    List<AppointmentDto> appointments,
    int totalItems
) {
}
