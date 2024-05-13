package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StationDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.Station;

import java.util.List;

public interface StationService {

    /**
     * Find a station by its name.
     *
     * @param name the name of the station to find
     *
     * @return the station with the given name
     */
    Station findByName(String name);

    /**
     * Find a station by its id.
     *
     * @param id the id of the station to find
     *
     * @return the station with the given id
     */
    Station findById(Long id);

    /**
     * Find all stations in the db.
     *
     * @return all stations in the db
     */
    List<Station> findAll();

    /**
     * This function creates a new station and assigns a new auto-generated id to it.
     *
     * @param toCreate the station to persist in the db
     *
     * @return the persisted station
     */
    Station createStation(StationDtoCreate toCreate);

    /**
     * This function counts the number of stations in the db.
     *
     * @param toUpdate the station to update
     *
     * @return the number of stations in the db
     */
    Station updateStation(Station toUpdate);
}
