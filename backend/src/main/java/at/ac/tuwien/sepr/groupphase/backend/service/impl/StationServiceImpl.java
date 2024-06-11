package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StationDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StationPageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.StationMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Station;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.StationRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.StationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Service
public class StationServiceImpl implements StationService {
    private final StationMapper stationMapper;
    StationRepository stationRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public StationServiceImpl(StationRepository stationRepository, StationMapper stationMapper) {
        this.stationRepository = stationRepository;
        this.stationMapper = stationMapper;
    }

    @Override
    public Station findByName(String name) {
        return null;
    }

    @Override
    public Station findById(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Id is null");
            }
            Optional<Station> data = stationRepository.findById(id);
            if (data.isPresent()) {
                return data.get();
            } else {
                throw new NotFoundException(String.format("Could not find station with id %s", id));
            }

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Can not search for null id.");
        }
    }

    @Override
    public StationPageDto findAll(Specification<Station> spec, Pageable pageable) {
        Page<Station> stations = stationRepository.findAll(spec, pageable);
        StationPageDto stationPageDto = stationMapper.toStationPageDto(stations);
        if (stations.isEmpty()) {
            throw new NotFoundException("No stations found");
        }
        return stationPageDto;
    }

    @Override
    public Station createStation(StationDtoCreate toCreate) {
        Station station = new Station();
        station.setName(toCreate.getName());
        station.setCapacity(toCreate.getCapacity());
        return stationRepository.save(station);
    }

    @Override
    public StationDto updateStation(StationDto toUpdate) {
        LOGGER.trace("updateStation({})", toUpdate);
        Station stationToUpdate = stationRepository.findById(toUpdate.getId()).orElseThrow(() -> new NotFoundException("Station not found"));
        stationToUpdate.setName(toUpdate.getName());
        stationToUpdate.setCapacity(toUpdate.getCapacity());
        return stationMapper.stationToDto(stationRepository.save(stationToUpdate));
    }

    @Override
    public StationDto deleteStation(Long id) {
        LOGGER.trace("deleteStation({})", id);
        Station toDelete = stationRepository.findById(id).orElseThrow(() -> new NotFoundException("Station not found"));
        stationRepository.delete(toDelete);
        return stationMapper.stationToDto(toDelete);
    }
}
