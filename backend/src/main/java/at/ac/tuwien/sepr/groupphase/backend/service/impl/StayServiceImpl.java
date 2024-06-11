package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDtoPage;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.StationMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.StayMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Station;
import at.ac.tuwien.sepr.groupphase.backend.entity.Stay;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.StayRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.StayService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class StayServiceImpl implements StayService {

    private final StayRepository stayRepository;
    private final StayMapper stayMapper;

    private final CustomUserDetailService customUserDetailService;

    private final PatientService patientService;

    private final StationMapper stationMapper;


    public StayServiceImpl(StayRepository stayRepository,
                           StayMapper stayMapper,
                           CustomUserDetailService customUserDetailService,
                           PatientService patientService,
                           StationMapper stationMapper) {
        this.stayRepository = stayRepository;
        this.stayMapper = stayMapper;
        this.customUserDetailService = customUserDetailService;
        this.patientService = patientService;
        this.stationMapper = stationMapper;
    }

    @Override
    public StayDto getCurrentStay(Long patientId) {
        try {
            return this.stayMapper.stayEntityToStayDto(stayRepository.findByPatient_PatientIdAndDepartureIsNull(patientId).getFirst());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public StayDto createNewStay(StayDtoCreate stayDtoCreate) {
        Patient patient = patientService.getPatientEntityById(stayDtoCreate.patientId());
        Station station = stationMapper.dtoToStation(stayDtoCreate.station());
        Stay stay = new Stay().setArrival(LocalDateTime.now(ZoneOffset.UTC)).setStation(station).setPatient(patient);
        return stayMapper.stayEntityToStayDto(stayRepository.save(stay));
    }

    @Override
    public StayDto endCurrentStay(StayDto stayDto) throws NotFoundException {
        Stay stay = stayRepository.findById(stayDto.id()).orElseThrow(() -> new NotFoundException("Stay not found"));
        stay.setDeparture(LocalDateTime.now(ZoneOffset.UTC));
        return stayMapper.stayEntityToStayDto(stayRepository.save(stay));
    }

    @Override
    public StayDtoPage getAllStays(Long patientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString("DESC"), "departure");
        Page<Stay> stays = stayRepository.findByPatient_PatientIdAndDepartureIsNotNull(patientId, pageable);
        return stayMapper.stayEntityListToStayDtoList(stays);
    }

    @Override
    public StayDto updateStay(StayDto stayDto) throws NotFoundException {
        Stay stay = stayRepository.findById(stayDto.id()).orElseThrow(() -> new NotFoundException("Stay not found"));

        stay.setArrival(stayDto.arrival());
        stay.setDeparture(stayDto.discharge());

        Stay updatedStay = stayRepository.save(stay);
        return stayMapper.stayEntityToStayDto(updatedStay);
    }
}
