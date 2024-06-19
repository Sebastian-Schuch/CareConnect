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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service for the treatment entity.
 */

@Service
public class TreatmentServiceImpl implements TreatmentService {

    private static final Logger log = LoggerFactory.getLogger(TreatmentServiceImpl.class);
    private final TreatmentRepository treatmentRepository;
    private final TreatmentMapper treatmentMapper;

    @Autowired
    public TreatmentServiceImpl(TreatmentRepository treatmentRepository, TreatmentMapper treatmentMapper, TreatmentMedicineServiceImpl treatmentMedicineServiceImpl) {
        this.treatmentRepository = treatmentRepository;
        this.treatmentMapper = treatmentMapper;
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
        log.trace("getAllTreatmentsFromPatient({})", patientId);
        List<Treatment> treatments = treatmentRepository.findByPatient_PatientId(patientId);
        return treatmentMapper.entityListToDtoList(treatments);
    }

    @Override
    public List<TreatmentDto> getAllTreatmentsFromDoctor(Long doctorId) {
        log.trace("getAllTreatmentsFromDoctor({})", doctorId);
        List<Treatment> treatments = treatmentRepository.findByDoctors_DoctorId(doctorId);
        return treatmentMapper.entityListToDtoList(treatments);
    }

    @Override
    public TreatmentPageDto searchTreatments(TreatmentDtoSearch searchParams) {
        log.trace("searchTreatments({})", searchParams);
        Pageable pageable = PageRequest.of(searchParams.page(), searchParams.size(), Sort.Direction.fromString("DESC"), "treatmentStart");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Specification<Treatment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (searchParams.startDate() != null && searchParams.endDate() != null) {
                try {
                    Date start = sdf.parse(searchParams.startDate().split("T")[0]);
                    Date end = sdf.parse(searchParams.endDate().split("T")[0]);
                    predicates.add(cb.between(root.get("treatmentStart"), start, end));
                } catch (ParseException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format");
                }
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
