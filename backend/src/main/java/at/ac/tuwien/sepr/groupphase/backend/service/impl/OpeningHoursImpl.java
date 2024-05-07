package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OpeningHoursMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import at.ac.tuwien.sepr.groupphase.backend.repository.OpeningHoursRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.OpeningHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpeningHoursImpl implements OpeningHoursService {

    private final OpeningHoursRepository openingHoursRepository;
    private final OpeningHoursMapper openingHoursMapper;

    @Autowired
    public OpeningHoursImpl(OpeningHoursRepository openingHoursRepository, OpeningHoursMapper openingHoursMapper) {
        this.openingHoursRepository = openingHoursRepository;
        this.openingHoursMapper = openingHoursMapper;
    }

    @Override
    public OpeningHoursDto createOpeningHours(OpeningHoursDtoCreate openingHoursDto) {
        OpeningHours savedOpeningHours = openingHoursRepository.save(openingHoursMapper.dtoToEntity(openingHoursDto));
        return openingHoursMapper.entityToDto(savedOpeningHours);
    }

    @Override
    public OpeningHoursDto getOpeningHoursById(Long id) {
        OpeningHours openingHours = openingHoursRepository.findById(id).orElse(null);
        if (openingHours == null) {
            return null;
        }
        return openingHoursMapper.entityToDto(openingHours);
    }
}
