package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutpatientDepartmentRepository extends JpaRepository<OutpatientDepartment, Long> {
}
