package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.List;

public record TreatmentDtoCreate(
    @Size(min = 1, max = 255)
    String treatmentTitle,
    @NotNull
    @PastOrPresent(message = "Date must be in the past or present")
    Date treatmentStart,
    @NotNull
    @PastOrPresent(message = "Date must be in the past or present")
    Date treatmentEnd,
    @NotNull(message = "patient must not be null")
    PatientDto patient,

    @NotNull(message = "outpatientDepartment must not be null")
    OutpatientDepartmentDto outpatientDepartment,

    @Size(max = 1024, message = "treatmentText must be less than or equal to 1024 characters")
    String treatmentText,

    @NotNull(message = "doctors must not be null")
    List<DoctorDto> doctors,

    List<TreatmentMedicineDtoCreate> medicines

) {
    @AssertTrue(message = "treatmentStart must be before treatmentEnd")
    private boolean isValidTimeRange() {
        return treatmentStart.before(treatmentEnd);
    }

    @AssertTrue(message = "Medicine must be between treatment start and end date")
    private boolean isValidMedicineTime() {
        for (TreatmentMedicineDtoCreate medicine : medicines) {
            if (medicine.medicineAdministrationDate().before(treatmentStart)
                || medicine.medicineAdministrationDate().after(treatmentEnd)) {
                return false;
            }
        }
        return true;
    }
}
