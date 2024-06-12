package at.ac.tuwien.sepr.groupphase.backend.integrationtest.treatment.util;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepr.groupphase.backend.service.TreatmentMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;


@Component
public class TreatmentTestUtils {

    @Autowired
    private final TreatmentMedicineService treatmentMedicineService;

    public TreatmentTestUtils(TreatmentMedicineService treatmentMedicineService) {
        this.treatmentMedicineService = treatmentMedicineService;
    }

    public Date createDate(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        return calendar.getTime();
    }

    public TreatmentMedicineDto createTreatmentMedicineDto(MedicationDto medication, Date date) {
        TreatmentMedicineDtoCreate treatmentMedicineDtoCreate = new TreatmentMedicineDtoCreate(
            medication,
            "mg",
            1,
            date
        );
        return treatmentMedicineService.createTreatmentMedicine(treatmentMedicineDtoCreate);
    }

}
