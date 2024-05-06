package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.PatientValidator;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientValidator patientValidator;

    public PatientServiceImpl(PatientValidator patientValidator) {
        this.patientValidator = patientValidator;
    }
    @Override
    public PatientDto createPatient(PatientCreateDto toCreate) {
        patientValidator.validateForCreate(toCreate);
        return null;
    }
}
