package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.InpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.InpatientDepartmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.InpatientDepartmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.InpatientDepartment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface InpatientDepartmentService {

    /**
     * Find an inpatient department by its id.
     *
     * @param id the id of the inpatient department to find
     * @return the inpatient department with the given id
     */
    InpatientDepartment findById(Long id);

    /**
     * Find all inpatient departments in the db.
     *
     * @return all inpatient departments in the db
     */
    InpatientDepartmentPageDto findAll(Specification<InpatientDepartment> spec, Pageable pageable);

    /**
     * This function creates a new inpatient department and assigns a new auto-generated id to it.
     *
     * @param toCreate the inpatient department to persist in the db
     * @return the persisted inpatient department
     */
    InpatientDepartment createInpatientDepartment(InpatientDepartmentDtoCreate toCreate);

    /**
     * This function updates an inpatient department in the db.
     *
     * @param toUpdate the inpatient department to update
     * @return the new inpatient department
     */
    InpatientDepartmentDto updateInpatientDepartment(InpatientDepartmentDto toUpdate);

    /**
     * This function deletes an inpatient department from the db.
     *
     * @param id the id of the inpatient department to delete
     * @return the deleted inpatient department
     */
    InpatientDepartmentDto deleteInpatientDepartment(Long id);
}
