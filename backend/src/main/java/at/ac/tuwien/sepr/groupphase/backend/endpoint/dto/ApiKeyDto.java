package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record ApiKeyDto(
    @NotNull
    long id,
    String description,
    Date created
) {
}
