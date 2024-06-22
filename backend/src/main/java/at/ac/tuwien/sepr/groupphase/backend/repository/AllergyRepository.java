package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Allergy;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllergyRepository extends CrudRepository<Allergy, Long> {
    /**
     * Find an allergy by its name.
     *
     * @param name the name of the allergy to find
     * @return the allergy with the given name
     */
    Allergy findByName(String name);

    @Transactional
    @Query(value = "SELECT * FROM Allergy WHERE ID= ?1", nativeQuery = true)
    Allergy findAllergyById(Long id);

    /**
     * Find all allergies by the specification.
     *
     * @param specification the specification
     * @param pageable      the pageable
     * @return the allergies as a page
     */
    Page<Allergy> findAll(Specification<Allergy> specification, Pageable pageable);
}
