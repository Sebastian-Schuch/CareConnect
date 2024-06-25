package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.service.AdminService;
import at.ac.tuwien.sepr.groupphase.backend.service.DoctorService;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.SecretaryService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserCreationFacadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class UserCreationFacadeServiceImpl implements UserCreationFacadeService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DoctorService doctorService;
    private final SecretaryService secretaryService;
    private final PatientService patientService;

    private final AdminService adminService;

    public UserCreationFacadeServiceImpl(DoctorService doctorService, SecretaryService secretaryService, PatientService patientService, AdminService adminService) {
        this.doctorService = doctorService;
        this.secretaryService = secretaryService;
        this.patientService = patientService;
        this.adminService = adminService;
    }

    @Override
    public DoctorDto createUser(DoctorDtoCreate toCreate, Credential credential) {
        LOG.trace("createDoctor({})", toCreate);
        return doctorService.createDoctor(toCreate, credential);
    }

    @Override
    public SecretaryDto createUser(SecretaryDtoCreate toCreate, Credential credential) {
        LOG.trace("createSecretary({})", toCreate);
        return secretaryService.create(toCreate, credential);
    }

    @Override
    public PatientDto createUser(PatientDtoCreate toCreate, Credential credential) {
        LOG.trace("createPatient({})", toCreate);
        return patientService.createPatient(toCreate, credential);
    }

    @Override
    public AdminDto createUser(AdminDtoCreate toCreate, Credential credential) {
        LOG.trace("createAdmin({})", toCreate);
        return adminService.createAdministrator(toCreate, credential);
    }
}
