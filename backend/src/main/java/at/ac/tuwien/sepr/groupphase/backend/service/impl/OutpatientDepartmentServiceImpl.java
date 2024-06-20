package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CapacityDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentCapacityDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OpeningHoursMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OutpatientDepartmentMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.AppointmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.OutpatientDepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.OpeningHoursService;
import at.ac.tuwien.sepr.groupphase.backend.service.OutpatientDepartmentService;
import at.ac.tuwien.sepr.groupphase.backend.service.util.CapacityCalculator;
import at.ac.tuwien.sepr.groupphase.backend.service.util.DateTimeUtil;
import at.ac.tuwien.sepr.groupphase.backend.service.util.OpeningHoursUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OutpatientDepartmentServiceImpl implements OutpatientDepartmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OutpatientDepartmentRepository outpatientDepartmentRepository;
    private final OutpatientDepartmentMapper outpatientDepartmentMapper;
    private final OpeningHoursMapper openingHoursMapper;
    private final OpeningHoursService openingHoursService;
    private final CapacityCalculator capacityCalculator;

    @Autowired
    private OutpatientDepartmentServiceImpl(
        OutpatientDepartmentRepository outpatientDepartmentRepository,
        OutpatientDepartmentMapper outpatientDepartmentMapper,
        OpeningHoursMapper openingHoursMapper,
        OpeningHoursService openingHoursService, AppointmentRepository appointmentRepository, CapacityCalculator capacityCalculator) {
        this.outpatientDepartmentRepository = outpatientDepartmentRepository;
        this.outpatientDepartmentMapper = outpatientDepartmentMapper;
        this.openingHoursMapper = openingHoursMapper;
        this.openingHoursService = openingHoursService;
        this.capacityCalculator = capacityCalculator;
    }

    @Override
    public OutpatientDepartmentDto createOutpatientDepartment(OutpatientDepartmentDtoCreate outpatientDepartmentDto) throws MethodArgumentNotValidException {
        LOGGER.trace("createOutpatientDepartment()");
        OpeningHours openingHours = openingHoursService.getOpeningHoursEntityFromDtoCreate(outpatientDepartmentDto.openingHours());
        OutpatientDepartment savedOutpatientDepartment = outpatientDepartmentRepository.save(outpatientDepartmentMapper.dtoToEntity(outpatientDepartmentDto, openingHours));

        return outpatientDepartmentMapper.entityToDto(savedOutpatientDepartment, openingHoursMapper.entityToDto(savedOutpatientDepartment.getOpeningHours()));
    }

    @Override
    public List<OutpatientDepartmentDto> getAllOutpatientDepartments() {
        LOGGER.trace("getAllOutpatientDepartments()");
        List<OutpatientDepartment> outpatientDepartments = outpatientDepartmentRepository.findAllByActiveTrue();
        return outpatientDepartments.stream()
            .map(outpatientDepartment -> outpatientDepartmentMapper.entityToDto(outpatientDepartment, openingHoursMapper.entityToDto(outpatientDepartment.getOpeningHours())))
            .collect(Collectors.toList());
    }

    @Override
    public OutpatientDepartmentDto getOutpatientDepartmentById(Long id) throws NotFoundException {
        LOGGER.trace("getOutpatientDepartmentById({})", id);
        OutpatientDepartment outpatientDepartment = outpatientDepartmentRepository.findByIdAndActiveTrue(id);
        if (outpatientDepartment == null) {
            LOGGER.warn("Outpatient department with id {} not found", id);
            throw new NotFoundException("Outpatient department with id " + id + " not found");
        }
        return outpatientDepartmentMapper.entityToDto(outpatientDepartment, openingHoursMapper.entityToDto(outpatientDepartment.getOpeningHours()));
    }

    @Override
    public OutpatientDepartment getOutpatientDepartmentEntityById(Long id) throws NotFoundException {
        LOGGER.trace("getOutpatientDepartmentEntityById({})", id);
        OutpatientDepartment outpatientDepartment = outpatientDepartmentRepository.findByIdAndActiveTrue(id);
        if (outpatientDepartment == null) {
            LOGGER.warn("Outpatient department with id {} not found", id);
            throw new NotFoundException("Outpatient department with id " + id + " not found");
        }
        return outpatientDepartment;
    }

    @Override
    public List<OutpatientDepartmentCapacityDto> getOutpatientDepartmentCapacitiesForDay(Date date) throws NotFoundException {
        LOGGER.trace("getOutpatientDepartmentCapacitiesForDay({})", date);
        List<OutpatientDepartment> departments = outpatientDepartmentRepository.findAll();
        List<OutpatientDepartmentCapacityDto> capacities = new ArrayList<>();

        for (OutpatientDepartment department : departments) {
            int totalSlots = OpeningHoursUtil.calculateTotalSlotsForDay(department.getOpeningHours(), date, department.getCapacity());
            CapacityDto capacity = capacityCalculator.calculateCapacity(department, DateTimeUtil.getStartOfDay(date), DateTimeUtil.getEndOfDay(date), totalSlots);
            capacities.add(
                new OutpatientDepartmentCapacityDto(
                outpatientDepartmentMapper.entityToDto(department, openingHoursMapper.entityToDto(department.getOpeningHours())),
                    capacity
                ));
        }
        return capacities;
    }

    @Override
    public List<OutpatientDepartmentCapacityDto> getOutpatientDepartmentCapacitiesForWeek(Date startDate) throws NotFoundException {
        LOGGER.trace("getOutpatientDepartmentCapacitiesForWeek({})", startDate);
        List<OutpatientDepartment> departments = outpatientDepartmentRepository.findAll();
        List<OutpatientDepartmentCapacityDto> capacities = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, 6);
        Date endDate = calendar.getTime();

        for (OutpatientDepartment department : departments) {
            int totalSlots = OpeningHoursUtil.calculateTotalSlotsForWeek(department.getOpeningHours(), startDate, endDate, department.getCapacity());
            CapacityDto capacity = capacityCalculator.calculateCapacity(department, startDate, endDate, totalSlots);
            capacities.add(
                new OutpatientDepartmentCapacityDto(
                    outpatientDepartmentMapper.entityToDto(department, openingHoursMapper.entityToDto(department.getOpeningHours())),
                    capacity
                ));
        }

        return capacities;
    }

    @Override
    public List<OutpatientDepartmentCapacityDto> getOutpatientDepartmentCapacitiesForMonth(Date date) throws NotFoundException {
        LOGGER.trace("getOutpatientDepartmentCapacitiesForMonth({})", date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        Date endDate = calendar.getTime();
        List<OutpatientDepartment> departments = outpatientDepartmentRepository.findAll();
        List<OutpatientDepartmentCapacityDto> capacities = new ArrayList<>();

        for (OutpatientDepartment department : departments) {
            int totalSlots = OpeningHoursUtil.calculateTotalSlotsForMonth(department.getOpeningHours(), startDate, endDate, department.getCapacity());
            CapacityDto capacity = capacityCalculator.calculateCapacity(department, startDate, endDate, totalSlots);
            capacities.add(
                new OutpatientDepartmentCapacityDto(
                    outpatientDepartmentMapper.entityToDto(department, openingHoursMapper.entityToDto(department.getOpeningHours())),
                    capacity
                ));
        }
        return capacities;
    }




}
