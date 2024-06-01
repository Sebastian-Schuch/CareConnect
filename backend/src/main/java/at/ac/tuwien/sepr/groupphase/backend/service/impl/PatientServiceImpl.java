package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PatientMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientServiceImpl(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    @Override
    public PatientDto createPatient(PatientDtoCreate toCreate, Credential credentials) {
        LOG.trace("createPatient({})", toCreate);
        return patientMapper.patientToPatientDto(patientRepository.save(patientMapper.dtoToEntity(toCreate, credentials)));
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
    public PatientDto getPatientByEmail(String email) {
        Patient patient = patientRepository.findByCredential_Email(email);
        if (patient == null) {
            LOG.warn("patient with email {} not found", email);
            throw new NotFoundException("Patient not found");
        }
        return patientMapper.patientToPatientDto(patient);
    }

    @Override
    public PatientDto findPatientByCredential(Credential credential) {
        LOG.debug("Find application user by email");
        Patient patient = patientRepository.findByCredential(credential);
        if (patient != null) {
            return patientMapper.patientToPatientDto(patient);
        }
        throw new NotFoundException(String.format("Could not find the user with the credential %s", credential));
    }

    @Override
    public List<PatientDto> getAllPatients() {
        return patientMapper.patientsToPatientDtos(patientRepository.findAll());
    }

    @Override
    public boolean isValidPatientRequest(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("PATIENT"))) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            try {
                Patient patient = this.getPatientEntityById(userId);
                return principal.toString().equals(patient.getCredential().getEmail());
            } catch (NotFoundException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
