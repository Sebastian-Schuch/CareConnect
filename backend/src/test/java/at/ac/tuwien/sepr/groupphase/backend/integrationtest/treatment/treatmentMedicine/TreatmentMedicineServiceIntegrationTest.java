package at.ac.tuwien.sepr.groupphase.backend.integrationtest.treatment.treatmentMedicine;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.integrationtest.treatment.util.TreatmentTestUtils;
import at.ac.tuwien.sepr.groupphase.backend.service.TreatmentMedicineService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.MedicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@ActiveProfiles({"test", "datagen"})
public class TreatmentMedicineServiceIntegrationTest extends TestBase {


    @Autowired
    private TreatmentMedicineService treatmentMedicineService;

    @Autowired
    private MedicationServiceImpl medicationService;

    @Autowired
    private TreatmentTestUtils treatmentTestUtils;

    private TreatmentMedicineDtoCreate validTreatmentMedicineDtoCreate;
    private TreatmentMedicineDtoCreate validTreatmentMedicineDtoCreate2;

    public TreatmentMedicineServiceIntegrationTest() {
        super("treatmentMedicine");
    }

    @BeforeEach
    void createTestData() {
        MedicationDto medication1 = medicationService.getAllMedications().get(1);
        MedicationDto medication2 = medicationService.getAllMedications().get(2);
        Date treatmentMedicationDate1 = treatmentTestUtils.createDate(2022, Calendar.JANUARY, 1, 10, 10);
        Date treatmentMedicationDate2 = treatmentTestUtils.createDate(2022, Calendar.FEBRUARY, 1, 10, 10);
        long amount = 200L;
        String unitOfMeasurement = "mg";

        validTreatmentMedicineDtoCreate = new TreatmentMedicineDtoCreate(
            medication1,
            unitOfMeasurement,
            amount,
            treatmentMedicationDate1
        );
        validTreatmentMedicineDtoCreate2 = new TreatmentMedicineDtoCreate(
            medication2,
            unitOfMeasurement,
            amount,
            treatmentMedicationDate2
        );
    }

    // *** createTreatmentMedicine: valid valid treatment medicine dto ***

    @Test
    @DisplayName("createTreatmentMedicine: valid treatment medicine dto - expect success")
    void testCreateTreatmentMedicine_givenValidDto_expectSuccess() {
        TreatmentMedicineDto createdTreatmentMedicine = treatmentMedicineService.createTreatmentMedicine(validTreatmentMedicineDtoCreate);

        assertNotNull(createdTreatmentMedicine);
        assertAll("Verify treatment medicine properties",
            () -> assertEquals(validTreatmentMedicineDtoCreate.medication().id(), createdTreatmentMedicine.medication().id()),
            () -> assertEquals(validTreatmentMedicineDtoCreate.medicineAdministrationDate(), createdTreatmentMedicine.medicineAdministrationDate()),
            () -> assertEquals(validTreatmentMedicineDtoCreate.amount(), createdTreatmentMedicine.amount()),
            () -> assertEquals(validTreatmentMedicineDtoCreate.unitOfMeasurement(), createdTreatmentMedicine.unitOfMeasurement())
        );
    }

    // *** getTreatmentMedicineById: create treatmentMedicine and then search it by id ***
    @Test
    @DisplayName("getTreatmentMedicineById: valid treatment medicine id - expect success")
    void testGetTreatmentMedicineById_givenValidId_expectSuccess() throws NotFoundException {
        TreatmentMedicineDto createdTreatmentMedicine = treatmentMedicineService.createTreatmentMedicine(validTreatmentMedicineDtoCreate);
        TreatmentMedicineDto foundTreatmentMedicine = treatmentMedicineService.getTreatmentMedicineById(createdTreatmentMedicine.id());

        assertNotNull(foundTreatmentMedicine);
        assertAll("Verify found treatment medicine properties",
            () -> assertEquals(createdTreatmentMedicine.id(), foundTreatmentMedicine.id()),
            () -> assertEquals(createdTreatmentMedicine.medication().id(), foundTreatmentMedicine.medication().id()),
            () -> assertEquals(createdTreatmentMedicine.medicineAdministrationDate(), foundTreatmentMedicine.medicineAdministrationDate()),
            () -> assertEquals(createdTreatmentMedicine.amount(), foundTreatmentMedicine.amount()),
            () -> assertEquals(createdTreatmentMedicine.unitOfMeasurement(), foundTreatmentMedicine.unitOfMeasurement())
        );
    }

    // ***  getTreatmentMedicineById: invalid treatment medicine id - expect NotFoundException ***

    @Test
    @DisplayName("getTreatmentMedicineById: invalid treatment medicine id - expect NotFoundException")
    void testGetTreatmentMedicineById_givenInvalidId_ThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> treatmentMedicineService.getTreatmentMedicineById(999L));
    }

    // *** deleteTreatmentMedicine: create treatmentMedicine and then delete it by id ***
    @Test
    @DisplayName("deleteTreatmentMedicine: valid treatment medicine id - expect success")
    void testDeleteTreatmentMedicine_givenValidId_expectSuccess() throws NotFoundException {
        TreatmentMedicineDto createdTreatmentMedicine = treatmentMedicineService.createTreatmentMedicine(validTreatmentMedicineDtoCreate);
        assertDoesNotThrow(() -> treatmentMedicineService.deleteTreatmentMedicine(createdTreatmentMedicine.id()));
        assertThrows(NotFoundException.class, () -> treatmentMedicineService.getTreatmentMedicineById(createdTreatmentMedicine.id()));
    }

    // *** deleteTreatmentMedicine: invalid treatment medicine id - expect NotFoundException ***
    @Test
    @DisplayName("deleteTreatmentMedicine: invalid treatment medicine id - expect NotFoundException")
    void testDeleteTreatmentMedicine_givenInvalidId_ThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> treatmentMedicineService.deleteTreatmentMedicine(999L));
    }

    // *** getAllTreatmentMedicines: create multiple treatment medicines and then get all treatment medicines ***
    @Test
    @DisplayName("getAllTreatmentMedicines: expect success")
    void testGetAllTreatmentMedicines_expectSuccess() {

        treatmentMedicineService.createTreatmentMedicine(validTreatmentMedicineDtoCreate);
        treatmentMedicineService.createTreatmentMedicine(validTreatmentMedicineDtoCreate2);

        List<TreatmentMedicineDto> treatmentMedicines = treatmentMedicineService.getAllTreatmentMedicines();

        assertNotNull(treatmentMedicines);
        assertFalse(treatmentMedicines.isEmpty());
        assertTrue(treatmentMedicines.size() >= 2);
    }

    // *** getTreatmentMedicineEntityById: create treatmentMedicine and then search it by id ***
    @Test
    @DisplayName("getTreatmentMedicineEntityById: valid treatment medicine id - expect success")
    void testGetTreatmentMedicineEntityById_givenValidId_expectSuccess() throws NotFoundException {
        TreatmentMedicineDto createdTreatmentMedicine = treatmentMedicineService.createTreatmentMedicine(validTreatmentMedicineDtoCreate);
        TreatmentMedicine foundTreatmentMedicine = treatmentMedicineService.getTreatmentMedicineEntityById(createdTreatmentMedicine.id());

        assertNotNull(foundTreatmentMedicine);
        assertAll("Verify found treatment medicine entity properties",
            () -> assertEquals(createdTreatmentMedicine.id(), foundTreatmentMedicine.getId()),
            () -> assertEquals(createdTreatmentMedicine.medication().id(), foundTreatmentMedicine.getMedicine().getId()),
            () -> assertEquals(createdTreatmentMedicine.medicineAdministrationDate(), foundTreatmentMedicine.getTimeOfAdministration()),
            () -> assertEquals(createdTreatmentMedicine.amount(), foundTreatmentMedicine.getAmount()),
            () -> assertEquals(createdTreatmentMedicine.unitOfMeasurement(), foundTreatmentMedicine.getUnitOfMeasurement())
        );
    }

    // *** getTreatmentMedicineEntityById: invalid treatment medicine id - expect NotFoundException ***
    @Test
    @DisplayName("getTreatmentMedicineEntityById: invalid treatment medicine id - expect NotFoundException")
    void testGetTreatmentMedicineEntityById_givenInvalidId_ThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> treatmentMedicineService.getTreatmentMedicineEntityById(999L));
    }


}
