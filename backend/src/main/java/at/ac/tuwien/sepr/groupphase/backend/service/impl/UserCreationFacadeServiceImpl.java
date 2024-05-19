package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.service.DoctorService;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.SecretaryService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserCreationFacadeService;
import org.springframework.stereotype.Service;

@Service
public class UserCreationFacadeServiceImpl implements UserCreationFacadeService {
    private final DoctorService doctorService;
    private final SecretaryService secretaryService;
    private final PatientService patientService;

    public UserCreationFacadeServiceImpl(DoctorService doctorService, SecretaryService secretaryService, PatientService patientService) {
        this.doctorService = doctorService;
        this.secretaryService = secretaryService;
        this.patientService = patientService;
    }

    @Override
    public DoctorDto createUser(DoctorCreateDto toCreate, Credential credential) {
        return doctorService.createDoctor(toCreate, credential);
    }

    @Override
    public SecretaryDetailDto createUser(SecretaryCreateDto toCreate, Credential credential) {
        return secretaryService.create(toCreate, credential);
    }

    @Override
    public PatientDto createUser(PatientCreateDto toCreate, Credential credential) {
        return patientService.createPatient(toCreate, credential);
    }
}
