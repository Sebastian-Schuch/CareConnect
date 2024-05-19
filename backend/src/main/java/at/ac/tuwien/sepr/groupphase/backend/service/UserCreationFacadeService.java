package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;

public interface UserCreationFacadeService {
    /**
     * Creates a doctor with the data given.
     *
     * @param toCreate   the data to create the doctor with
     * @param credential the credential to create the doctor with
     * @return the created doctor
     */
    DoctorDto createUser(DoctorCreateDto toCreate, Credential credential);

    /**
     * Creates a secretary with the data given.
     *
     * @param toCreate   the data to create the secretary with
     * @param credential the credential to create the secretary with
     * @return the created secretary
     */
    SecretaryDetailDto createUser(SecretaryCreateDto toCreate, Credential credential);

    /**
     * Creates a patient with the data given.
     *
     * @param toCreate   the data to create the patient with
     * @param credential the credential to create the patient with
     * @return the created patient
     */
    PatientDto createUser(PatientCreateDto toCreate, Credential credential);
}
