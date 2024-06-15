package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

public interface OutpatientDepartmentService {

    /**
     * Creates a new outpatient department.
     *
     * @param outpatientDepartmentDto the outpatient department to create
     * @return the created outpatient department
     */
    OutpatientDepartmentDto createOutpatientDepartment(OutpatientDepartmentDtoCreate outpatientDepartmentDto) throws MethodArgumentNotValidException;

    /**
     * Gets all outpatient departments.
     *
     * @return a list of all outpatient departments
     */
    List<OutpatientDepartmentDto> getAllOutpatientDepartments();

    /**
     * Gets an outpatient department by its id.
     *
     * @param id the id of the outpatient department
     * @return the outpatient department
     */
    OutpatientDepartmentDto getOutpatientDepartmentById(Long id) throws NotFoundException;

    /**
     * Gets an outpatient department Entity by its id.
     *
     * @param id the id of the outpatient department
     * @return the outpatient department
     */
    OutpatientDepartment getOutpatientDepartmentEntityById(Long id) throws NotFoundException;

    /**
     * Gets a page of outpatient departments.
     *
     * @param spec     the specification to filter the outpatient departments
     * @param pageable the page to get
     * @return a page of outpatient departments
     */
    OutpatientDepartmentPageDto getOutpatientDepartmentsPage(Specification<OutpatientDepartment> spec, Pageable pageable);
}
