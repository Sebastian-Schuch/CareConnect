package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StationPageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Station;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StationMapper {
    @IterableMapping(qualifiedByName = "station")
    StationDto stationToDto(Station station);

    List<StationDto> stationToDto(List<Station> station);

    Station dtoToStation(StationDto stationDto);

    default StationPageDto toStationPageDto(Page<Station> stationPage) {
        return new StationPageDto(stationToDto(stationPage.getContent()), (int) stationPage.getTotalElements());
    }
}
