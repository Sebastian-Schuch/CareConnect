package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;

import java.util.List;

public interface PatientService {
    /**
     * Creates a patient with the data given.
     *
     * @param toCreate    the data to create the patient with
     * @param credentials the credentials of the patient
     * @return the created patient
     */
    PatientDto createPatient(PatientDtoCreate toCreate, Credential credentials);

    /**
     * Get the specified patient.
     *
     * @param id the id of the patient requested
     * @return the patient with the id given
     */
    PatientDtoSparse getPatientById(Long id);

    /**
     * Get all patients from repository.
     *
     * @return the list of all patients
     */
    List<PatientDtoSparse> getAllPatients();

    /**
     * Get the specified patient Entity.
     *
     * @param id the id of the patient requested
     * @return the patient with the id given
     */
    Patient getPatientEntityById(Long id);

    /**
     * Get the specified patient Entity.
     *
     * @param email the email of the patient requested
     * @return the patient with the email given
     */
    PatientDto getPatientByEmail(String email);

    /**
     * Update the patient with the given id.
     *
     * @param id       the id of the patient to update
     * @param toUpdate the data to update the patient with
     * @return the updated patient
     */
    PatientDtoSparse updatePatient(Long id, PatientDtoUpdate toUpdate);

    /**
     * Find a patient by the given credential.
     *
     * @param credential the credential to find the patient by
     * @return the patient with the credential given
     */
    PatientDtoSparse findPatientByCredential(Credential credential);

    /**
     * Search for patients based on the search criteria.
     *
     * @param search the search criteria
     * @return a list of patients
     */
    List<PatientDtoSparse> searchPatients(UserDtoSearch search);


    /**
     * Check if the used id matches the token given.
     *
     * @param userId the id of the user
     * @return true if the userId is from the patient that is sending the request, false otherwise
     */
    boolean isOwnRequest(Long userId);
}
