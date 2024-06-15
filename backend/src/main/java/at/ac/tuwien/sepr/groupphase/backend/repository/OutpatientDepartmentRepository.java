package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutpatientDepartmentRepository extends JpaRepository<OutpatientDepartment, Long> {
    /**
     * Returns all outpatient departments of the given page from the db.
     *
     * @param spec     the specification to filter the outpatient departments
     * @param pageable the page to get
     * @return all outpatient departments of the page.
     */
    Page<OutpatientDepartment> findAll(Specification<OutpatientDepartment> spec, Pageable pageable);
}
