package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * Service for the treatment entity.
 */

public interface TreatmentService {

    /**
     * Creates a new treatment.
     *
     * @param treatmentDtoCreate the treatment to create
     * @return the created
     * @throws MethodArgumentNotValidException if the treatment is not valid
     */
    TreatmentDto createTreatment(TreatmentDtoCreate treatmentDtoCreate) throws MethodArgumentNotValidException;


    /**
     * Gets a treatmentDto by its id.
     *
     * @param id the id of the treatment
     * @return the treatment as a DTO
     * @throws NotFoundException if the treatment with the given ID does not exist
     */
    TreatmentDto getTreatmentById(Long id) throws NotFoundException;

}
