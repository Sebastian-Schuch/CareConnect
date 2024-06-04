package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record DoctorDtoUpdate(
    String firstname,
    String lastname,
    String email,
    Boolean active
) {
}
