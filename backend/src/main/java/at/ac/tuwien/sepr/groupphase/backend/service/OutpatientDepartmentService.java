package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentCapacityDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Date;
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
     * Gets all outpatient department capacities for a specific day.
     *
     * @param date the date for which to get the capacities
     * @return a list of all outpatient department capacities for the given day
     */
    List<OutpatientDepartmentCapacityDto> getOutpatientDepartmentCapacitiesForDay(Date date) throws NotFoundException;

    /**
     * Gets all outpatient department capacities for a specific week.
     *
     * @param startDate the start date of the week for which to get the capacities
     * @return a list of all outpatient department capacities for the given week
     */
    List<OutpatientDepartmentCapacityDto> getOutpatientDepartmentCapacitiesForWeek(Date startDate) throws NotFoundException;

    /**
     * Gets all outpatient department capacities for a specific month.
     *
     * @param date the date for which to get the capacities
     * @return a list of all outpatient department capacities for the given month
     */
    List<OutpatientDepartmentCapacityDto> getOutpatientDepartmentCapacitiesForMonth(Date date) throws NotFoundException;



}
