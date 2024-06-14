package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.InpatientDepartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InpatientDepartmentRepository extends JpaRepository<InpatientDepartment, Long> {

    /**
     * Returns all inpatient departments in the db.
     *
     * @return all inpatient departments.
     */
    Page<InpatientDepartment> findAll(Specification<InpatientDepartment> specification, Pageable pageable);
}
