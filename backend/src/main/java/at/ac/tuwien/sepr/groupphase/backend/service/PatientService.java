package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;

public interface PatientService {
    PatientDto createPatient(PatientCreateDto toCreate);

    PatientDto getPatientById(Long id);
}
