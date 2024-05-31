package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;

import java.util.List;

public interface MedicationService {

    /**
     * Creates medication with the given data.
     *
     * @param toCreate the data to create the medication with
     * @return the created medication
     */
    MedicationDto create(MedicationDtoCreate toCreate);

    /**
     * Get the specified medication.
     *
     * @param id the id of the medication requested
     * @return the medication with the id given
     */
    MedicationDto getById(Long id);

    /**
     * Get the specified medication entity.
     *
     * @param id the id of the medication requested
     * @return the medication entity with the id given
     */
    Medication getEntityById(Long id);


    /**
     * Get all medications from repository.
     *
     * @return the list of all medications
     */
    List<MedicationDto> getAllMedications();
}
