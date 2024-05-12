package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PatientMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CredentialService;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CredentialService credentialService;
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientServiceImpl(CredentialService credentialService, PatientRepository patientRepository, PatientMapper patientMapper) {
        this.credentialService = credentialService;
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    @Override
    public PatientDto createPatient(PatientCreateDto toCreate) {
        LOG.trace("createPatient({})", toCreate);
        Patient patient = new Patient();
        patient.setSvnr(toCreate.svnr());
        patient.setCredential(credentialService.createCredentialEntity(toCreate.toCredentialCreateDto(), Role.PATIENT));
        return patientMapper.patientToPatientDto(patientRepository.save(patient));
    }

    @Override
    public PatientDto getPatientById(Long id) {
        LOG.trace("getPatientById({})", id);
        Patient patient = patientRepository.findById(id).orElse(null);
        if (patient == null) {
            LOG.warn("patient with id {} not found", id);
            throw new NotFoundException("Patient not found");
        }
        return patientMapper.patientToPatientDto(patient);
    }

    @Override
    public Patient getPatientEntityById(Long id) {
        LOG.trace("getPatientEntityById({})", id);
        Patient patient = patientRepository.findById(id).orElse(null);
        if (patient == null) {
            LOG.warn("patient with id {} not found", id);
            throw new NotFoundException("Patient not found");
        }
        return patient;
    }


    @Override
    public List<PatientDto> getAllPatients() {
        return patientMapper.patientsToPatientDtos(patientRepository.findAll());
    }
}
