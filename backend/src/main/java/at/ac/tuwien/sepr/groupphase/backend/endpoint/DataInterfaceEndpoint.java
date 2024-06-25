package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DataInterfaceDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorWorkingHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationAmountDto;
import at.ac.tuwien.sepr.groupphase.backend.service.ApiKeyService;
import at.ac.tuwien.sepr.groupphase.backend.service.DataInterfaceService;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(DataInterfaceEndpoint.BASE_PATH)
public class DataInterfaceEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
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
    public List<DoctorWorkingHoursDto> getDoctorWorkingHours(@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                                                             @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        LOG.info("GET " + BASE_PATH + "/doctor-working-hours");
        checkApiKey();
        DataInterfaceDtoSearch search = new DataInterfaceDtoSearch(startDate, endDate);
        return dataInterfaceService.getDoctorWorkingHours(search);
    }

    @PermitAll
    @GetMapping("/medication-amounts")
    public List<MedicationAmountDto> getMedicationAmounts(@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                                                          @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        LOG.info("GET " + BASE_PATH + "/medication-amounts");
        checkApiKey();
        DataInterfaceDtoSearch search = new DataInterfaceDtoSearch(startDate, endDate);
        return dataInterfaceService.getMedicationAmounts(search);
    }

    private void checkApiKey() {
        LOG.trace("Checking API key");
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !apiKeyService.checkApiKey(authHeader.substring(6))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid or missing API key");
        }
    }

}
