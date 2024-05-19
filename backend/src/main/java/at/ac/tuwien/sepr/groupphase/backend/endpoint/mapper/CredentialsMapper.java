package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CredentialCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryCreateDto;
import org.mapstruct.Mapper;

@Mapper
public interface CredentialsMapper {

    /**
     * Converts the doctorCreateDto to the credentialCreateDto.
     *
     * @param doctorCreateDto the doctorCreateDto to convert
     * @return the credentialCreateDto
     */
    CredentialCreateDto doctorCreateDtoToCredentialCreateDto(DoctorCreateDto doctorCreateDto);

    /**
     * Converts the secretaryCreateDto to the credentialCreateDto.
     *
     * @param secretaryCreateDto the secretaryCreateDto to convert
     * @return the credentialCreateDto
     */
    CredentialCreateDto secretaryCreateDtoToCredentialCreateDto(SecretaryCreateDto secretaryCreateDto);

    /**
     * Converts the patientCreateDto to the credentialCreateDto.
     *
     * @param patientCreateDto the patientCreateDto to convert
     * @return the credentialCreateDto
     */
    CredentialCreateDto patientCreateDtoToCredentialCreateDto(PatientCreateDto patientCreateDto);
}
