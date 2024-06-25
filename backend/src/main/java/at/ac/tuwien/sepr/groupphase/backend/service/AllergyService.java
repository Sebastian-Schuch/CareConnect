package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyPageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Allergy;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

import java.util.List;

public interface AllergyService {
    /**
     * This function creates a new allergy and assigns a new auto-generated id to it.
     *
     * @param toCreate the allergy to persist in the db
     *
     * @return the persisted allergy
     */
    Allergy createAllergy(AllergyDtoCreate toCreate);

    /**
     * This function finds an allergy by its id.
     *
     * @param id the id of the allergy to find
     *
     * @return the allergy with the given id
     */
    Allergy findById(Long id);

    /**
     * This function finds an allergy by its name.
     *
     * @param name the name of the allergy to find
     *
     * @return the allergy with the given name
     */
    Allergy findByName(String name);

    /**
     * This function finds all allergies in the db.
     *
     * @return all allergies in the db
     *
     * @throws NotFoundException if no allergies are found
     */
    List<Allergy> findAll() throws NotFoundException;

    /**
     * Updates an allergy in the db.
     *
     * @param allergy the allergy to update
     *
     * @return the updated allergy
     */
    Allergy updateAllergy(AllergyDto allergy);

    /**
     * Get the specified allergy entity.
     *
     * @param id the id of the allergy requested
     *
     * @return the allergy entity with the id given
     */
    Allergy getEntityById(Long id);

    /**
     * This function searches for allergies by their name.
     *
     * @param name the name of the allergy to search for
     * @param page the page number
     * @param size the size of the page
     *
     * @return the allergies with the given name
     */
    AllergyPageDto searchAllergies(String name, Integer page, Integer size);

    /**
     * This function sets an allergy to inactive.
     *
     * @param id the id of the allergy to set inactive
     *
     * @return the allergy set to inactive
     */
    AllergyDto setAllergyInactive(Long id);

    /**
     * This function gets the count of all allergies.
     *
     * @return the count of all allergies
     */
    int countAllergies();
}
