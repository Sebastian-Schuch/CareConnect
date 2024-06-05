package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Mapper
public class DoctorMapper {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Converts the doctor entity to the doctorDto.
     *
     * @param doctor the doctor to convert
     * @return the doctorDto
     */
    public DoctorDto doctorToDoctorDto(Doctor doctor) {
        LOG.trace("doctorToDoctorDto({})", doctor);
        return new DoctorDto(doctor.getDoctorId(), doctor.getCredential().getFirstName(), doctor.getCredential().getLastName(), doctor.getCredential().getEmail(), doctor.getCredential().getPassword(),
            doctor.getCredential().isInitialPassword(),
            doctor.getCredential().getActive());
    }

    /**
     * Converts all the doctor entities to doctorDtos.
     *
     * @param doctors the doctor entities to convert
     * @return the converted doctorDtos
     */
    public List<DoctorDto> doctorsToDoctorDtos(List<Doctor> doctors) {
        LOG.trace("doctorsToDoctorDtos({})", doctors);
        List<DoctorDto> doctorDtos = new ArrayList<>();
        for (Doctor doctor : doctors) {
            doctorDtos.add(doctorToDoctorDto(doctor));
        }
        return doctorDtos;
    }

    public Doctor updateDtoToEntity(DoctorDtoUpdate toUpdate, Doctor doctor) {
        LOG.trace("dtoToEntity({})", toUpdate);
        Doctor doctorUpdate = new Doctor();
        doctorUpdate.setDoctorId(doctor.getDoctorId());
        Credential credential = new Credential();
        credential.setRole(Role.DOCTOR);
        credential.setPassword(doctor.getCredential().getPassword());
        credential.setId(doctor.getCredential().getId());
        credential.setFirstName(toUpdate.firstname());
        credential.setLastName(toUpdate.lastname());
        credential.setEmail(toUpdate.email());
        credential.setActive(doctor.getCredential().getActive());
        doctorUpdate.setCredential(credential);
        return doctorUpdate;
    }
}
