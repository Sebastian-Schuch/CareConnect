package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record DoctorDtoSparse(
    long id,
    String firstname,
    String lastname,
    String email,
    boolean isInitialPassword
) {
}
