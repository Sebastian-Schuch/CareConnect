package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StationDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.StationMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Station;
import at.ac.tuwien.sepr.groupphase.backend.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
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
@RequestMapping(value = "/api/v1/station")
public class StationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final StationService stationService;
    private final StationMapper stationMapper;

    public StationEndpoint(StationService stationService, StationMapper stationMapper) {
        this.stationService = stationService;
        this.stationMapper = stationMapper;
    }

    /**
     * Create a new station.
     *
     * @param toCreate the data for the station to create
     * @return the created station
     */
    @Secured({"ADMIN"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new station")
    public StationDto create(@Valid @RequestBody StationDtoCreate toCreate) {
        LOGGER.info("POST /api/v1/station");
        Station newStation = this.stationService.createStation(toCreate);
        return stationMapper.stationToDto(newStation);
    }

    /**
     * Get all stations.
     *
     * @return list of stations
     */
    @Secured({"ADMIN", "DOCTOR", "SECRETARY", "PATIENT"})
    @GetMapping
    @Operation(summary = "Get list of stations")
    public List<StationDto> getAllStations() {
        LOGGER.info("GET /api/v1/station");
        return stationMapper.stationToDto(this.stationService.findAll());
    }


    /**
     * Get a station by id.
     *
     * @param id the id of the station
     * @return the station
     */
    @Secured({"ADMIN", "DOCTOR", "SECRETARY", "PATIENT"})
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get a station")
    public StationDto getById(@PathVariable(name = "id") Long id) {
        LOGGER.info("GET /api/v1/station/{id}");
        return stationMapper.stationToDto(this.stationService.findById(id));
    }
}
