package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentCapacityDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.service.OutpatientDepartmentService;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/outpatient-departments")
public class OutpatientDepartmentEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OutpatientDepartmentService outpatientDepartmentService;

    public OutpatientDepartmentEndpoint(OutpatientDepartmentService outpatientDepartmentService) {
        this.outpatientDepartmentService = outpatientDepartmentService;
    }

    /**
     * Create a new outpatient department.
     *
     * @param outpatientDepartmentDto the data for the outpatient department to create
     * @return the created outpatient department
     * @throws MethodArgumentNotValidException if the outpatient department is not valid
     */
    @Secured({"ADMIN"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OutpatientDepartmentDto createOutpatientDepartment(@Valid @RequestBody OutpatientDepartmentDtoCreate outpatientDepartmentDto)
        throws MethodArgumentNotValidException {
        LOGGER.info("createOutpatientDepartment(" + outpatientDepartmentDto.toString() + ")");
        return outpatientDepartmentService.createOutpatientDepartment(outpatientDepartmentDto);
    }

    /**
     * Get an outpatient department by its id.
     *
     * @param id the id of the outpatient department
     * @return the outpatient department
     */
    @Secured({"ADMIN", "DOCTOR", "SECRETARY", "PATIENT"})
    @GetMapping({"/{id}"})
    public OutpatientDepartmentDto getOutpatientDepartmentById(@PathVariable("id") Long id) {
        LOGGER.info("getOutpatientDepartmentById(" + id + ")");
        return outpatientDepartmentService.getOutpatientDepartmentById(id);
    }

    /**
     * Get all outpatient departments.
     *
     * @return list of outpatient departments
     */
    @Secured({"ADMIN", "DOCTOR", "SECRETARY", "PATIENT"})
    @GetMapping
    public List<OutpatientDepartmentDto> getAllOutpatientDepartments() {
        LOGGER.info("getAllOutpatientDepartments()");
        return outpatientDepartmentService.getAllOutpatientDepartments();
    }

    @Secured({"SECRETARY", "PATIENT"})
    @GetMapping("/capacities/day")
    public List<OutpatientDepartmentCapacityDto> getOutpatientDepartmentCapacitiesForDay(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
        return outpatientDepartmentService.getOutpatientDepartmentCapacitiesForDay(date);
    }

    @Secured({"SECRETARY", "PATIENT"})
    @GetMapping("/capacities/week")
    public List<OutpatientDepartmentCapacityDto> getOutpatientDepartmentCapacitiesForWeek(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate) {
        return outpatientDepartmentService.getOutpatientDepartmentCapacitiesForWeek(startDate);
    }

    @Secured({"SECRETARY", "PATIENT"})
    @GetMapping("/capacities/month")
    public List<OutpatientDepartmentCapacityDto> getOutpatientDepartmentCapacitiesForMonth(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
        return outpatientDepartmentService.getOutpatientDepartmentCapacitiesForMonth(date);
    }

    /**
     * Get either all or a page of outpatient departments.
     *
     * @return page of outpatient departments including the content of the list and the total number of elements
     */
    @Secured({"ADMIN", "DOCTOR", "SECRETARY", "PATIENT"})
    @GetMapping("page")
    public OutpatientDepartmentPageDto getOutpatientDepartmentPage(
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @RequestParam(name = "size", defaultValue = "20") Integer size,
        @RequestParam(name = "searchTerm", defaultValue = "") String searchTerm
    ) {
        LOGGER.info("getOutpatientDepartmentPage()");
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString("ASC"), "name");
        Specification<OutpatientDepartment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(root.get("name"), "%" + searchTerm + "%"));
            predicates.add(cb.equal(root.get("active"), true));  // Check for active field
            return cb.and(predicates.toArray(new Predicate[0]));

        };
        return outpatientDepartmentService.getOutpatientDepartmentsPage(spec, pageable);
    }

    @Secured({"ADMIN"})
    @PostMapping({"/{id}"})
    public OutpatientDepartmentDto updateOutpatientDepartment(@PathVariable("id") Long id,
                                                              @Valid @RequestBody OutpatientDepartmentDto toUpdate)
        throws MethodArgumentNotValidException {
        LOGGER.info("updateOutpatientDepartment(" + id + ", " + toUpdate.toString() + ")");
        if (!id.equals(toUpdate.id())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Id in path and body do not match");
        }
        return outpatientDepartmentService.updateOutpatientDepartment(id, toUpdate);
    }

    /**
     * Set an outpatient department inactive by its id.
     *
     * @param id the id of the outpatient department
     * @return the inactive outpatient department
     */
    @Secured({"ADMIN"})
    @DeleteMapping({"/{id}"})
    public OutpatientDepartmentDto setOutpatientDepartmentInactiveById(@PathVariable("id") Long id) {
        LOGGER.info("setOutpatientDepartmentInactiveById(" + id + ")");
        return outpatientDepartmentService.setOutpatientDepartmentInactive(id);
    }
}
