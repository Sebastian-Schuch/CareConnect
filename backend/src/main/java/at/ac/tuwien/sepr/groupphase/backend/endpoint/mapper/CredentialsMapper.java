package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CredentialDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoCreate;
import org.mapstruct.Mapper;

@Mapper
public interface CredentialsMapper {

    /**
     * Converts the doctorCreateDto to the credentialCreateDto.
     *
     * @param doctorCreateDto the doctorCreateDto to convert
     * @return the credentialCreateDto
     */
    CredentialDtoCreate doctorCreateDtoToCredentialCreateDto(DoctorDtoCreate doctorCreateDto);

    /**
     * Converts the secretaryCreateDto to the credentialCreateDto.
     *
     * @param secretaryCreateDto the secretaryCreateDto to convert
     * @return the credentialCreateDto
     */
    CredentialDtoCreate secretaryCreateDtoToCredentialCreateDto(SecretaryDtoCreate secretaryCreateDto);

    /**
     * Converts the patientCreateDto to the credentialCreateDto.
     *
     * @param patientCreateDto the patientCreateDto to convert
     * @return the credentialCreateDto
     */
    CredentialDtoCreate patientCreateDtoToCredentialCreateDto(PatientCreateDto patientCreateDto);
}
