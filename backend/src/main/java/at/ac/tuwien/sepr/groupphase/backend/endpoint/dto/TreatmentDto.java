package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.List;

public record TreatmentDto(
    long id,

    @Size(min = 1, max = 255)
    String treatmentTitle,
    @NotNull
    @PastOrPresent(message = "Date must be in the past or present")
    Date treatmentStart,
    @NotNull
    @PastOrPresent(message = "Date must be in the past or present")
    Date treatmentEnd,

    @NotNull(message = "patient must not be null")
    PatientDtoSparse patient,

    @NotNull(message = "outpatientDepartment must not be null")
    OutpatientDepartmentDto outpatientDepartment,

    @Size(max = 1024, message = "treatmentText must be less than or equal to 1024 characters")
    String treatmentText,

    @NotEmpty(message = "doctors must not be empty")
    @NotNull(message = "doctors must not be null")
    List<DoctorDtoSparse> doctors,

    List<TreatmentMedicineDto> medicines
) {

}
