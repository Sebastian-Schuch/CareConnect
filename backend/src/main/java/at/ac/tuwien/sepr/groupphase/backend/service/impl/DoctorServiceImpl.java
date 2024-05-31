package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DoctorMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DoctorRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DoctorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    public DoctorServiceImpl(DoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }


    @Override
    public DoctorDto createDoctor(DoctorDtoCreate toCreate, Credential credentials) {
        LOG.trace("createDoctor({})", toCreate);
        Doctor doctor = new Doctor();
        doctor.setCredential(credentials);
        return doctorMapper.doctorToDoctorDto(doctorRepository.save(doctor));

    }

    @Override
    public DoctorDto getDoctorById(Long id) {
        LOG.trace("getDoctorById({})", id);
        Doctor doctor = doctorRepository.findById(id).orElse(null);
        if (doctor == null) {
            LOG.warn("doctor with id {} not found", id);
            throw new NotFoundException("Doctor not found");
        }
        return doctorMapper.doctorToDoctorDto(doctor);
    }

    @Override
    public Doctor getDoctorEntityById(Long id) {
        LOG.trace("getDoctorEntityById({})", id);
        Doctor doctor = doctorRepository.findById(id).orElse(null);
        if (doctor == null) {
            LOG.warn("doctor with id {} not found", id);
            throw new NotFoundException("Doctor not found");
        }
        return doctor;
    }

    @Override
    public DoctorDto findDoctorByCredential(Credential credential) {
        LOG.debug("Find application user by email");
        Doctor doctor = doctorRepository.findByCredential(credential);
        if (doctor != null) {
            return doctorMapper.doctorToDoctorDto(doctor);
        }
        throw new NotFoundException(String.format("Could not find the user with the credential %s", credential));
    }

    @Override
    public List<DoctorDto> getAllDoctors() {
        return doctorMapper.doctorsToDoctorDtos(doctorRepository.findAll());
    }

    @Override
    public DoctorDto getDoctorByEmail(String email) {
        return doctorMapper.doctorToDoctorDto(doctorRepository.findByCredential_Email(email));
    }
}
