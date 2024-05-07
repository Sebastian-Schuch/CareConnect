package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PatientMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CredentialService;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.PatientValidator;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class PatientServiceImpl implements PatientService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final PatientValidator patientValidator;
    private final CredentialService credentialService;
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientServiceImpl(PatientValidator patientValidator, CredentialService credentialService, PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientValidator = patientValidator;
        this.credentialService = credentialService;
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    @Override
    public PatientDto createPatient(PatientCreateDto toCreate) {
        LOG.trace("createPatient({})", toCreate);
        patientValidator.validateForCreate(toCreate);
        return insertPatientData(toCreate, Role.PATIENT);
    }

    @Override
    public PatientDto getPatientById(Long id) {
        LOG.trace("getPatientById({})", id);
        Patient patient = patientRepository.findPatientById(id);
        if (patient == null) {
            throw new NotFoundException("Patient not found");
        }
        return patientMapper.patientToPatientDto(patient);
    }

    public PatientDto insertPatientData(PatientCreateDto toCreate, Role role) {
        LOG.debug("insertPatientData({}, {})", toCreate, role);
        Patient patient = new Patient();
        patient.setSvnr(toCreate.svnr());
        patient.setCredential(credentialService.createCredentialEntity(toCreate.toCredentialCreateDto(), role));
        return patientMapper.patientToPatientDto(patientRepository.save(patient));
    }
}
