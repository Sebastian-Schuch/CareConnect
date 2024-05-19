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


}
