package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyPageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.AllergyMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Allergy;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.AllergyRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.AllergyService;
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
import java.util.Optional;

@Service
public class AllergyServiceImpl implements AllergyService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AllergyRepository allergyRepository;
    private final AllergyMapper allergyMapper;

    public AllergyServiceImpl(AllergyRepository allergyRepository, AllergyMapper allergyMapper) {
        this.allergyRepository = allergyRepository;
        this.allergyMapper = allergyMapper;
    }

    @Override
    public Allergy createAllergy(AllergyDtoCreate toCreate) {
        LOG.trace("create({})", toCreate);
        Allergy existingAllergy = findByName(toCreate.name());
        if (existingAllergy != null) {
            throw new ConflictException("Allergy already exists");
        }
        Allergy allergy = new Allergy();
        allergy.setName(toCreate.name());
        allergy.setActive(true);
        return allergyRepository.save(allergy);
    }

    @Override
    public Allergy findById(Long id) {
        LOG.trace("findById({})", id);
        if (id == null) {
            throw new NotFoundException("Id is null");
        }

        Optional<Allergy> allergy = allergyRepository.findById(id);
        if (allergy.isPresent()) {
            return allergy.get();
        } else {
            throw new NotFoundException(String.format("Could not find allergy with id %s", id));
        }
    }

    @Override
    public Allergy findByName(String name) {
        LOG.trace("findByName({})", name);
        return allergyRepository.findByName(name);
    }

    @Override
    public List<Allergy> findAll() throws NotFoundException {
        LOG.trace("findAll()");
        return (List<Allergy>) allergyRepository.findAll();
    }

    @Override
    public Allergy updateAllergy(AllergyDto allergy) {
        LOG.trace("updateAllergy({})", allergy);
        Allergy existingAllergy = findById(allergy.id());
        existingAllergy.setName(allergy.name());
        return allergyRepository.save(existingAllergy);
    }

    @Override
    public Allergy getEntityById(Long id) {
        LOG.trace("getEntityById({})", id);
        Allergy allergy = allergyRepository.findAllergyById(id);
        if (allergy == null) {
            throw new NotFoundException("Allergy not found");
        }
        return allergy;
    }

    @Override
    public AllergyPageDto searchAllergies(String name, Integer page, Integer size) {
        LOG.trace("searchAllergies({}, {}, {})", name, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString("ASC"), "name");
        Specification<Allergy> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return allergyMapper.toAllergyPageDto(allergyRepository.findAll(spec, pageable));
    }
}
