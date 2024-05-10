package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MedicationRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.MedicationService;
import org.junit.jupiter.api.BeforeEach;
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
@ActiveProfiles("test")
public class MedicationServiceTest {

    @Autowired
    private MedicationService medicationService;

    @Autowired
    MedicationRepository medicationRepository;

    @BeforeEach
    void clean() {
        medicationRepository.deleteAll();
    }

    @Test
    public void givenNewlyCreatedMedication_whenGettingMedicationWithId_thenReturnMedication() {
        MedicationCreateDto createMedication = new MedicationCreateDto("WieAgra");
        MedicationDto createdMedication = medicationService.create(createMedication);
        MedicationDto foundMedication = medicationService.getById(createdMedication.id());
        assertAll("Grouped Assertions of Medication",
            () -> assertEquals(createdMedication.id(), foundMedication.id(), "ID should be equal"),
            () -> assertEquals(createdMedication.name(), foundMedication.name(), "Name should be equal"),
            () -> assertEquals(createdMedication.active(), foundMedication.active(), "Active should be equal"));
    }

    @Test
    public void givenIdOfNonExistentMedication_whenGettingMedicationWithId_thenThrowNotFoundException() {
        long id = -1;
        Exception e = assertThrows(NotFoundException.class, () -> medicationService.getById(id));
        assertEquals("Medication not found", e.getMessage());
    }

    @Test
    public void givenMedicationCreateDto_whenCreatingNewMedication_thenReturnNewlyCreated() {
        MedicationCreateDto createMedication = new MedicationCreateDto("WieAgra");
        MedicationDto createdMedication = medicationService.create(createMedication);
        assertAll("Grouped Assertions of Medication",
            () -> assertEquals(createdMedication.name(), createMedication.name(), "Name should be equal"));
    }

    @Test
    public void givenNoMedicationsInDatabase_whenGettingAllMedications_thenReturnEmptyList() {
        List<MedicationDto> allMedications = medicationService.getAllMedications();
        assertEquals(0, allMedications.size());
    }

    @Test
    public void givenThreeNewlyCreatedMedicationsInDatabase_whenGettingAllMedications_thenReturnThreeMedications() {
        MedicationCreateDto createMedicine = new MedicationCreateDto("WieAgra");
        MedicationDto createdMedicine1 = medicationService.create(createMedicine);
        MedicationDto createdMedicine2 = medicationService.create(createMedicine);
        MedicationDto createdMedicine3 = medicationService.create(createMedicine);
        assertAll("Grouped Assertions of Medication",
            () -> assertEquals(createMedicine.name(), createdMedicine1.name(), "Name should be equal"),

            () -> assertEquals(createMedicine.name(), createdMedicine2.name(), "Name should be equal"),

            () -> assertEquals(createMedicine.name(), createdMedicine3.name(), "Name should be equal"));
    }
}
