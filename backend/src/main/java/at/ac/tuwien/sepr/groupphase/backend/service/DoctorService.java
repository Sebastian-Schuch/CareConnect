package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;

import java.util.List;

public interface DoctorService {
    /**
     * Creates a doctor with the data given.
     *
     * @param toCreate    the data to create the doctor with
     * @param credentials the credentials of the doctor
     * @return the created doctor
     */
    DoctorDto createDoctor(DoctorCreateDto toCreate, Credential credentials);

    /**
     * Get the specified doctor.
     *
     * @param id the id of the doctor requested
     * @return the doctor with the id given
     */
    DoctorDto getDoctorById(Long id);

    /**
     * Get the specified doctor.
     *
     * @param id the id of the doctor Entity requested
     * @return the doctor with the id given
     */
    Doctor getDoctorEntityById(Long id);


    /**
     * Get all Doctors from the repository.
     *
     * @return a list of all Doctors
     */
    List<DoctorDto> getAllDoctors();

    /**
     * Get the doctor by email.
     *
     * @param email the email of the doctor
     * @return the doctor with the email given
     */
    DoctorDto getDoctorByEmail(String email);

}
