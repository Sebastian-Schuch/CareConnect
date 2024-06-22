package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.MedicationMapper;
import at.ac.tuwien.sepr.groupphase.backend.specification.MedicationSpecification;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MedicationRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.MedicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.invoke.MethodHandles;
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
    public List<MedicationDto> getMedicationsPage(String searchTerm, Pageable pageable) {
        Specification<Medication> spec = Specification.where(MedicationSpecification.nameContains(searchTerm))
            .and(MedicationSpecification.isActive());
        return medicationMapper.medicationEntitiesToListOfMedicationDto(medicationRepository.findAll(spec, pageable).getContent());
    }
}
