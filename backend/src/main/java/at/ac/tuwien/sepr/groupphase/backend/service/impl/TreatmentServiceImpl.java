package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TreatmentMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Treatment;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TreatmentService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for the treatment entity.
 */

@Service
public class TreatmentServiceImpl implements TreatmentService {

    private static final Logger LOG = LoggerFactory.getLogger(TreatmentServiceImpl.class);
    private final TreatmentRepository treatmentRepository;
    private final TreatmentMapper treatmentMapper;

    @Autowired
    public TreatmentServiceImpl(TreatmentRepository treatmentRepository, TreatmentMapper treatmentMapper, TreatmentMedicineServiceImpl treatmentMedicineServiceImpl) {
        this.treatmentRepository = treatmentRepository;
        this.treatmentMapper = treatmentMapper;
    }

    @Override
    public TreatmentDto createTreatment(TreatmentDtoCreate treatmentDtoCreate) {
        LOG.trace("createTreatment()");
        Treatment treatment = treatmentMapper.dtoToEntity(treatmentDtoCreate);
        Treatment savedTreatment = treatmentRepository.save(treatment);
        return treatmentMapper.entityToDto(savedTreatment);
    }

    @Override
    public TreatmentDto updateTreatment(Long id, TreatmentDtoCreate treatmentDtoCreate) throws NotFoundException {
        LOG.trace("updateTreatment()");
        Treatment updatedTreatment = treatmentMapper.dtoToEntity(treatmentDtoCreate);
        updatedTreatment.setId(id);

        treatmentRepository.save(updatedTreatment);
        return treatmentMapper.entityToDto(updatedTreatment);
    }

    @Override
    public TreatmentDto getTreatmentById(Long id) throws NotFoundException {
        LOG.trace("getTreatmentByID({})", id);
        Treatment treatment = treatmentRepository.findById(id).orElse(null);
        if (treatment == null) {
            LOG.warn("treatment with id {} not found", id);
            throw new NotFoundException("treatment with id " + id + " not found");
        }
        return treatmentMapper.entityToDto(treatment);
    }

    @Override
    public Treatment getTreatmentEntityById(Long id) throws NotFoundException {
        LOG.trace("getTreatmentEntityById({})", id);
        Treatment treatment = treatmentRepository.findById(id).orElse(null);
        if (treatment == null) {
            LOG.warn("treatment with id {} not found", id);
            throw new NotFoundException("treatment with id " + id + " not found");
        }
        return treatment;
    }

    @Override
    public List<TreatmentDto> getAllTreatmentsFromPatient(Long patientId) {
        LOG.trace("getAllTreatmentsFromPatient({})", patientId);
        List<Treatment> treatments = treatmentRepository.findByPatient_PatientId(patientId);
        return treatmentMapper.entityListToDtoList(treatments);
    }

    @Override
    public List<TreatmentDto> getAllTreatmentsFromDoctor(Long doctorId) {
        LOG.trace("getAllTreatmentsFromDoctor({})", doctorId);
        List<Treatment> treatments = treatmentRepository.findByDoctors_DoctorId(doctorId);
        return treatmentMapper.entityListToDtoList(treatments);
    }

    @Override
    public TreatmentPageDto searchTreatments(TreatmentDtoSearch searchParams) {
        LOG.trace("searchTreatments({})", searchParams);
        Pageable pageable = PageRequest.of(searchParams.page(), searchParams.size(), Sort.Direction.fromString("DESC"), "treatmentStart");

        Specification<Treatment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (searchParams.startDate() != null && searchParams.endDate() != null) {
                predicates.add(cb.or(
                    cb.and(cb.lessThanOrEqualTo(root.get("treatmentStart"), searchParams.startDate()), cb.greaterThanOrEqualTo(root.get("treatmentEnd"), searchParams.endDate())),
                    cb.and(cb.greaterThanOrEqualTo(root.get("treatmentStart"), searchParams.startDate()), cb.lessThanOrEqualTo(root.get("treatmentEnd"), searchParams.endDate())),
                    cb.and(cb.greaterThanOrEqualTo(root.get("treatmentStart"), searchParams.startDate()), cb.lessThanOrEqualTo(root.get("treatmentStart"), searchParams.endDate())),
                    cb.and(cb.lessThanOrEqualTo(root.get("treatmentEnd"), searchParams.endDate()), cb.greaterThanOrEqualTo(root.get("treatmentEnd"), searchParams.startDate()))
                ));
            }
            if (searchParams.doctorName() != null) {
                Join<Treatment, Doctor> doctorJoin = root.join("doctors");
                Join<Doctor, Credential> credentialsJoin = doctorJoin.join("credential");
                Predicate firstName = cb.like(cb.lower(credentialsJoin.get("firstName")), "%" + searchParams.doctorName().toLowerCase().trim() + "%");
                Predicate lastName = cb.like(cb.lower(credentialsJoin.get("lastName")), "%" + searchParams.doctorName().toLowerCase().trim() + "%");
                predicates.add(cb.or(firstName, lastName));
            }

            if (searchParams.patientId() != null && searchParams.patientId() != -1) {
                predicates.add(cb.equal(root.get("patient").get("id"), searchParams.patientId()));
            } else {
                if (searchParams.patientName() != null) {
                    Join<Treatment, Patient> patientJoin = root.join("patient");
                    Join<Patient, Credential> credentialsJoin = patientJoin.join("credential");
                    Predicate firstName = cb.like(cb.lower(credentialsJoin.get("firstName")), "%" + searchParams.patientName().toLowerCase().trim() + "%");
                    Predicate lastName = cb.like(cb.lower(credentialsJoin.get("lastName")), "%" + searchParams.patientName().toLowerCase().trim() + "%");
                    predicates.add(cb.or(firstName, lastName));
                }
                if (searchParams.svnr() != null && searchParams.svnr().trim().length() == 10) {
                    Join<Treatment, Patient> patientJoin = root.join("patient");
                    predicates.add(cb.equal(patientJoin.get("svnr"), searchParams.svnr().trim()));
                }
            }
            if (searchParams.treatmentTitle() != null) {
                predicates.add(cb.like(cb.lower(root.get("treatmentTitle")), "%" + searchParams.treatmentTitle().toLowerCase().trim() + "%"));
            }
            if (searchParams.departmentName() != null) {
                Join<Treatment, OutpatientDepartment> outpatientJoin = root.join("outpatientDepartment");
                predicates.add(cb.like(cb.lower(outpatientJoin.get("name")), "%" + searchParams.departmentName().toLowerCase().trim() + "%"));
            }
            if (searchParams.medicationName() != null) {
                Join<Treatment, TreatmentMedicine> treatmentMedicineJoin = root.join("medicines");
                Join<TreatmentMedicine, Medication> medicineJoin = treatmentMedicineJoin.join("medicine");
                predicates.add(cb.like(cb.lower(medicineJoin.get("name")), "%" + searchParams.medicationName().toLowerCase().trim() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return treatmentMapper.toTreatmentPageDto(treatmentRepository.findAll(spec, pageable));
    }
}
