package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDto;
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
    Allergy createAllergy(AllergyCreateDto toCreate);

    /**
     * This function finds an allergy by its id.
     *
     * @param id the id of the allergy to find
     *
     * @return the allergy with the given id
     */
    Allergy findById(Long id);

    /**
     * This function counts the number of allergies in the db.
     *
     * @return the number of allergies in the db
     */
    int countAllergies();


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
}
