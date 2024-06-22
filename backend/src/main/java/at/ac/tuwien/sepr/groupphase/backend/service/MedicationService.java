package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * Get a page of medications from the repository.
     *
     * @param searchTerm the search term to filter the medications by
     * @param pageable the pageable object to get the page from
     * @return the page of medications
     */
    List<MedicationDto> getMedicationsPage(String searchTerm, Pageable pageable);
}
