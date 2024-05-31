package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentCalendarDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;

import java.util.List;

public interface AppointmentService {

    /**
     * Creates an appointment with the given data.
     *
     * @param toCreate the data to create the appointment with
     * @return the created appointment
     */
    AppointmentDto create(AppointmentDtoCreate toCreate) throws ConflictException;

    /**
     * Get the specified appointment.
     *
     * @param id the id of the appointment requested
     * @return the appointment with the id given
     */
    List<AppointmentDto> getAllAppointmentsFromPatientWithPatientId(long id);

    /**
     * Get the specified appointments.
     *
     * @param searchParams the search parameters
     * @return the booked appointments
     */
    List<AppointmentCalendarDto> getAllAppointmentsFromStartDateToEndDateWithOutpatientDepartmentId(AppointmentSearchDto searchParams);

    /**
     * Get the specified appointment.
     *
     * @param id the id of the appointment requested
     * @return the appointment with the id given
     */
    AppointmentDto getAppointmentById(long id);

    /**
     * Get all appointments.
     *
     * @return the list of all appointments.
     */
    List<AppointmentDto> getAllAppointments();

    /**
     * delete the specified appointment.
     *
     * @param id the id of the appointment to delete
     */
    void delete(long id);
}
