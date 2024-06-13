package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Allergy;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllergyRepository extends CrudRepository<Allergy, Long> {
    /**
     * Find an allergy by its name.
     *
     * @param name the name of the allergy to find
     * @return the allergy with the given name
     */
    Allergy findByName(String name);

    /**
     * Find all allergies in the db.
     *
     * @return all allergies in the db
     */
    List<Allergy> findAll();

    @Transactional
    @Query(value = "SELECT * FROM Allergy WHERE ID= ?1", nativeQuery = true)
    Allergy findAllergyById(Long id);
}
