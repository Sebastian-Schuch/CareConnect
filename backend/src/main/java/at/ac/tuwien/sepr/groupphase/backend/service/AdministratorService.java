package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdministratorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdministratorDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdministratorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdministratorDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.entity.Administrator;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;

import java.util.List;

public interface AdministratorService {
    /**
     * Creates an administrator with the data given.
     *
     * @param toCreate    the data to create the administrator with
     * @param credentials the credentials of the administrator
     * @return the created administrator
     */
    AdministratorDto createAdministrator(AdministratorDtoCreate toCreate, Credential credentials);

    /**
     * Get the specified administrator.
     *
     * @param id the id of the administrator requested
     * @return the administrator with the id given
     */
    AdministratorDtoSparse getAdministratorById(Long id);

    /**
     * Get the specified administrator.
     *
     * @param id the id of the administrator Entity requested
     * @return the administrator with the id given
     */
    Administrator getAdministratorEntityById(Long id);

    /**
     * Update the administrator with the given id.
     *
     * @param id       the id of the administrator to update
     * @param toUpdate the data to update the administrator with
     * @return the updated administrator
     */
    AdministratorDtoSparse updateAdministrator(Long id, AdministratorDtoUpdate toUpdate);


    /**
     * Get all administrators from the repository.
     *
     * @return a list of all administrators
     */
    List<AdministratorDtoSparse> getAllAdministrators();

    /**
     * Get the administrator by email.
     *
     * @param email the email of the administrator
     * @return the administrator with the email given
     */
    AdministratorDto getAdministratorByEmail(String email);

    /**
     * Find an administrator by the given credential.
     *
     * @param credential the credential to search for
     * @return the administrator with the given credential
     */
    AdministratorDto findAdministratorByCredential(Credential credential);

    /**
     * Search for administrators based on the search criteria.
     *
     * @param search the search criteria
     * @return a list of administrators
     */
    List<AdministratorDtoSparse> searchAdministrators(UserDtoSearch search);

    /**
     * Check if the used id matches the token given.
     *
     * @param userId the id of the user
     * @return true if the userId is from the administrator that is sending the request, false otherwise
     */
    boolean isOwnRequest(Long userId);
}
