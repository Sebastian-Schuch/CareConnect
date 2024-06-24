package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyPageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Allergy;
import at.ac.tuwien.sepr.groupphase.backend.entity.Appointment;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class AppointmentMapper {

    private final PatientMapper patientMapper;

    private final OutpatientDepartmentMapper outpatientDepartmentMapper;

    private final OpeningHoursMapper openingHoursMapper;
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public AppointmentMapper(PatientMapper patientMapper, OutpatientDepartmentMapper outpatientDepartmentMapper, OpeningHoursMapper openingHoursMapper) {
        this.patientMapper = patientMapper;
        this.outpatientDepartmentMapper = outpatientDepartmentMapper;
        this.openingHoursMapper = openingHoursMapper;
    }

    public AppointmentDto appointmentEntityToAppointmentDto(Appointment appointment) {
        LOG.trace("appointmentEntityToAppointmentDto({})", appointment);
        return new AppointmentDto(
            appointment.getId(),
            patientMapper.patientToPatientDtoSparse(appointment.getPatient()),
            outpatientDepartmentMapper.entityToDto(appointment.getOutpatientDepartment(), openingHoursMapper.entityToDto(appointment.getOutpatientDepartment().getOpeningHours())),
            appointment.getStartDate(),
            appointment.getEndDate(),
            appointment.getNotes()
        );
    }

    public List<AppointmentDto> appointmentEntitiesToListOfAppointmentDto(List<Appointment> appointments) {
        LOG.trace("appointmentEntitiesToListOfAppointmentDto({})", appointments);
        List<AppointmentDto> appointmentsDto = new ArrayList<>();
        for (Appointment appointment : appointments) {
            appointmentsDto.add(appointmentEntityToAppointmentDto(appointment));
        }
        return appointmentsDto;
    }

    public AppointmentPageDto toAppointmentPageDto(Page<Appointment> appointmentPage) {
        return new AppointmentPageDto(appointmentEntitiesToListOfAppointmentDto(appointmentPage.getContent()), (int) appointmentPage.getTotalElements());
    }

}
