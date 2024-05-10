package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;

import java.util.List;

public interface PatientService {
    /**
     * Creates a patient with the data given.
     *
     * @param toCreate the data to create the patient with
     * @return the created patient
     */
    PatientDto createPatient(PatientCreateDto toCreate);

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
}
