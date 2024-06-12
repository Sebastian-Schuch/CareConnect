package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.List;

public record TreatmentDtoCreate(
    @Size(min = 1, max = 255)
    String treatmentTitle,

    @NotNull(message = "treatment start must not be null")
    @PastOrPresent(message = "Date must be in the past or present")
    Date treatmentStart,

    @NotNull(message = "treatment end must not be null")
    @PastOrPresent(message = "Date must be in the past or present")
    Date treatmentEnd,

    @NotNull(message = "patient must not be null")
    PatientDtoSparse patient,

    @NotNull(message = "outpatientDepartment must not be null")
    OutpatientDepartmentDto outpatientDepartment,

    @Size(max = 1024, message = "Text must be less than or equal to 1024 characters")
    @NotNull(message = "treatmentText must not be null")
    String treatmentText,

    @NotEmpty(message = "doctors must not be empty")
    @NotNull(message = "doctors must not be null")
    List<DoctorDtoSparse> doctors,

    @NotNull(message = "treatmentMedicines must not be null")
    List<TreatmentMedicineDto> medicines

) {
    @AssertTrue(message = "Treatment-start must be before treatment-end")
    private boolean isValidTimeRange() {
        if (treatmentStart == null || treatmentEnd == null) {
            return false;
        }
        return treatmentStart.before(treatmentEnd);
    }

    @AssertTrue(message = "Medicine time of administration must be between treatment start and end date")
    private boolean isValidMedicineTime() {
        if (medicines == null || treatmentStart == null || treatmentEnd == null) {
            return true;
        }
        return medicines.stream()
            .allMatch(medicine -> medicine.medicineAdministrationDate() != null
                && !medicine.medicineAdministrationDate().before(treatmentStart)
                && !medicine.medicineAdministrationDate().after(treatmentEnd));
    }
}
