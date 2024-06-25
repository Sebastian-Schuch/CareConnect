package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MedicationRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.MedicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "datagen"})
public class MedicationServiceTest extends TestBase {

    @Autowired
    private MedicationService medicationService;

    @Autowired
    MedicationRepository medicationRepository;

    public MedicationServiceTest() {
        super("medication");
    }


    @Test
    public void givenNewlyCreatedMedication_whenGettingMedicationWithId_thenReturnMedication() {
        MedicationDtoCreate createMedication = new MedicationDtoCreate("WieAgra2", "mg");
        MedicationDto createdMedication = medicationService.create(createMedication);
        MedicationDto foundMedication = medicationService.getById(createdMedication.id());
        assertAll("Grouped Assertions of Medication",
            () -> assertEquals(createdMedication.id(), foundMedication.id(), "ID should be equal"),
            () -> assertEquals(createdMedication.name(), foundMedication.name(), "Name should be equal"),
            () -> assertEquals(createdMedication.active(), foundMedication.active(), "Active should be equal"),
            () -> assertEquals(createdMedication.unitOfMeasurement(), foundMedication.unitOfMeasurement(), "Unit of Measurement should be equal"));
    }

    @Test
    public void givenIdOfNonExistentMedication_whenGettingMedicationWithId_thenThrowNotFoundException() {
        long id = -1;
        Exception e = assertThrows(NotFoundException.class, () -> medicationService.getById(id));
        assertEquals("Medication not found", e.getMessage());
    }

    @Test
    public void givenMedicationCreateDto_whenCreatingNewMedication_thenReturnNewlyCreated() {
        MedicationDtoCreate createMedication = new MedicationDtoCreate("WieAgra2", "mg");
        MedicationDto createdMedication = medicationService.create(createMedication);
        assertAll("Grouped Assertions of Medication",
            () -> assertEquals(createdMedication.name(), createMedication.name(), "Name should be equal"),
            () -> assertEquals(createdMedication.unitOfMeasurement(), createMedication.unitOfMeasurement(), "Unit of Measurement should be equal"));
    }

    @Test
    public void givenNoMedicationsInDatabase_whenGettingAllMedications_thenReturnEmptyList() {
        medicationRepository.deleteAll();
        List<MedicationDto> allMedications = medicationService.getAllMedications();
        assertEquals(0, allMedications.size());
    }
}
