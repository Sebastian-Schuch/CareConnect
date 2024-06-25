package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TreatmentMedicineMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentMedicineRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TreatmentMedicineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for the treatment medicine entity.
 */
@Service
public class TreatmentMedicineServiceImpl implements TreatmentMedicineService {
    private static final Logger LOG = LoggerFactory.getLogger(TreatmentMedicineServiceImpl.class);
    private final TreatmentMedicineRepository treatmentMedicineRepository;
    private final TreatmentMedicineMapper treatmentMedicineMapper;

    public TreatmentMedicineServiceImpl(TreatmentMedicineRepository treatmentMedicineRepository, TreatmentMedicineMapper treatmentMedicineMapper) {
        this.treatmentMedicineRepository = treatmentMedicineRepository;
        this.treatmentMedicineMapper = treatmentMedicineMapper;
    }

    @Override
    public TreatmentMedicineDto createTreatmentMedicine(TreatmentMedicineDtoCreate treatmentMedicineDtoCreate) {
        LOG.trace("createTreatment()");
        TreatmentMedicine savedTreatmentMedicine = treatmentMedicineRepository.save(treatmentMedicineMapper.dtoToEntity(treatmentMedicineDtoCreate));
        return treatmentMedicineMapper.entityToDto(savedTreatmentMedicine);
    }

    @Override
    public TreatmentMedicineDto getTreatmentMedicineById(long id) throws NotFoundException {
        LOG.trace("getTreatmentMedicineById()");
        TreatmentMedicine treatmentMedicine = treatmentMedicineRepository.findById(id).orElse(null);
        if (treatmentMedicine == null) {
            LOG.warn("treatmentMedicine with id {} not found", id);
            throw new NotFoundException("Treatment medicine not found");
        }
        return treatmentMedicineMapper.entityToDto(treatmentMedicine);
    }

    @Override
    public TreatmentMedicine getTreatmentMedicineEntityById(long id) throws NotFoundException {
        LOG.trace("getTreatmentMedicineEntityById()");
        TreatmentMedicine treatmentMedicine = treatmentMedicineRepository.findById(id).orElse(null);
        if (treatmentMedicine == null) {
            LOG.warn("treatmentMedicine with id {} not found", id);
            throw new NotFoundException("Treatment medicine not found");
        }
        return treatmentMedicine;
    }

    @Override
    public void deleteTreatmentMedicine(long id) throws NotFoundException {
        LOG.trace("deleteTreatmentMedicine()");
        if (!treatmentMedicineRepository.existsById(id)) {
            LOG.warn("treatmentMedicine with id {} not found", id);
            throw new NotFoundException("Treatment medicine not found");
        }
        treatmentMedicineRepository.deleteById(id);
    }

    @Override
    public List<TreatmentMedicineDto> getAllTreatmentMedicines() {
        LOG.trace("getAllTreatmentMedicines()");
        return treatmentMedicineMapper.entityListToDtoList(treatmentMedicineRepository.findAll());
    }
}
