package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationPageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class MedicationMapper {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Converts the medication entity to the MedicationDto.
     *
     * @param medication the medication to convert
     * @return the MedicationDto
     */
    public MedicationDto medicationEntityToMedicationDto(Medication medication) {
        LOG.trace("medicationEntityToMedicationDto({})", medication);
        return new MedicationDto(medication.getId(), medication.getName(), medication.getActive(), medication.getUnitOfMeasurement());
    }

    public Medication medicationDtoCreateToMedicationEntity(MedicationDtoCreate medicationDto) {
        LOG.trace("medicationDtoCreateToMedicationEntity({})", medicationDto);
        Medication medication = new Medication();
        medication.setName(medicationDto.name());
        medication.setActive(true);
        medication.setUnitOfMeasurement(medicationDto.unitOfMeasurement());
        return medication;
    }

    /**
     * Converts the medication entity list into a MedicationDto list.
     *
     * @param medications the medications to convert
     * @return a list of MedicationDto
     */
    public List<MedicationDto> medicationEntitiesToListOfMedicationDto(List<Medication> medications) {
        LOG.trace("medicationEntitiesToListOfMedicationDto({})", medications);
        List<MedicationDto> medicationsDto = new ArrayList<>();
        if (medications == null) {
            return medicationsDto;
        }
        for (Medication medication : medications) {
            medicationsDto.add(medicationEntityToMedicationDto(medication));
        }
        return medicationsDto;
    }

    public MedicationPageDto toMedicationPageDto(Page<Medication> medicationPage) {
        LOG.trace("toMedicationPageDto({})", medicationPage);
        return new MedicationPageDto(
            medicationEntitiesToListOfMedicationDto(medicationPage.getContent()),
            (int) medicationPage.getTotalElements()
        );
    }
}
