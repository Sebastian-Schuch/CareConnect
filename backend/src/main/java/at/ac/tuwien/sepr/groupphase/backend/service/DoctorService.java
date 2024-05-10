package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;

import java.util.List;

public interface DoctorService {
    /**
     * Creates a doctor with the data given.
     *
     * @param toCreate the data to create the doctor with
     * @return the created doctor
     */
    DoctorDto createDoctor(DoctorCreateDto toCreate);

    /**
     * Get the specified doctor.
     *
     * @param id the id of the doctor requested
     * @return the doctor with the id given
     */
    DoctorDto getDoctorById(Long id);

    /**
     * Get all Doctors from the repository.
     *
     * @return a list of all Doctors
     */
    List<DoctorDto> getAllDoctors();

}
