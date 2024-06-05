package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Secretary;

import java.util.List;

public interface SecretaryService {

    /**
     * Creates a secretary with the data given.
     *
     * @param toCreate    the data to create the secretary with
     * @param credentials the credential to create the secretary with
     * @return the created secretary
     */
    SecretaryDto create(SecretaryDtoCreate toCreate, Credential credentials);

    /**
     * Get the specified secretary.
     *
     * @param id the id of the secretary requested
     * @return the secretary with the id given
     */
    SecretaryDto getById(Long id);

    /**
     * Get the specified secretary Entity.
     *
     * @param id the id of the secretary requested
     * @return the secretary with the id given
     */
    Secretary getEntityById(Long id);

    /**
     * Update the secretary with the given id.
     *
     * @param id       the id of the secretary to update
     * @param toUpdate the data to update the secretary with
     * @return the updated secretary
     */
    SecretaryDto updateSecretary(Long id, SecretaryDtoUpdate toUpdate);

    /**
     * Search for secretaries based on the search criteria.
     *
     * @param search the search criteria
     * @return a list of secretaries
     */
    List<SecretaryDto> searchSecretaries(UserDtoSearch search);


    /**
     * Get all secretaries from repository.
     *
     * @return the list of all secretaries
     */
    List<SecretaryDto> getAllSecretaries();

    /**
     * Check if the used id matches the token given.
     *
     * @param userId the id of the user
     * @return true if the userId is from the secretary that is sending the request, false otherwise
     */
    boolean isOwnRequest(Long userId);

    /**
     * Find a secretary by the given credential.
     *
     * @param credential the credential to search for
     * @return the secretary with the given credential
     */
    SecretaryDto findSecretaryByCredential(Credential credential);
}
