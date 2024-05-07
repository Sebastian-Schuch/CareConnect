package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OpeningHoursDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OpeningHoursMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.OpeningHoursRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.OpeningHoursService;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.OpeningHoursValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.invoke.MethodHandles;

@Service
public class OpeningHoursImpl implements OpeningHoursService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final OpeningHoursRepository openingHoursRepository;
    private final OpeningHoursMapper openingHoursMapper;

    private final OpeningHoursValidator openingHoursValidator;

    @Autowired
    public OpeningHoursImpl(
        OpeningHoursRepository openingHoursRepository,
        OpeningHoursMapper openingHoursMapper,
        OpeningHoursValidator openingHoursValidator) {
        this.openingHoursRepository = openingHoursRepository;
        this.openingHoursMapper = openingHoursMapper;
        this.openingHoursValidator = openingHoursValidator;
    }

    @Override
    public OpeningHoursDto createOpeningHours(OpeningHoursDtoCreate openingHoursDto) {
        LOGGER.trace("createOpeningHours()");
        OpeningHours savedOpeningHours = openingHoursRepository.save(openingHoursMapper.dtoToEntity(openingHoursDto));
        return openingHoursMapper.entityToDto(savedOpeningHours);
    }

    @Override
    public OpeningHoursDto getOpeningHoursById(Long id) throws NotFoundException {
        LOGGER.trace("getOpeningHoursById()");
        OpeningHours openingHours = openingHoursRepository.findById(id).orElse(null);
        if (openingHours == null) {
            LOGGER.warn("OpeningHours not found for id: " + id);
            throw new NotFoundException("OpeningHours not found for id: " + id);
        }
        return openingHoursMapper.entityToDto(openingHours);
    }

    public OpeningHours getOpeningHoursEntityFromDto(OpeningHoursDtoCreate openingHoursDto) throws MethodArgumentNotValidException {
        LOGGER.trace("getOpeningHoursEntityFromDto()");
        openingHoursValidator.validateOpeningHours(openingHoursDto);
        return openingHoursMapper.dtoToEntity(openingHoursDto);
    }
}
