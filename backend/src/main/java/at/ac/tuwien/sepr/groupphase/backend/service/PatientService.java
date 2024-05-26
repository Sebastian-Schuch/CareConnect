package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
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
    PatientDto createPatient(PatientCreateDto toCreate, Credential credentials);

    /**
     * Get the specified patient.
     *
     * @param id the id of the patient requested
     * @return the patient with the id given
     */
    PatientDto getPatientById(Long id);

    /**
     * Get all patients from repository.
     *
     * @return the list of all patients
     */
    List<PatientDto> getAllPatients();

    /**
     * Get the specified patient Entity.
     *
     * @param id the id of the patient requested
     * @return the patient with the id given
     */
    Patient getPatientEntityById(Long id);

    /**
     * Find a patient by the given credential.
     *
     * @param credential the credential to find the patient by
     * @return the patient with the credential given
     */
    PatientDto findPatientByCredential(Credential credential);

    /**
     * Check if the used id matches the token given.
     *
     * @param userId the id of the user
     * @return true if the user is from the patient that is sending the request, false otherwise
     */
    boolean isValidPatientRequest(Long userId);
}
