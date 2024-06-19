package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record DoctorWorkingHoursDto(
    DoctorDtoSparse doctorDto,
    double hours
) {

}
