package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Mapper
public class PatientMapper {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Converts the patient entity to the patientDto.
     *
     * @param patient the patient to convert
     * @return the patientDto
     */
    public PatientDto patientToPatientDto(Patient patient) {
        LOG.trace("patientToPatientDto({})", patient);
        return new PatientDto(patient.getCredential().getId(), patient.getSvnr(), patient.getCredential().getFirstName(), patient.getCredential().getLastName(), patient.getCredential().getEmail(), patient.getCredential().getPassword(),
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
}
