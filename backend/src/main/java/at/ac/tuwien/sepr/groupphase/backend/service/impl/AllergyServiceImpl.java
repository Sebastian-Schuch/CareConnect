package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AllergyDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.Allergy;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.AllergyRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.AllergyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class AllergyServiceImpl implements AllergyService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AllergyRepository allergyRepository;

    public AllergyServiceImpl(AllergyRepository allergyRepository) {
        this.allergyRepository = allergyRepository;
    }

    @Override
    public Allergy createAllergy(AllergyDtoCreate toCreate) {
        Allergy allergy = new Allergy();
        allergy.setName(toCreate.getName());
        if (allergyRepository.findByName(toCreate.getName()) != null) {
            throw new ConflictException("Allergy already exists");
        }
        return allergyRepository.save(allergy);
    }

    @Override
    public Allergy findById(Long id) {
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
    public int countAllergies() {
        return (int) allergyRepository.count();
    }

    @Override
    public Allergy findByName(String name) {
        if (name == null) {
            throw new NotFoundException("Name is null");
        }

        Optional<Allergy> maybeAllergy = Optional.ofNullable(allergyRepository.findByName(name));
        if (maybeAllergy.isPresent()) {
            return maybeAllergy.get();
        } else {
            throw new NotFoundException(String.format("Could not find allergy with name %s", name));
        }
    }

    @Override
    public List<Allergy> findAll() throws NotFoundException {
        List<Allergy> allergies = allergyRepository.findAll();
        if (allergies.isEmpty()) {
            throw new NotFoundException("No allergies found");
        }
        return allergies;
    }

    @Override
    public Allergy updateAllergy(AllergyDto allergy) {
        Allergy existingAllergy = findById(allergy.getUid());
        existingAllergy.setName(allergy.getName());
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
}
