package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Date;


public record TreatmentMedicineDto(
    long id,

    MedicationDto medicine,
    String unitOfMeasurement,
    long amount,
    Date medicineAdministrationDate) {
}
