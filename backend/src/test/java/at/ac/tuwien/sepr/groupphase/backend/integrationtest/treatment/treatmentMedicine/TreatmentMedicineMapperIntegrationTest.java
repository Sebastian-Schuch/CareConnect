package at.ac.tuwien.sepr.groupphase.backend.integrationtest.treatment.treatmentMedicine;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TreatmentMedicineMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.integrationtest.treatment.util.TreatmentTestUtils;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentMedicineRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.MedicationService;
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
public class TreatmentMedicineMapperIntegrationTest extends TestBase {

    @Autowired
    private TreatmentMedicineMapper treatmentMedicineMapper;

    @Autowired
    private TreatmentMedicineRepository treatmentMedicineRepository;

    @Autowired
    private MedicationService medicationService;

    @Autowired
    private TreatmentTestUtils treatmentTestUtils;

    private TreatmentMedicineDtoCreate validTreatmentMedicineDtoCreate;
    private TreatmentMedicine validTreatmentMedicineEntity;
    private TreatmentMedicine validTreatmentMedicineEntity2;

    public TreatmentMedicineMapperIntegrationTest() {
        super("treatmentMedicine");
    }

    @BeforeEach
    void createTestData() {
        MedicationDto medicationDto = medicationService.getAllMedications().getFirst();
        Date treatmentMedicationDate1 = treatmentTestUtils.createDate(2022, Calendar.JANUARY, 1, 10, 10);

        validTreatmentMedicineDtoCreate = new TreatmentMedicineDtoCreate(
            medicationDto,
            "mg",
            100,
            treatmentMedicationDate1
        );

        validTreatmentMedicineEntity = treatmentMedicineRepository.findAll().getFirst();
        validTreatmentMedicineEntity2 = treatmentMedicineRepository.findAll().get(1);
    }

    @Test
    @DisplayName("dtoToEntity: valid TreatmentMedicineDtoCreate - expect success")
    void testDtoToEntity_givenValidDtoCreate_expectSuccess() {
        TreatmentMedicine treatmentMedicine = treatmentMedicineMapper.dtoToEntity(validTreatmentMedicineDtoCreate);

        assertNotNull(treatmentMedicine);
        assertAll("Verify treatment medicine properties",
            () -> assertNull(treatmentMedicine.getId()),
            () -> assertEquals(validTreatmentMedicineDtoCreate.medication().id(), treatmentMedicine.getMedicine().getId()),
            () -> assertEquals(validTreatmentMedicineDtoCreate.amount(), treatmentMedicine.getAmount()),
            () -> assertEquals(validTreatmentMedicineDtoCreate.unitOfMeasurement(), treatmentMedicine.getUnitOfMeasurement()),
            () -> assertEquals(validTreatmentMedicineDtoCreate.medicineAdministrationDate(), treatmentMedicine.getTimeOfAdministration())
        );
    }

    @Test
    @DisplayName("entityToDto: valid TreatmentMedicine entity - expect success")
    void testEntityToDto_givenValidEntity_expectSuccess() {
        TreatmentMedicineDto treatmentMedicineDto = treatmentMedicineMapper.entityToDto(validTreatmentMedicineEntity);

        assertNotNull(treatmentMedicineDto);
        assertAll("Verify treatment medicine DTO properties",
            () -> assertEquals(validTreatmentMedicineEntity.getId(), treatmentMedicineDto.id()),
            () -> assertEquals(validTreatmentMedicineEntity.getMedicine().getId(), treatmentMedicineDto.medication().id()),
            () -> assertEquals(validTreatmentMedicineEntity.getAmount(), treatmentMedicineDto.amount()),
            () -> assertEquals(validTreatmentMedicineEntity.getUnitOfMeasurement(), treatmentMedicineDto.unitOfMeasurement()),
            () -> assertEquals(validTreatmentMedicineEntity.getTimeOfAdministration(), treatmentMedicineDto.medicineAdministrationDate())
        );
    }

    @Test
    @DisplayName("entityListToDtoList: valid TreatmentMedicine entity list - expect success")
    void testEntityListToDtoList_givenValidEntityList_expectSuccess() {
        List<TreatmentMedicine> treatmentMedicineList = List.of(validTreatmentMedicineEntity, validTreatmentMedicineEntity2);
        List<TreatmentMedicineDto> treatmentMedicineDtoList = treatmentMedicineMapper.entityListToDtoList(treatmentMedicineList);

        assertNotNull(treatmentMedicineDtoList);
        assertEquals(2, treatmentMedicineDtoList.size());
        assertAll("Verify treatment medicine DTO list properties",
            () -> assertEquals(validTreatmentMedicineEntity.getId(), treatmentMedicineDtoList.getFirst().id()),
            () -> assertEquals(validTreatmentMedicineEntity.getMedicine().getId(), treatmentMedicineDtoList.getFirst().medication().id()),
            () -> assertEquals(validTreatmentMedicineEntity.getAmount(), treatmentMedicineDtoList.getFirst().amount()),
            () -> assertEquals(validTreatmentMedicineEntity.getUnitOfMeasurement(), treatmentMedicineDtoList.getFirst().unitOfMeasurement()),
            () -> assertEquals(validTreatmentMedicineEntity.getTimeOfAdministration(), treatmentMedicineDtoList.getFirst().medicineAdministrationDate()),
            () -> assertEquals(validTreatmentMedicineEntity2.getId(), treatmentMedicineDtoList.get(1).id()),
            () -> assertEquals(validTreatmentMedicineEntity2.getMedicine().getId(), treatmentMedicineDtoList.get(1).medication().id()),
            () -> assertEquals(validTreatmentMedicineEntity2.getAmount(), treatmentMedicineDtoList.get(1).amount()),
            () -> assertEquals(validTreatmentMedicineEntity2.getUnitOfMeasurement(), treatmentMedicineDtoList.get(1).unitOfMeasurement()),
            () -> assertEquals(validTreatmentMedicineEntity2.getTimeOfAdministration(), treatmentMedicineDtoList.get(1).medicineAdministrationDate())
        );
    }

    @Test
    @DisplayName("dtoToEntity: invalid Medication ID in TreatmentMedicineDtoCreate - expect exception")
    void testDtoToEntity_givenInvalidMedicationId_ThrowsException() {
        TreatmentMedicineDtoCreate treatmentMedicineDtoCreate = new TreatmentMedicineDtoCreate(
            new MedicationDto(999L, "Invalid Medication", false),
            validTreatmentMedicineDtoCreate.unitOfMeasurement(),
            validTreatmentMedicineDtoCreate.amount(),
            validTreatmentMedicineDtoCreate.medicineAdministrationDate()
        );

        assertThrows(NotFoundException.class, () -> treatmentMedicineMapper.dtoToEntity(treatmentMedicineDtoCreate));
    }
}
