package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

/**
 * Service for the treatment medicine entity.
 */
public interface TreatmentMedicineService {

    /**
     * Creates a new treatment medicine.
     *
     * @param treatmentMedicineDtoCreate the treatment medicine department to create
     * @return the created treatment medicine
     */
    TreatmentMedicineDto createTreatmentMedicine(TreatmentMedicineDtoCreate treatmentMedicineDtoCreate);

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

    /**
     * Deletes a treatment medicine by its id.
     *
     * @param id the id of the treatment medicine
     * @throws NotFoundException if the treatment medicine does not exist
     */
    void deleteTreatmentMedicine(long id) throws NotFoundException;


    /**
     * Gets all treatment medicines.
     *
     * @return the treatment medicines
     */
    List<TreatmentMedicineDto> getAllTreatmentMedicines();


}
