package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorService {
    /**
     * Creates a doctor with the data given.
     *
     * @param toCreate    the data to create the doctor with
     * @param credentials the credentials of the doctor
     * @return the created doctor
     */
    DoctorDto createDoctor(DoctorDtoCreate toCreate, Credential credentials);

    /**
     * Get the specified doctor.
     *
     * @param id the id of the doctor requested
     * @return the doctor with the id given
     */
    DoctorDtoSparse getDoctorById(Long id);

    /**
     * Get the specified doctor.
     *
     * @param id the id of the doctor Entity requested
     * @return the doctor with the id given
     */
    Doctor getDoctorEntityById(Long id);

    /**
     * Update the doctor with the given id.
     *
     * @param id       the id of the doctor to update
     * @param toUpdate the data to update the doctor with
     * @return the updated doctor
     */
    DoctorDtoSparse updateDoctor(Long id, DoctorDtoUpdate toUpdate);


    /**
     * Get all Doctors from the repository.
     *
     * @return a list of all Doctors
     */
    List<DoctorDtoSparse> getAllDoctors();

    /**
     * Get the doctor by email.
     *
     * @param email the email of the doctor
     * @return the doctor with the email given
     */
    DoctorDto getDoctorByEmail(String email);

    /**
     * Find a doctor by the given credential.
     *
     * @param credential the credential to search for
     * @return the doctor with the given credential
     */
    DoctorDto findDoctorByCredential(Credential credential);

    /**
     * Search for doctors based on the search criteria.
     *
     * @param search the search criteria
     * @return a list of doctors
     */
    List<DoctorDtoSparse> searchDoctors(UserDtoSearch search);

    /**
     * Check if the used id matches the token given.
     *
     * @param userId the id of the user
     * @return true if the userId is from the doctor that is sending the request, false otherwise
     */
    boolean isOwnRequest(Long userId);

    /**
     * Get a page of doctors.
     *
     * @param searchTerm the search term to search for
     * @param pageable   the pageable object
     * @return a page of doctors
     */
    Page<DoctorDto> getDoctors(String searchTerm, Pageable pageable);

}
