package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DataInterfaceDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorWorkingHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.service.DataInterfaceService;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(DataInterfaceEndpoint.BASE_PATH)
public class DataInterfaceEndpoint {
    public static final String BASE_PATH = "/api/v1/data";

    private final DataInterfaceService dataInterfaceService;

    public DataInterfaceEndpoint(DataInterfaceService dataInterfaceService) {
        this.dataInterfaceService = dataInterfaceService;
    }

    @PermitAll
    @GetMapping("/doctor-working-hours")
    public List<DoctorWorkingHoursDto> getDoctorWorkingHours(@RequestBody DataInterfaceDtoSearch search) {
        return dataInterfaceService.getDoctorWorkingHours(search);
    }

}
