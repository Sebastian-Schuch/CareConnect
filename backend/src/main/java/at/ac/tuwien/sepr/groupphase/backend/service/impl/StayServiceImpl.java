package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDtoPage;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.StayMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.InpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Stay;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.StayRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.InpatientDepartmentService;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.StayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Date;

@Service
public class StayServiceImpl implements StayService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final StayRepository stayRepository;
    private final StayMapper stayMapper;


    private final PatientService patientService;

    private final InpatientDepartmentService inpatientDepartmentService;


    public StayServiceImpl(StayRepository stayRepository,
                           StayMapper stayMapper,
                           PatientService patientService,
                           InpatientDepartmentService inpatientDepartmentService) {
        this.stayRepository = stayRepository;
        this.stayMapper = stayMapper;
        this.patientService = patientService;
        this.inpatientDepartmentService = inpatientDepartmentService;
    }

    @Override
    public StayDto getCurrentStay(Long patientId) {
        LOG.trace("getCurrentStay({})", patientId);
        try {
            return this.stayMapper.stayEntityToStayDto(stayRepository.findByPatient_PatientIdAndDepartureIsNull(patientId).get(0));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public StayDto createNewStay(StayDtoCreate stayDtoCreate) throws NotFoundException {
        LOG.trace("createNewStay({})", stayDtoCreate);
        Patient patient = patientService.getPatientEntityById(stayDtoCreate.patientId());
        InpatientDepartment inpatientDepartment = this.inpatientDepartmentService.findById(stayDtoCreate.inpatientDepartment().id());
        Stay stay = new Stay().setArrival(new Date()).setInpatientDepartment(inpatientDepartment).setPatient(patient);
        return stayMapper.stayEntityToStayDto(stayRepository.save(stay));
    }

    @Override
    public StayDto endCurrentStay(Long stayId) throws NotFoundException {
        LOG.trace("endCurrentStay({})", stayId);
        Stay stay = stayRepository.findById(stayId).orElseThrow(() -> new NotFoundException("Stay not found"));
        stay.setDeparture(new Date());
        return stayMapper.stayEntityToStayDto(stayRepository.save(stay));
    }

    @Override
    public StayDtoPage getAllStays(Long patientId, int page, int size) {
        LOG.trace("getAllStays({}, {}, {})", patientId, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString("DESC"), "departure");
        Page<Stay> stays = stayRepository.findByPatient_PatientIdAndDepartureIsNotNull(patientId, pageable);
        return stayMapper.stayEntityListToStayDtoList(stays);
    }

    @Override
    public StayDto updateStay(StayDto stayDto) throws NotFoundException {
        LOG.trace("updateStay({})", stayDto);
        Stay stay = stayRepository.findById(stayDto.id()).orElseThrow(() -> new NotFoundException("Stay not found"));

        stay.setArrival(stayDto.arrival());
        stay.setDeparture(stayDto.discharge());

        Stay updatedStay = stayRepository.save(stay);
        return stayMapper.stayEntityToStayDto(updatedStay);
    }
}
