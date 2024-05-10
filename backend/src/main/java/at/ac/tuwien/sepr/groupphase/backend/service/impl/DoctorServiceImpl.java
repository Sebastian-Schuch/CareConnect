package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DoctorMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DoctorRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.CredentialService;
import at.ac.tuwien.sepr.groupphase.backend.service.DoctorService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CredentialService credentialService;
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    public DoctorServiceImpl(CredentialService credentialService, DoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.credentialService = credentialService;
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }


    @Override
    public DoctorDto createDoctor(DoctorCreateDto toCreate) {
        LOG.trace("createDoctor({})", toCreate);
        Doctor doctor = new Doctor();
        doctor.setCredential(credentialService.createCredentialEntity(toCreate.toCredentialCreateDto(), Role.DOCTOR));
        return doctorMapper.doctorToDoctorDto(doctorRepository.save(doctor));

    }

    @Override
    public DoctorDto getDoctorById(Long id) {
        LOG.trace("getDoctorById({})", id);
        Doctor doctor = doctorRepository.findDoctorById(id);
        if (doctor == null) {
            throw new NotFoundException("Doctor not found");
        }
        return doctorMapper.doctorToDoctorDto(doctor);

    }

    @Override
    public List<DoctorDto> getAllDoctors() {
        return doctorMapper.doctorsToDoctorDtos(doctorRepository.findAll());
    }
}
