package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DataInterfaceDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorWorkingHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.service.ApiKeyService;
import at.ac.tuwien.sepr.groupphase.backend.service.DataInterfaceService;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(DataInterfaceEndpoint.BASE_PATH)
public class DataInterfaceEndpoint {
    public static final String BASE_PATH = "/api/v1/data";

    private final DataInterfaceService dataInterfaceService;
    private final ApiKeyService apiKeyService;
    private final HttpServletRequest request;

    public DataInterfaceEndpoint(DataInterfaceService dataInterfaceService,
                                 ApiKeyService apiKeyService,
                                 HttpServletRequest request) {
        this.dataInterfaceService = dataInterfaceService;
        this.apiKeyService = apiKeyService;
        this.request = request;
    }

    @PermitAll
    @GetMapping("/doctor-working-hours")
    public List<DoctorWorkingHoursDto> getDoctorWorkingHours(@RequestBody DataInterfaceDtoSearch search) {
        String authHeader = request.getHeader("Authorization");
        if (!apiKeyService.checkApiKey(authHeader.substring(6))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid API key");
        }
        return dataInterfaceService.getDoctorWorkingHours(search);
    }

}
