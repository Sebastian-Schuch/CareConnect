package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CredentialCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CredentialDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.service.CredentialService;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.PatientValidator;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientValidator patientValidator;
    private final CredentialService credentialService;

    public PatientServiceImpl(PatientValidator patientValidator, CredentialService credentialService) {
        this.patientValidator = patientValidator;
        this.credentialService = credentialService;
    }

    @Override
    public PatientDto createPatient(PatientCreateDto toCreate) {
        patientValidator.validateForCreate(toCreate);
        CredentialCreateDto toCreateCredentials = new CredentialCreateDto().setEmail(toCreate.getEmail()).setFirstname(toCreate.getFirstname()).setLastname(toCreate.getLastname());
        credentialService.createCredential(toCreateCredentials, Role.PATIENT);
        return null;
    }
}
