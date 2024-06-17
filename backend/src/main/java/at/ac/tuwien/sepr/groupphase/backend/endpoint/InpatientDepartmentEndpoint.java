package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.InpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.InpatientDepartmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.InpatientDepartmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.InpatientDepartmentMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.InpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.service.InpatientDepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import static at.ac.tuwien.sepr.groupphase.backend.endpoint.InpatientDepartmentEndpoint.BASE_PATH;

@RestController
@RequestMapping(value = BASE_PATH)
public class InpatientDepartmentEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static final String BASE_PATH = "/api/v1/inpatient-departments";

    private final InpatientDepartmentService inpatientDepartmentService;
    private final InpatientDepartmentMapper inpatientDepartmentMapper;

    public InpatientDepartmentEndpoint(InpatientDepartmentService inpatientDepartmentService, InpatientDepartmentMapper inpatientDepartmentMapper) {
        this.inpatientDepartmentService = inpatientDepartmentService;
        this.inpatientDepartmentMapper = inpatientDepartmentMapper;
    }

    /**
     * Create a new inpatient department.
     *
     * @param toCreate the data for the inpatient department to create
     * @return the created inpatient department
     */
    @Secured({"ADMIN"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new inpatient department")
    public InpatientDepartmentDto create(@Valid @RequestBody InpatientDepartmentDtoCreate toCreate) {
        LOGGER.info("POST " + BASE_PATH);
        InpatientDepartment newInpatientDepartment = this.inpatientDepartmentService.createInpatientDepartment(toCreate);
        return inpatientDepartmentMapper.inpatientDepartmentToDto(newInpatientDepartment);
    }

    /**
     * Get all inpatient departments.
     *
     * @return list of inpatient departments
     */
    @Secured({"ADMIN", "DOCTOR", "SECRETARY", "PATIENT"})
    @GetMapping
    @Operation(summary = "Get list of inpatient departments")
    public InpatientDepartmentPageDto getAllInpatientDepartments(
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @RequestParam(name = "size", defaultValue = "20") Integer size,
        @RequestParam(name = "searchTerm", defaultValue = "") String searchTerm
    ) {
        LOGGER.info("GET " + BASE_PATH);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString("ASC"), "name");
        Specification<InpatientDepartment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(root.get("name"), "%" + searchTerm + "%"));
            predicates.add(cb.equal(root.get("active"), true));  // Check for active field
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return this.inpatientDepartmentService.findAll(spec, pageable);
    }


    /**
     * Get an inpatient department by id.
     *
     * @param id the id of the inpatient department
     * @return the inpatient department
     */
    @Secured({"ADMIN", "DOCTOR", "SECRETARY", "PATIENT"})
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get an inpatient department")
    public InpatientDepartmentDto getById(@PathVariable(name = "id") Long id) {
        LOGGER.info("GET " + BASE_PATH + "/{id}");
        return inpatientDepartmentMapper.inpatientDepartmentToDto(this.inpatientDepartmentService.findById(id));
    }


    /**
     * Delete an inpatient department by id.
     *
     * @param id the id of the inpatient department
     */
    @Secured({"ADMIN"})
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete an inpatient department")
    public InpatientDepartmentDto delete(@PathVariable(name = "id") Long id) {
        LOGGER.info("DELETE " + BASE_PATH + "/{id}");
        return this.inpatientDepartmentService.deleteInpatientDepartment(id);
    }

    @Secured({"ADMIN"})
    @PostMapping(value = "/{id}")
    @Operation(summary = "Edit an inpatient department")
    public InpatientDepartmentDto edit(@PathVariable(name = "id") Long id, @Valid @RequestBody InpatientDepartmentDto toUpdate) {
        LOGGER.info("POST " + BASE_PATH + "/{id}");
        return this.inpatientDepartmentService.updateInpatientDepartment(toUpdate);
    }
}
