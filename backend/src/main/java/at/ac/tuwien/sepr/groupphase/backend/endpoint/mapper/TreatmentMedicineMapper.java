package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.service.MedicationService;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between TreatmentMedicine and TreatmentMedicineDto objects.
 */

@Component
public class TreatmentMedicineMapper {

    private final MedicationService medicationService;

    public TreatmentMedicineMapper(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    /**
     * Converts a TreatmentMedicineDtoCreate dto to a TreatmentMedicine entity.
     *
     * @param treatmentMedicineDtoCreate the TreatmentMedicineDtoCreate dto to convert
     * @return the converted TreatmentMedicine entity
     */
    public TreatmentMedicine dtoToEntity(TreatmentMedicineDtoCreate treatmentMedicineDtoCreate) {
        Medication med = medicationService.getEntityById(treatmentMedicineDtoCreate.medication().id());

        TreatmentMedicine treatmentMedicine = new TreatmentMedicine();
        treatmentMedicine.setMedicine(med);
        treatmentMedicine.setAmount(treatmentMedicineDtoCreate.amount());
        treatmentMedicine.setUnitOfMeasurement(treatmentMedicineDtoCreate.unitOfMeasurement());
        treatmentMedicine.setTimeOfAdministration(treatmentMedicineDtoCreate.medicineAdministrationDate());
        return treatmentMedicine;
    }

    /**
     * Converts a TreatmentMedicine entity to a TreatmentMedicineDto dto.
     *
     * @param treatmentMedicine the TreatmentMedicine entity to convert
     * @return the converted TreatmentMedicineDto dto
     */
    public TreatmentMedicineDto entityToDto(TreatmentMedicine treatmentMedicine) {
        return new TreatmentMedicineDto(
            treatmentMedicine.getId(),
            new MedicationDto(treatmentMedicine.getMedicine().getId(), treatmentMedicine.getMedicine().getName(), treatmentMedicine.getMedicine().getActive()),
            treatmentMedicine.getUnitOfMeasurement(),
            treatmentMedicine.getAmount(),
            treatmentMedicine.getTimeOfAdministration()
        );
    }
}
