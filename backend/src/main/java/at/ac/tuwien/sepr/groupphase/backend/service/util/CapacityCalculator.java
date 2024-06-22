package at.ac.tuwien.sepr.groupphase.backend.service.util;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CapacityDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Appointment;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CapacityCalculator {
    private final AppointmentRepository appointmentRepository;

    public CapacityCalculator(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Calculates the capacity of an outpatient department for a given time period.
     *
     * @param department the outpatient department
     * @param startDate  the start date of the time period
     * @param endDate    the end date of the time period
     * @param totalSlots the total number of slots available
     * @return the capacity of the outpatient department
     */
    public CapacityDto calculateCapacity(OutpatientDepartment department, Date startDate, Date endDate, int totalSlots) {
        List<Appointment> appointments = appointmentRepository.findByOutpatientDepartmentAndStartDateBetween(department, startDate, endDate);
        int occupied = appointments.size();
        return new CapacityDto(occupied, totalSlots);
    }
}
