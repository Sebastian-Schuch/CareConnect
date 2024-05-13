package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StationDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.Station;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.StationRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.StationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StationServiceImpl implements StationService {
    StationRepository stationRepository;

    public StationServiceImpl(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
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
    public List<Station> findAll() {
        List<Station> stations = stationRepository.findAll();
        if (stations.isEmpty()) {
            throw new NotFoundException("No stations found");
        }
        return stations;
    }

    @Override
    public Station createStation(StationDtoCreate toCreate) {
        Station station = new Station();
        station.setName(toCreate.getName());
        station.setCapacity(toCreate.getCapacity());
        return stationRepository.save(station);
    }

    @Override
    public Station updateStation(Station toUpdate) {
        return null;
    }
}
