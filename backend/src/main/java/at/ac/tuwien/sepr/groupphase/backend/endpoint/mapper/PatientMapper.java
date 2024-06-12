package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.entity.Allergy;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.service.AllergyService;
import at.ac.tuwien.sepr.groupphase.backend.service.MedicationService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class PatientMapper {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MedicationService medicationService;
    private final AllergyService allergyService;
    private final AllergyMapper allergyMapper;
    private final MedicationMapper medicationMapper;

    public PatientMapper(MedicationService medicationService, AllergyService allergyService, AllergyMapper allergyMapper, MedicationMapper medicationMapper) {
        this.medicationService = medicationService;
        this.allergyService = allergyService;
        this.allergyMapper = allergyMapper;
        this.medicationMapper = medicationMapper;
    }

    /**
     * Converts the patient entity to the patientDto.
     *
     * @param patient the patient to convert
     * @return the patientDto
     */
    public PatientDto patientToPatientDto(Patient patient) {
        LOG.trace("patientToPatientDto({})", patient);
        return new PatientDto(patient.getPatientId(), patient.getSvnr(), medicationMapper.medicationEntitiesToListOfMedicationDto(patient.getMedicines()), allergyMapper.allergyToDto(patient.getAllergies()),
            patient.getCredential().getFirstName(), patient.getCredential().getLastName(), patient.getCredential().getEmail(),
            patient.getCredential().getPassword(),
            patient.getCredential().isInitialPassword(),
            patient.getCredential().getActive());
    }

    /**
     * Converts the patient entity to the patientDtoSparse without password.
     *
     * @param patient the patient to convert
     * @return the patientDto
     */
    public PatientDtoSparse patientToPatientDtoSparse(Patient patient) {
        LOG.trace("patientToPatientDtoSparse({})", patient);
        return new PatientDtoSparse(patient.getPatientId(), patient.getSvnr(), medicationMapper.medicationEntitiesToListOfMedicationDto(patient.getMedicines()), allergyMapper.allergyToDto(patient.getAllergies()),
            patient.getCredential().getFirstName(), patient.getCredential().getLastName(), patient.getCredential().getEmail(),
            patient.getCredential().isInitialPassword());
    }

    /**
     * Converts the patient entity list into a patientDto list.
     *
     * @param patients the patients to convert
     * @return the converted patients
     */
    public List<PatientDtoSparse> patientsToPatientDtosSparse(List<Patient> patients) {
        LOG.trace("patientsToPatientsDtos({})", patients);
        List<PatientDtoSparse> patientDtos = new ArrayList<>();
        for (Patient patient : patients) {
            patientDtos.add(patientToPatientDtoSparse(patient));
        }
        return patientDtos;
    }

    public Patient createDtoToEntity(PatientDtoCreate toCreate, Credential credentials) {
        LOG.trace("createDtoToEntity({}, {})", toCreate, credentials);
        Patient patient = new Patient();
        patient.setSvnr(toCreate.svnr());
        if (toCreate.medicines() != null) {
            List<Medication> medications = new ArrayList<>();
            for (MedicationDto medication : toCreate.medicines()) {
                medications.add(medicationService.getEntityById(medication.id()));
            }
            patient.setMedicines(medications);
        }
        if (toCreate.allergies() != null) {
            List<Allergy> allergies = new ArrayList<>();
            for (AllergyDto allergy : toCreate.allergies()) {
                allergies.add(allergyService.getEntityById(allergy.id()));
            }
            patient.setAllergies(allergies);
        }
        patient.setCredential(credentials);
        return patient;
    }

    public Patient updateDtoToEntity(PatientDtoUpdate toUpdate, Patient patient) {
        LOG.trace("dtoToEntity({})", toUpdate);
        Patient patientUpdate = new Patient();
        patientUpdate.setPatientId(patient.getPatientId());
        patientUpdate.setSvnr(toUpdate.svnr());
        if (toUpdate.medicines() != null) {
            List<Medication> medications = new ArrayList<>();
            for (MedicationDto medication : toUpdate.medicines()) {
                medications.add(medicationService.getEntityById(medication.id()));
            }
            patientUpdate.setMedicines(medications);
        }
        if (toUpdate.allergies() != null) {
            List<Allergy> allergies = new ArrayList<>();
            for (AllergyDto allergy : toUpdate.allergies()) {
                allergies.add(allergyService.getEntityById(allergy.id()));
            }
            patientUpdate.setAllergies(allergies);
        }
        Credential credential = new Credential();
        credential.setRole(Role.PATIENT);
        credential.setPassword(patient.getCredential().getPassword());
        credential.setId(patient.getCredential().getId());
        credential.setFirstName(toUpdate.firstname());
        credential.setLastName(toUpdate.lastname());
        credential.setEmail(toUpdate.email());
        credential.setActive(patient.getCredential().getActive());
        patientUpdate.setCredential(credential);
        return patientUpdate;
    }
}
