package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationPageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.MedicationMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MedicationRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.MedicationService;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Service
public class MedicationServiceImpl implements MedicationService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MedicationRepository medicationRepository;

    private final MedicationMapper medicationMapper;

    public MedicationServiceImpl(MedicationRepository medicationRepository, MedicationMapper medicationMapper) {
        this.medicationRepository = medicationRepository;
        this.medicationMapper = medicationMapper;
    }

    @Override
    public MedicationDto create(MedicationDtoCreate toCreate) {
        LOG.trace("create{}", toCreate);
        Medication existingMedication = medicationRepository.findByName(toCreate.name());
        if (existingMedication != null) {
            throw new ConflictException("Medication already exists");
        }
        Medication medication = new Medication();
        medication.setName(toCreate.name());
        medication.setActive(true);
        return medicationMapper.medicationEntityToMedicationDto(medicationRepository.save(medication));
    }

    @Override
    public MedicationDto getById(Long id) {
        LOG.trace("getById({})", id);
        Medication medication = medicationRepository.findMedicationById(id);
        if (medication == null) {
            throw new NotFoundException("Medication not found");
        }
        return medicationMapper.medicationEntityToMedicationDto(medication);
    }

    @Override
    public Medication getEntityById(Long id) {
        LOG.trace("getEntityById({})", id);
        Medication medication = medicationRepository.findMedicationById(id);
        if (medication == null) {
            throw new NotFoundException("Medication not found");
        }
        return medication;
    }

    @Override
    public List<MedicationDto> getAllMedications() {
        LOG.trace("getAllMedications()");
        return medicationMapper.medicationEntitiesToListOfMedicationDto(medicationRepository.findByActiveTrue());
    }

    @Override
    public MedicationDto disableById(Long id) {
        LOG.trace("disableById({})", id);
        Medication medication = medicationRepository.findMedicationById(id);
        if (medication == null) {
            throw new NotFoundException("Medication not found");
        }
        medication.setActive(false);
        return medicationMapper.medicationEntityToMedicationDto(medicationRepository.save(medication));
    }

    @Override
    public MedicationPageDto searchMedications(String name, int page, int size) {
        LOG.trace("searchMedications({}, {}, {})", name, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString("ASC"), "name");
        Specification<Medication> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("active")));
            if (name != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return medicationMapper.toMedicationPageDto(medicationRepository.findAll(spec, pageable));
    }

    @Override
    public MedicationDto update(MedicationDto medicationDto) {
        LOG.trace("update({})", medicationDto);
        Medication sameName = medicationRepository.findByName(medicationDto.name());
        if (sameName != null) {
            throw new ConflictException("Medication with this name already exists");
        }
        Medication existingMedication = getEntityById(medicationDto.id());
        existingMedication.setName(medicationDto.name());
        return medicationMapper.medicationEntityToMedicationDto(medicationRepository.save(existingMedication));
    }
}
