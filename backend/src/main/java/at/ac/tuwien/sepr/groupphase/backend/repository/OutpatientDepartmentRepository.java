package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutpatientDepartmentRepository extends JpaRepository<OutpatientDepartment, Long> {
    List<OutpatientDepartment> findAllByActiveTrue();

    OutpatientDepartment findByIdAndActiveTrue(Long id);
}
