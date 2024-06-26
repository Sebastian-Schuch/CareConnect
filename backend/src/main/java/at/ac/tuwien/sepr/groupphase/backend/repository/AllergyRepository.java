package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Allergy;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, Long>, JpaSpecificationExecutor<Allergy> {
    /**
     * Find an allergy by its name.
     *
     * @param name the name of the allergy to find
     * @return the allergy with the given name
     */
    Allergy findByNameAndActiveTrue(String name);

    /**
     * Find an allergy by its name.
     *
     * @param name the name of the allergy to find
     * @return the allergy with the given name
     */
    Allergy findByName(String name);

    @Transactional
    @Query(value = "SELECT * FROM Allergy WHERE ID= ?1 AND ACTIVE", nativeQuery = true)
    Allergy findAllergyById(Long id);

    /**
     * Returns all active allergies.
     *
     * @return all active allergies.
     */
    List<Allergy> findByActiveTrue();
}
