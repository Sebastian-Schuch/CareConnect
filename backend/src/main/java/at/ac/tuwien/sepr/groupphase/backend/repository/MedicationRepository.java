package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long>, JpaSpecificationExecutor<Medication> {
    /**
     * Find a medication by its name.
     *
     * @param name the name of the medication to find
     * @return the medication with the given name
     */
    Medication findByName(String name);

    @Transactional
    @Query(value = "SELECT * FROM Medication WHERE ID= ?1 AND ACTIVE = TRUE", nativeQuery = true)
    Medication findMedicationById(Long id);

    List<Medication> findByActiveTrue();

    /**
     * Find all medications by the specification.
     *
     * @param specification the specification
     * @param pageable      the pageable
     * @return the medications as a page
     */
    Page<Medication> findAll(Specification<Medication> specification, Pageable pageable);
}
