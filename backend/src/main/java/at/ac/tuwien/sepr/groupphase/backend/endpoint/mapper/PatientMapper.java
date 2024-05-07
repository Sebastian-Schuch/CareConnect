package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import org.mapstruct.Mapper;

@Mapper
public class PatientMapper {
    public PatientDto patientToPatientDto(Patient patient) {
        return new PatientDto(patient.getCredential().getId(), patient.getSvnr(), patient.getCredential().getFirstName(), patient.getCredential().getLastName(), patient.getCredential().getEmail(), patient.getCredential().getPassword(), patient.getCredential().getActive());
    }
}
