package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
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
import org.springframework.security.core.userdetails.UserDetails;
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
        return patientMapper.patientToPatientDto(patientRepository.save(patientMapper.createDtoToEntity(toCreate, credentials)));
    }

    @Override
    public PatientDtoSparse getPatientById(Long id) {
        LOG.trace("getPatientById({})", id);
        Patient patient = patientRepository.findByPatientIdAndCredential_ActiveTrue(id);
        if (patient == null) {
            LOG.warn("patient with id {} not found", id);
            throw new NotFoundException("Patient not found");
        }
        return patientMapper.patientToPatientDtoSparse(patient);
    }

    @Override
    public Patient getPatientEntityById(Long id) {
        LOG.trace("getPatientEntityById({})", id);
        Patient patient = patientRepository.findByPatientIdAndCredential_ActiveTrue(id);
        if (patient == null) {
            LOG.warn("patient with id {} not found", id);
            throw new NotFoundException("Patient not found");
        }
        return patient;
    }

    @Override
    public PatientDto getPatientByEmail(String email) {
        Patient patient = patientRepository.findByCredential_EmailAndCredential_ActiveTrue(email);
        if (patient == null) {
            LOG.warn("patient with email {} not found", email);
            throw new NotFoundException("Patient not found");
        }
        return patientMapper.patientToPatientDto(patient);
    }

    @Override
    public Patient getPatientEntityByEmail(String email) {
        Patient patient = patientRepository.findByCredential_EmailAndCredential_ActiveTrue(email);
        if (patient == null) {
            LOG.warn("patient with email {} not found", email);
            throw new NotFoundException("Patient not found");
        }
        return patient;
    }

    @Override
    public PatientDtoSparse updatePatient(Long id, PatientDtoUpdate toUpdate) {
        LOG.trace("updatePatient({}, {})", id, toUpdate);
        Patient patient = patientRepository.findByPatientIdAndCredential_ActiveTrue(id);
        if (patient == null) {
            LOG.warn("patient with id {} not found", id);
            throw new NotFoundException("Patient not found");
        }
        return patientMapper.patientToPatientDtoSparse(patientRepository.save(patientMapper.updateDtoToEntity(toUpdate, patient)));
    }

    @Override
    public PatientDtoSparse findPatientByCredential(Credential credential) {
        LOG.debug("Find application user by email");
        Patient patient = patientRepository.findByCredentialAndCredential_ActiveTrue(credential);
        if (patient != null) {
            return patientMapper.patientToPatientDtoSparse(patient);
        }
        throw new NotFoundException(String.format("Could not find the user with the credential %s", credential));
    }

    @Override
    public List<PatientDtoSparse> searchPatients(UserDtoSearch search) {
        LOG.trace("searchPatients({})", search);
        return patientMapper.patientsToPatientDtosSparse(
            patientRepository.searchPatient(this.makeStringSearchable(search.email()), this.makeStringSearchable(search.firstName()), this.makeStringSearchable(search.lastName())));
    }

    @Override
    public List<PatientDtoSparse> getAllPatients() {
        return patientMapper.patientsToPatientDtosSparse(patientRepository.findByCredential_ActiveTrue());
    }

    @Override
    public boolean isOwnRequest(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("PATIENT"))) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = principal.toString();
            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername();
            }
            try {
                Patient patient = this.getPatientEntityById(userId);
                return email.equals(patient.getCredential().getEmail());
            } catch (NotFoundException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Make a string searchable by converting it to upper case.
     *
     * @param input the string to make searchable
     * @return the searchable string
     */
    private String makeStringSearchable(String input) {
        String search = null;
        if (input != null) {
            search = input.toUpperCase();
        }
        return search;
    }
}
