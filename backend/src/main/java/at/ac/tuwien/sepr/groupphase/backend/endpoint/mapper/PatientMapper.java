package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.Allergy;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.service.AllergyService;
import at.ac.tuwien.sepr.groupphase.backend.service.MedicationService;
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
            patient.getCredential().getActive());

    }

    /**
     * Converts the patient entity list into a patientDto list.
     *
     * @param patients the patients to convert
     * @return the converted patients
     */
    public List<PatientDto> patientsToPatientDtos(List<Patient> patients) {
        LOG.trace("patientsToPatientsDtos({})", patients);
        List<PatientDto> patientDtos = new ArrayList<>();
        for (Patient patient : patients) {
            patientDtos.add(patientToPatientDto(patient));
        }
        return patientDtos;
    }

    public Patient dtoToEntity(PatientDtoCreate toCreate, Credential credentials) {
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
            for (MedicationDto medication : toCreate.medicines()) {
                allergies.add(allergyService.getEntityById(medication.id()));
            }
            patient.setAllergies(allergies);
        }
        patient.setCredential(credentials);
        return patient;
    }
}
