package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoUpdate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DoctorMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.DoctorRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DoctorService;
import at.ac.tuwien.sepr.groupphase.backend.specification.DoctorSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public DoctorDtoSparse getDoctorById(Long id) {
        LOG.trace("getDoctorById({})", id);
        Doctor doctor = doctorRepository.findByDoctorIdAndCredential_ActiveTrue(id);
        if (doctor == null) {
            LOG.warn("doctor with id {} not found", id);
            throw new NotFoundException("Doctor not found");
        }
        return doctorMapper.doctorToDoctorDtoSparse(doctor);
    }

    @Override
    public Doctor getDoctorEntityById(Long id) {
        LOG.trace("getDoctorEntityById({})", id);
        Doctor doctor = doctorRepository.findByDoctorIdAndCredential_ActiveTrue(id);
        if (doctor == null) {
            LOG.warn("doctor with id {} not found", id);
            throw new NotFoundException("Doctor not found");
        }
        return doctor;
    }

    @Override
    public DoctorDtoSparse updateDoctor(Long id, DoctorDtoUpdate toUpdate) {
        LOG.trace("updateDoctor({}, {})", id, toUpdate);
        Doctor doctor = doctorRepository.findByDoctorIdAndCredential_ActiveTrue(id);
        if (doctor == null) {
            LOG.warn("doctor with id {} not found", id);
            throw new NotFoundException("Doctor not found");
        }
        return doctorMapper.doctorToDoctorDtoSparse(doctorRepository.save(doctorMapper.updateDtoToEntity(toUpdate, doctor)));
    }

    @Override
    public DoctorDto findDoctorByCredential(Credential credential) {
        LOG.debug("Find application user by email");
        Doctor doctor = doctorRepository.findByCredentialAndCredential_ActiveTrue(credential);
        if (doctor != null) {
            return doctorMapper.doctorToDoctorDto(doctor);
        }
        throw new NotFoundException(String.format("Could not find the user with the credential %s", credential));
    }

    @Override
    public List<DoctorDtoSparse> searchDoctors(UserDtoSearch search) {
        LOG.trace("searchDoctors({})", search);
        return doctorMapper.doctorsToDoctorDtosSparse(
            doctorRepository.searchDoctor(this.makeStringSearchable(search.email()), this.makeStringSearchable(search.firstName()), this.makeStringSearchable(search.lastName())));
    }

    @Override
    public List<DoctorDtoSparse> getAllDoctors() {
        return doctorMapper.doctorsToDoctorDtosSparse(doctorRepository.findByCredential_ActiveTrue());
    }

    @Override
    public DoctorDto getDoctorByEmail(String email) {
        return doctorMapper.doctorToDoctorDto(doctorRepository.findByCredential_EmailAndCredential_ActiveTrue(email));
    }

    @Override
    public boolean isOwnRequest(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("DOCTOR"))) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = principal.toString();
            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername();
            }
            try {
                Doctor doctor = this.getDoctorEntityById(userId);
                return email.equals(doctor.getCredential().getEmail());
            } catch (NotFoundException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public Page<DoctorDto> getDoctors(String searchTerm, Pageable pageable) {
        Specification<Doctor> spec = Specification.where(DoctorSpecification.containsTextInAnyField(searchTerm))
            .and(DoctorSpecification.isActive());
        return doctorRepository.findAll(spec, pageable)
            .map(doctorMapper::doctorToDoctorDto);
    }

    /**
     * Make a string searchable by converting it to upper case.
     *
     * @param input the string to make searchable
     * @return the searchable string
     */
    private String makeStringSearchable(String input) {
        String search = null;
        if (input != null) {
            search = input.toUpperCase();
        }
        return search;
    }


}
