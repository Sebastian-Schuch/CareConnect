package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Mapper
public class PatientMapper {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public PatientDto patientToPatientDto(Patient patient) {
        LOG.trace("patientToPatientDto({})", patient);
        return new PatientDto(patient.getCredential().getId(), patient.getSvnr(), patient.getCredential().getFirstName(), patient.getCredential().getLastName(), patient.getCredential().getEmail(), patient.getCredential().getPassword(),
            patient.getCredential().getActive());
    }
}
