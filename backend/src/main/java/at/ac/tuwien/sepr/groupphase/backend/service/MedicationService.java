package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationPageDto;
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

    /**
     * Disable the medication with the given id.
     *
     * @param id the id of the medication to disable
     * @return the disabled medication
     */
    MedicationDto disableById(Long id);

    /**
     * Search for medications with the specified criteria.
     *
     * @param name the name of the medication to search for
     * @param page the page number
     * @param size the size of the page
     * @return the medications found
     */
    MedicationPageDto searchMedications(String name, int page, int size);

    /**
     * Update the medication with the given data.
     *
     * @param medicationDto the data to update the medication with
     * @return the updated medication
     */
    MedicationDto update(MedicationDto medicationDto);

}
