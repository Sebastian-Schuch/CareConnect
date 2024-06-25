package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDtoPage;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.StayService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;


@RestController
@RequestMapping(value = StayEndpoint.BASE_PATH)
public class StayEndpoint {
    static final String BASE_PATH = "/api/v1/stays";
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final StayService stayService;

    private final CustomUserDetailService customUserDetailService;

    private final PatientService patientService;

    public StayEndpoint(StayService stayService, CustomUserDetailService customUserDetailService, PatientService patientService) {
        this.stayService = stayService;
        this.customUserDetailService = customUserDetailService;
        this.patientService = patientService;
    }

    @Secured({"SECRETARY"})
    @GetMapping("/current")
    public StayDto getCurrentStay(@RequestParam(name = "id") Long patientId) {
        LOG.info("GET" + BASE_PATH + "/current");
        return stayService.getCurrentStay(patientId);
    }

    @Secured({"SECRETARY", "PATIENT"})
    @GetMapping("/all")
    public StayDtoPage getAllStays(@RequestParam(name = "id") Long patientId, @RequestParam(name = "page") int page, @RequestParam(name = "size") int size) {
        LOG.info("GET" + BASE_PATH + "/all");
        if (this.customUserDetailService.isValidRequestOfRole(Role.SECRETARY) || this.patientService.isOwnRequest(patientId)) {
            return stayService.getAllStays(patientId, page, size);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource.");
        }
    }

    @Secured({"SECRETARY"})
    @PostMapping("/arrival")
    public StayDto createNewStay(@Valid @RequestBody StayDtoCreate stayDto) throws NotFoundException {
        LOG.info("POST" + BASE_PATH + "/arrival");
        return stayService.createNewStay(stayDto);
    }

    @Secured({"SECRETARY"})
    @PutMapping("/discharge")
    public StayDto endCurrentStay(@RequestBody StayDto stayDto) {
        LOG.info("PUT" + BASE_PATH + "/discharge");
        return stayService.endCurrentStay(stayDto.id());
    }

    @Secured({"SECRETARY"})
    @PutMapping("/update")
    public StayDto updateStay(@Valid @RequestBody StayDto stayDto) {
        LOG.info("PUT" + BASE_PATH + "/update");
        return stayService.updateStay(stayDto);
    }

}
