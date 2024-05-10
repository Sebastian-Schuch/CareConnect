package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Mapper
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
        return new MedicationDto(medication.getId(), medication.getName(), medication.getActive());
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
        for (Medication medication : medications) {
            medicationsDto.add(medicationEntityToMedicationDto(medication));
        }
        return medicationsDto;
    }
}
