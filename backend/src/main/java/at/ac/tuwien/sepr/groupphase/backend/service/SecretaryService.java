package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDetailDto;

import java.util.List;

public interface SecretaryService {

    /**
     * Creates a secretary with the data given.
     *
     * @param toCreate the data to create the secretary with
     * @return the created secretary
     */
    SecretaryDetailDto create(SecretaryCreateDto toCreate);

    /**
     * Get the specified secretary.
     *
     * @param id the id of the secretary requested
     * @return the secretary with the id given
     */
    SecretaryDetailDto getById(Long id);

    /**
     * Get all patients from repository.
     *
     * @return the list of all patients
     */
    List<SecretaryDetailDto> getAllSecretaries();

}
