package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;

import java.util.List;

public interface SecretaryService {

    /**
     * Creates a secretary with the data given.
     *
     * @param toCreate    the data to create the secretary with
     * @param credentials the credential to create the secretary with
     * @return the created secretary
     */
    SecretaryDetailDto create(SecretaryCreateDto toCreate, Credential credentials);

    /**
     * Get the specified secretary.
     *
     * @param id the id of the secretary requested
     * @return the secretary with the id given
     */
    SecretaryDetailDto getById(Long id);


    /**
     * Get all secretaries from repository.
     *
     * @return the list of all secretaries
     */
    List<SecretaryDetailDto> getAllSecretaries();

    /**
     * Check if the used id matches the token given.
     *
     * @param userId the id of the user
     * @return true if the user is from the secretary that is sending the request, false otherwise
     */
    boolean isValidSecretaryRequest(Long userId);
}
