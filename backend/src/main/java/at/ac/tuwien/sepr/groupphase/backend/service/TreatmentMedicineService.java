package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * Service for the treatment medicine entity.
 */
public interface TreatmentMedicineService {

    /**
     * Creates a new treatment medicine.
     *
     * @param treatmentMedicineDtoCreate the treatment medicine department to create
     * @return the created treatment medicine
     * @throws MethodArgumentNotValidException if the treatment medicine is not valid
     */
    TreatmentMedicineDto createTreatmentMedicine(TreatmentMedicineDtoCreate treatmentMedicineDtoCreate) throws MethodArgumentNotValidException;

    /**
     * Gets a treatment medicine by its id.
     *
     * @param id the id of the treatment medicine
     * @return the treatment medicine
     * @throws NotFoundException if the treatment medicine does not exist
     */
    TreatmentMedicineDto getTreatmentMedicineById(long id) throws NotFoundException;

    /**
     * Gets a treatment medicine entity by its id.
     *
     * @param id the id of the treatment medicine
     * @return the treatment medicine
     * @throws NotFoundException if the treatment medicine does not exist
     */
    TreatmentMedicine getTreatmentMedicineEntityById(long id) throws NotFoundException;

}
