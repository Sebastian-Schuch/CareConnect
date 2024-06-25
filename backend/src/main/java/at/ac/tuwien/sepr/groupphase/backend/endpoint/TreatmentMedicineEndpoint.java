package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.service.TreatmentMedicineService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/treatmentMedicines")
public class TreatmentMedicineEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TreatmentMedicineService treatmentMedicineService;

    @Autowired
    public TreatmentMedicineEndpoint(TreatmentMedicineService treatmentMedicineService) {
        LOG.info("TreatmentMedicineEndpoint({})", treatmentMedicineService);
        this.treatmentMedicineService = treatmentMedicineService;
    }

    /**
     * Create a new treatment medicine.
     *
     * @param treatmentMedicineDtoCreate the treatment medicine to create
     * @return the created treatment medicine
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("DOCTOR")
    public TreatmentMedicineDto createTreatmentMedicine(@Valid @RequestBody TreatmentMedicineDtoCreate treatmentMedicineDtoCreate) {
        LOG.info("createTreatmentMedicine({})", treatmentMedicineDtoCreate);
        return treatmentMedicineService.createTreatmentMedicine(treatmentMedicineDtoCreate);
    }

    /**
     * Delete a treatment medicine.
     *
     * @param id the id of the treatment medicine to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("DOCTOR")
    public void deleteTreatmentMedicine(@PathVariable("id") long id) {
        LOG.info("deleteTreatmentMedicine({})", id);
        treatmentMedicineService.deleteTreatmentMedicine(id);
    }

}
