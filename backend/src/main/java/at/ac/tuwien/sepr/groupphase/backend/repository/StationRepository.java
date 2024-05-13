package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Station;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends CrudRepository<Station, Long> {

    /**
     * Returns all stations in the db,.
     *
     * @return all stations.
     */
    List<Station> findAll();
}
