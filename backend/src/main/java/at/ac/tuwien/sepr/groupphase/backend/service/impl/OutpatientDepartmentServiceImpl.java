package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OpeningHoursMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OutpatientDepartmentMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.OutpatientDepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.OpeningHoursService;
import at.ac.tuwien.sepr.groupphase.backend.service.OutpatientDepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OutpatientDepartmentServiceImpl implements OutpatientDepartmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OutpatientDepartmentRepository outpatientDepartmentRepository;
    private final OutpatientDepartmentMapper outpatientDepartmentMapper;
    private final OpeningHoursMapper openingHoursMapper;
    private final OpeningHoursService openingHoursService;

    @Autowired
    private OutpatientDepartmentServiceImpl(
        OutpatientDepartmentRepository outpatientDepartmentRepository,
        OutpatientDepartmentMapper outpatientDepartmentMapper,
        OpeningHoursMapper openingHoursMapper,
        OpeningHoursService openingHoursService) {
        this.outpatientDepartmentRepository = outpatientDepartmentRepository;
        this.outpatientDepartmentMapper = outpatientDepartmentMapper;
        this.openingHoursMapper = openingHoursMapper;
        this.openingHoursService = openingHoursService;
    }

    @Override
    public OutpatientDepartmentDto createOutpatientDepartment(OutpatientDepartmentDtoCreate outpatientDepartmentDto) throws MethodArgumentNotValidException {
        LOGGER.trace("createOutpatientDepartment()");
        OpeningHours openingHours = openingHoursService.getOpeningHoursEntityFromDtoCreate(outpatientDepartmentDto.openingHours());
        OutpatientDepartment savedOutpatientDepartment =
            outpatientDepartmentRepository.save(outpatientDepartmentMapper.dtoToEntity(outpatientDepartmentDto, openingHours));

        return outpatientDepartmentMapper.entityToDto(savedOutpatientDepartment, openingHoursMapper.entityToDto(savedOutpatientDepartment.getOpeningHours()));
    }

    @Override
    public List<OutpatientDepartmentDto> getAllOutpatientDepartments() {
        LOGGER.trace("getAllOutpatientDepartments()");
        List<OutpatientDepartment> outpatientDepartments = outpatientDepartmentRepository.findAll();
        return outpatientDepartments.stream()
            .map(outpatientDepartment -> outpatientDepartmentMapper.entityToDto(outpatientDepartment,
                openingHoursMapper.entityToDto(outpatientDepartment.getOpeningHours())))
            .collect(Collectors.toList());
    }

    @Override
    public OutpatientDepartmentDto getOutpatientDepartmentById(Long id) throws NotFoundException {
        LOGGER.trace("getOutpatientDepartmentById({})", id);
        OutpatientDepartment outpatientDepartment = outpatientDepartmentRepository.findById(id).orElse(null);
        if (outpatientDepartment == null) {
            LOGGER.warn("Outpatient department with id {} not found", id);
            throw new NotFoundException("Outpatient department with id " + id + " not found");
        }
        return outpatientDepartmentMapper.entityToDto(outpatientDepartment, openingHoursMapper.entityToDto(outpatientDepartment.getOpeningHours()));
    }

    @Override
    public OutpatientDepartment getOutpatientDepartmentEntityById(Long id) throws NotFoundException {
        LOGGER.trace("getOutpatientDepartmentEntityById({})", id);
        OutpatientDepartment outpatientDepartment = outpatientDepartmentRepository.findById(id).orElse(null);
        if (outpatientDepartment == null) {
            LOGGER.warn("Outpatient department with id {} not found", id);
            throw new NotFoundException("Outpatient department with id " + id + " not found");
        }
        return outpatientDepartment;
    }

    @Override
    public int getOutpatientDepartmentCount() {
        LOGGER.trace("getOutpatientDepartmentCount()");
        return outpatientDepartmentRepository.findAll().size();
    }
}
