package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.List;

public record TreatmentDto(
    long id,

    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    String treatmentTitle,
    @NotNull
    @PastOrPresent(message = "Date must be in the past or present")
    Date treatmentStart,
    @NotNull
    @PastOrPresent(message = "Date must be in the past or present")
    Date treatmentEnd,

    @NotNull(message = "Patient must not be null")
    PatientDtoSparse patient,

    @NotNull(message = "Outpatient department must not be null")
    OutpatientDepartmentDto outpatientDepartment,

    @Size(max = 1024, message = "Treatment text must be less than or equal to 1024 characters")
    String treatmentText,

    @NotEmpty(message = "Doctors must not be empty")
    @NotNull(message = "Doctors must not be null")
    List<DoctorDtoSparse> doctors,

    List<TreatmentMedicineDto> medicines
) {
}
