package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TreatmentMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Treatment;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentMedicineRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TreatmentMedicineService;
import at.ac.tuwien.sepr.groupphase.backend.service.TreatmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Service for the treatment entity.
 */

@Service
public class TreatmentServiceImpl implements TreatmentService {

    private static final Logger log = LoggerFactory.getLogger(TreatmentServiceImpl.class);
    private final TreatmentRepository treatmentRepository;
    private final TreatmentMapper treatmentMapper;
    private final TreatmentMedicineServiceImpl treatmentMedicineServiceImpl;

    @Autowired
    public TreatmentServiceImpl(TreatmentRepository treatmentRepository, TreatmentMapper treatmentMapper, TreatmentMedicineService treatmentMedicineService, TreatmentMedicineServiceImpl treatmentMedicineServiceImpl) {
        this.treatmentRepository = treatmentRepository;
        this.treatmentMapper = treatmentMapper;
        this.treatmentMedicineServiceImpl = treatmentMedicineServiceImpl;
    }

    @Override
    public TreatmentDto createTreatment(TreatmentDtoCreate treatmentDtoCreate) {
        log.trace("createTreatment()");
        Treatment treatment = treatmentMapper.dtoToEntity(treatmentDtoCreate);
        Treatment savedTreatment = treatmentRepository.save(treatment);
        return treatmentMapper.entityToDto(savedTreatment);
    }

    @Override
    public TreatmentDto updateTreatment(Long id, TreatmentDtoCreate treatmentDtoCreate) throws NotFoundException {
        log.trace("updateTreatment()");
        Treatment updatedTreatment = treatmentMapper.dtoToEntity(treatmentDtoCreate);
        updatedTreatment.setId(id);

        treatmentRepository.save(updatedTreatment);
        return treatmentMapper.entityToDto(updatedTreatment);
    }

    @Override
    public TreatmentDto getTreatmentById(Long id) throws NotFoundException {
        log.trace("getTreatmentByID({})", id);
        Treatment treatment = treatmentRepository.findById(id).orElse(null);
        if (treatment == null) {
            log.warn("treatment with id {} not found", id);
            throw new NotFoundException("treatment with id " + id + " not found");
        }
        return treatmentMapper.entityToDto(treatment);
    }

    @Override
    public Treatment getTreatmentEntityById(Long id) throws NotFoundException {
        log.trace("getTreatmentEntityById({})", id);
        Treatment treatment = treatmentRepository.findById(id).orElse(null);
        if (treatment == null) {
            log.warn("treatment with id {} not found", id);
            throw new NotFoundException("treatment with id " + id + " not found");
        }
        return treatment;
    }

    @Override
    public List<TreatmentDto> getAllTreatmentsFromPatient(Long patientId) {
        List<Treatment> treatments = treatmentRepository.findByPatient_PatientId(patientId);
        return treatmentMapper.entityListToDtoList(treatments);
    }

    @Override
    public List<TreatmentDto> getAllTreatmentsFromDoctor(Long doctorId) {
        List<Treatment> treatments = treatmentRepository.findByDoctors_DoctorId(doctorId);
        return treatmentMapper.entityListToDtoList(treatments);
    }
}
