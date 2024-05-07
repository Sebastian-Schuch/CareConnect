package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CredentialService;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.PatientValidator;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientValidator patientValidator;
    private final CredentialService credentialService;
    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientValidator patientValidator, CredentialService credentialService, PatientRepository patientRepository) {
        this.patientValidator = patientValidator;
        this.credentialService = credentialService;
        this.patientRepository = patientRepository;
    }

    @Override
    public PatientDto createPatient(PatientCreateDto toCreate) {
        System.out.println("SERVUS!");
        patientValidator.validateForCreate(toCreate);
        insertPatientData(toCreate, Role.PATIENT);
        return null;
    }

    public void insertPatientData(PatientCreateDto toCreate, Role role) {
        Credential credential = new Credential();
        credential.setEmail(toCreate.getEmail());
        credential.setFirstName(toCreate.getFirstname());
        credential.setLastName(toCreate.getLastname());
        credential.setPassword(new java.math.BigInteger(130, new java.security.SecureRandom()).toString(32));
        credential.setActive(true);
        credential.setRole(role);

        Patient patient = new Patient();
        patient.setSvnr(toCreate.getSvnr());
        patient.setCredential(credential);
        patientRepository.save(patient);
    }

}
