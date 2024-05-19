package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.service.OutpatientDepartmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/outpatient-departments")
public class OutpatientDepartmentEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OutpatientDepartmentService outpatientDepartmentService;

    public OutpatientDepartmentEndpoint(OutpatientDepartmentService outpatientDepartmentService) {
        this.outpatientDepartmentService = outpatientDepartmentService;
    }

    @Secured({"ADMIN"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OutpatientDepartmentDto createOutpatientDepartment(@Valid @RequestBody OutpatientDepartmentDtoCreate outpatientDepartmentDto)
        throws MethodArgumentNotValidException {
        LOGGER.info("createOutpatientDepartment(" + outpatientDepartmentDto.toString() + ")");
        return outpatientDepartmentService.createOutpatientDepartment(outpatientDepartmentDto);
    }

    @Secured({"ADMIN", "DOCTOR", "SECRETARY", "NURSE"})
    @GetMapping({"/{id}"})
    public OutpatientDepartmentDto getOutpatientDepartmentById(@PathVariable("id") Long id) {
        LOGGER.info("getOutpatientDepartmentById(" + id + ")");
        return outpatientDepartmentService.getOutpatientDepartmentById(id);
    }

    @Secured({"ADMIN", "DOCTOR", "SECRETARY", "NURSE"})
    @GetMapping
    public List<OutpatientDepartmentDto> getAllOutpatientDepartments() {
        LOGGER.info("getAllOutpatientDepartments()");
        return outpatientDepartmentService.getAllOutpatientDepartments();
    }
}
