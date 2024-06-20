package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentCalendarDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.AppointmentMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Appointment;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.AppointmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.AppointmentService;
import at.ac.tuwien.sepr.groupphase.backend.service.OutpatientDepartmentService;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AppointmentRepository appointmentRepository;

    private final PatientService patientService;

    private final OutpatientDepartmentService outpatientDepartmentService;

    private final AppointmentMapper appointmentMapper;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, AppointmentMapper appointmentMapper, PatientServiceImpl patientService,
                                  OutpatientDepartmentServiceImpl outpatientDepartmentService) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.patientService = patientService;
        this.outpatientDepartmentService = outpatientDepartmentService;
    }

    @Override
    public AppointmentDto create(AppointmentDtoCreate toCreate) throws ConflictException {
        LOG.trace("create{}", toCreate);
        int currentAppointmentCount = appointmentRepository.getCountFromAllAppointmentsOnOutpatientDepartmentDuringSpecificTime(toCreate.outpatientDepartment().id(), toCreate.startDate());
        if (currentAppointmentCount >= toCreate.outpatientDepartment().capacity()) {
            LOG.warn("Appointment cannot be created. Slot is already full.");
            throw new ConflictException("Appointment cannot be created. The Timeslot is already full.");
        } else {
            if (appointmentRepository.getAllAppointmentsMatchingPatientIdOutpatientDepartmentStartDateAndEndDate(toCreate.patient().id(), toCreate.outpatientDepartment().id(), toCreate.startDate(), toCreate.endDate()) > 0) {
                LOG.warn("Appointment cannot be created. Patient already has an appointment at this time on this outpatient department.");
                throw new ConflictException("Appointment cannot be created. Patient already has an appointment at this time on this outpatient department.");
            }
            Appointment appointment = new Appointment();
            appointment.setPatient(patientService.getPatientEntityById(toCreate.patient().id()));
            appointment.setOutpatientDepartment(outpatientDepartmentService.getOutpatientDepartmentEntityById(toCreate.outpatientDepartment().id()));
            appointment.setStartDate(toCreate.startDate());
            appointment.setEndDate(toCreate.endDate());
            appointment.setNotes(toCreate.notes());
            return appointmentMapper.appointmentEntityToAppointmentDto(appointmentRepository.save(appointment));
        }
    }

    @Transactional
    @Override
    public void delete(long id) {
        if (!appointmentRepository.existsById(id)) {
            LOG.warn("Appointment {} not found", id);
            throw new NotFoundException("Appointment not found");
        }
        appointmentRepository.deleteById(id);
    }

    @Override
    public List<AppointmentDto> getAllAppointmentsFromPatientWithPatientId(long id) {
        LOG.trace("getAllAppointmentsFromPatientWithPatientId({})", id);
        return appointmentMapper.appointmentEntitiesToListOfAppointmentDto(appointmentRepository.getAllAppointmentsFromPatientWithPatientId(id));
    }

    @Override
    public List<AppointmentDto> getAllAppointments() {
        LOG.trace("getAllAppointments");
        return appointmentMapper.appointmentEntitiesToListOfAppointmentDto(appointmentRepository.getAllAppointments());
    }

    @Override
    public List<AppointmentCalendarDto> getAllAppointmentsFromStartDateToEndDateWithOutpatientDepartmentId(long outpatientDepartmentId, Date startDate, Date endDate) {
        LOG.trace("getAllAppointmentsFromOutpatientDepartmentWithOutpatientDepartmentId({},{},{})", outpatientDepartmentId, startDate, endDate);
        List<Appointment> appointments = appointmentRepository.getAllAppointmentsFromStartDateToEndDateWithOutpatientDepartmentId(outpatientDepartmentId, startDate, endDate);
        return this.appointmentEntitiesToListOfAppointmentCalendarDto(appointments, outpatientDepartmentId);
    }

    @Override
    public AppointmentDto getAppointmentById(long id) {
        var appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            LOG.warn("Appointment {} not found", id);
            throw new NotFoundException("Appointment not found");
        }
        return appointmentMapper.appointmentEntityToAppointmentDto(appointment);
    }

    /**
     * Converts a list of appointment Entities to a list of AppointmentCalendarDto.
     *
     * @param appointments           the list of appointments
     * @param outpatientDepartmentId the id of the outpatient department
     * @return the list of AppointmentCalendarDto
     */
    private List<AppointmentCalendarDto> appointmentEntitiesToListOfAppointmentCalendarDto(List<Appointment> appointments, Long outpatientDepartmentId) {
        LOG.trace("appointmentEntitiesToListOfAppointmentCalendarDto({},{})", appointments, outpatientDepartmentId);
        List<AppointmentCalendarDto> appointmentCalendarDtos = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointmentCalendarDtos.isEmpty()) {
                appointmentCalendarDtos.add(new AppointmentCalendarDto(outpatientDepartmentId, appointment.getStartDate(), appointment.getEndDate(), 1));
            } else {
                AppointmentCalendarDto appointmentCalendarDto = this.getAppointmentInListOfAppointmentCalendarDto(appointment, appointmentCalendarDtos);
                if (appointmentCalendarDto == null) {
                    appointmentCalendarDtos.add(new AppointmentCalendarDto(outpatientDepartmentId, appointment.getStartDate(), appointment.getEndDate(), 1));
                } else {
                    appointmentCalendarDto.increaseCount();
                }
            }
        }
        return appointmentCalendarDtos;
    }

    /**
     * Get the appointment in the list of AppointmentCalendarDto.
     *
     * @param appointment             the appointment to check
     * @param appointmentCalendarDtos the list of AppointmentCalendarDto
     * @return the AppointmentCalendarDto if the appointment is in the list, null otherwise
     */
    private AppointmentCalendarDto getAppointmentInListOfAppointmentCalendarDto(Appointment appointment, List<AppointmentCalendarDto> appointmentCalendarDtos) {
        LOG.trace("getAppointmentInListOfAppointmentCalendarDto({},{})", appointment, appointmentCalendarDtos);
        for (AppointmentCalendarDto appointmentCalendarDto : appointmentCalendarDtos) {
            if (appointmentCalendarDto.getStartDate().equals(appointment.getStartDate()) && appointmentCalendarDto.getEndDate().equals(appointment.getEndDate())) {
                return appointmentCalendarDto;
            }
        }
        return null;
    }


}
