package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDtoPage;
import at.ac.tuwien.sepr.groupphase.backend.service.StayService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = StayEndpoint.BASE_PATH)
public class StayEndpoint {
    static final String BASE_PATH = "/api/v1/stays";

    private final StayService stayService;

    public StayEndpoint(StayService stayService) {
        this.stayService = stayService;
    }

    @Secured({"SECRETARY"})
    @GetMapping("/current")
    public StayDto getCurrentStay(@RequestParam(name = "id") Long patientId) {
        return stayService.getCurrentStay(patientId);
    }

    @Secured({"SECRETARY"})
    @GetMapping("/all")
    public StayDtoPage getAllStays(@RequestParam(name = "id") Long patientId, @RequestParam(name = "page") int page, @RequestParam(name = "size") int size) {
        return stayService.getAllStays(patientId, page, size);
    }

    @Secured({"SECRETARY"})
    @PostMapping("/arrival")
    public StayDto createNewStay(@RequestBody StayDtoCreate stayDto) {
        return stayService.createNewStay(stayDto);
    }

    //@Secured({"SECRETARY"})
    @PermitAll
    @PutMapping("/discharge")
    public StayDto endCurrentStay(@RequestBody StayDto stayDto) {
        return stayService.endCurrentStay(stayDto);
    }

    @Secured({"SECRETARY"})
    @PutMapping("/update")
    public StayDto updateStay(@Valid @RequestBody StayDto stayDto) {
        return stayService.updateStay(stayDto);
    }

}
