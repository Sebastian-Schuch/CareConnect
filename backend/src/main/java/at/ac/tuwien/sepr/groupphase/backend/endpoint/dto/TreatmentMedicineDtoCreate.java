package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.util.Date;

public record TreatmentMedicineDtoCreate(

    @NotNull(message = "Medication must not be null")
    MedicationDto medication,

    @Min(value = 0, message = "Amount must be greater than or equal to 0")
    @NotNull(message = "Amount must not be null")
    long amount,

    @NotNull(message = "Administration date must not be null")
    @PastOrPresent(message = "Administration date must be in the past or present")
    Date medicineAdministrationDate
) {
}
