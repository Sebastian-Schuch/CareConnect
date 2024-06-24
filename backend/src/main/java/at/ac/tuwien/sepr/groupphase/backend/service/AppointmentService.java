package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentCalendarDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AppointmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;

import java.util.Date;
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
     * @param outpatientDepartmentId the id of the outpatient department
     * @param startDate              the start date of the appointments
     * @param endDate                the end date of the appointments
     * @return the booked appointments
     */
    List<AppointmentCalendarDto> getAllAppointmentsFromStartDateToEndDateWithOutpatientDepartmentId(long outpatientDepartmentId, Date startDate, Date endDate);

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
     * Get all appointments of the patient.
     *
     * @return the list of all appointments of the patient.
     */
    AppointmentPageDto getAppointmentsByPatient(Long patientId, Long outpatientDepartmentId, Date startDate, Date endDate, int page, int size);


    /**
     * Get all appointments.
     *
     * @return the list of all appointments.
     */
    AppointmentPageDto getAllFilteredAppointments(Long patientId, Long outpatientDepartmentId, Date startDate, Date endDate, int page, int size);

    /**
     * delete the specified appointment.
     *
     * @param id the id of the appointment to delete
     */
    void delete(long id);
}
