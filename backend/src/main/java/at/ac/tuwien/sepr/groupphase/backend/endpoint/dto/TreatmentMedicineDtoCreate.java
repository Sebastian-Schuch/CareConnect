package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record TreatmentMedicineDtoCreate(

    @NotNull(message = "must not be null")
    MedicationDto medication,

    @NotNull(message = "must not be null")
    String unitOfMeasurement,

    @Min(value = 0, message = "must be greater than or equal to 0")
    @NotNull(message = "must not be null")
    long amount,

    @NotNull(message = "must not be null")
    @PastOrPresent(message = "Date must be in the past or present")
    Date medicineAdministrationDate
) {
}
