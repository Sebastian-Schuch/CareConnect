package at.ac.tuwien.sepr.groupphase.backend.unittests.treatment.treatmentMedicine;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TreatmentMedicineMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.MedicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TreatmentMedicineMapperTest {

    @Mock
    private MedicationServiceImpl medicationService;

    @InjectMocks
    private TreatmentMedicineMapper treatmentMedicineMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("dtoToEntity: valid TreatmentMedicineDtoCreate - expect success")
    void shouldConvertDtoToEntity_whenGivenValidDtoCreate() {
        Medication medication = new Medication();
        medication.setId(1L);
        medication.setName("Med1");
        medication.setActive(true);

        TreatmentMedicineDtoCreate dtoCreate = new TreatmentMedicineDtoCreate(
            new MedicationDto(1L, "Med1", true),
            "mg",
            100,
            new Date()
        );

        when(medicationService.getEntityById(anyLong())).thenReturn(medication);

        TreatmentMedicine treatmentMedicine = treatmentMedicineMapper.dtoToEntity(dtoCreate);

        assertNotNull(treatmentMedicine);
        assertAll("Verify treatment medicine properties",
            () -> assertEquals(medication, treatmentMedicine.getMedicine()),
            () -> assertEquals(dtoCreate.amount(), treatmentMedicine.getAmount()),
            () -> assertEquals(dtoCreate.unitOfMeasurement(), treatmentMedicine.getUnitOfMeasurement()),
            () -> assertEquals(dtoCreate.medicineAdministrationDate(), treatmentMedicine.getTimeOfAdministration())
        );

        verify(medicationService, times(1)).getEntityById(1L);
    }

    @Test
    @DisplayName("entityToDto: valid TreatmentMedicine - expect success")
    void shouldConvertEntityToDto_whenGivenValidEntity() {
        Medication medication = new Medication();
        medication.setId(1L);
        medication.setName("Med1");
        medication.setActive(true);

        TreatmentMedicine treatmentMedicine = new TreatmentMedicine();
        treatmentMedicine.setId(1L);
        treatmentMedicine.setMedicine(medication);
        treatmentMedicine.setAmount(100L);
        treatmentMedicine.setUnitOfMeasurement("mg");
        treatmentMedicine.setTimeOfAdministration(new Date());

        TreatmentMedicineDto treatmentMedicineDto = treatmentMedicineMapper.entityToDto(treatmentMedicine);

        assertNotNull(treatmentMedicineDto);
        assertAll("Verify treatment medicine DTO properties",
            () -> assertEquals(treatmentMedicine.getId(), treatmentMedicineDto.id()),
            () -> assertEquals(treatmentMedicine.getMedicine().getId(), treatmentMedicineDto.medication().id()),
            () -> assertEquals(treatmentMedicine.getMedicine().getName(), treatmentMedicineDto.medication().name()),
            () -> assertEquals(treatmentMedicine.getMedicine().getActive(), treatmentMedicineDto.medication().active()),
            () -> assertEquals(treatmentMedicine.getAmount(), treatmentMedicineDto.amount()),
            () -> assertEquals(treatmentMedicine.getUnitOfMeasurement(), treatmentMedicineDto.unitOfMeasurement()),
            () -> assertEquals(treatmentMedicine.getTimeOfAdministration(), treatmentMedicineDto.medicineAdministrationDate())
        );
    }

    @Test
    @DisplayName("entityListToDtoList: valid TreatmentMedicine list - expect success")
    void shouldConvertEntityListToDtoList_whenGivenValidEntityList() {

        Medication medication1 = new Medication();
        medication1.setId(1L);
        medication1.setName("Med1");
        medication1.setActive(true);

        Medication medication2 = new Medication();
        medication2.setId(2L);
        medication2.setName("Med2");
        medication2.setActive(true);

        TreatmentMedicine treatmentMedicine1 = new TreatmentMedicine();
        treatmentMedicine1.setId(1L);
        treatmentMedicine1.setMedicine(medication1);
        treatmentMedicine1.setAmount(100L);
        treatmentMedicine1.setUnitOfMeasurement("mg");
        treatmentMedicine1.setTimeOfAdministration(new Date());

        TreatmentMedicine treatmentMedicine2 = new TreatmentMedicine();
        treatmentMedicine2.setId(2L);
        treatmentMedicine2.setMedicine(medication2);
        treatmentMedicine2.setAmount(200L);
        treatmentMedicine2.setUnitOfMeasurement("ml");
        treatmentMedicine2.setTimeOfAdministration(new Date());

        List<TreatmentMedicine> treatmentMedicines = Arrays.asList(treatmentMedicine1, treatmentMedicine2);

        List<TreatmentMedicineDto> treatmentMedicineDtos = treatmentMedicineMapper.entityListToDtoList(treatmentMedicines);

        assertNotNull(treatmentMedicineDtos);
        assertEquals(2, treatmentMedicineDtos.size());

        assertAll("Verify treatment medicine DTO properties for first entity",
            () -> assertEquals(treatmentMedicine1.getId(), treatmentMedicineDtos.get(0).id()),
            () -> assertEquals(treatmentMedicine1.getMedicine().getId(), treatmentMedicineDtos.get(0).medication().id()),
            () -> assertEquals(treatmentMedicine1.getMedicine().getName(), treatmentMedicineDtos.get(0).medication().name()),
            () -> assertEquals(treatmentMedicine1.getMedicine().getActive(), treatmentMedicineDtos.get(0).medication().active()),
            () -> assertEquals(treatmentMedicine1.getAmount(), treatmentMedicineDtos.get(0).amount()),
            () -> assertEquals(treatmentMedicine1.getUnitOfMeasurement(), treatmentMedicineDtos.get(0).unitOfMeasurement()),
            () -> assertEquals(treatmentMedicine1.getTimeOfAdministration(), treatmentMedicineDtos.get(0).medicineAdministrationDate())
        );

        assertAll("Verify treatment medicine DTO properties for second entity",
            () -> assertEquals(treatmentMedicine2.getId(), treatmentMedicineDtos.get(1).id()),
            () -> assertEquals(treatmentMedicine2.getMedicine().getId(), treatmentMedicineDtos.get(1).medication().id()),
            () -> assertEquals(treatmentMedicine2.getMedicine().getName(), treatmentMedicineDtos.get(1).medication().name()),
            () -> assertEquals(treatmentMedicine2.getMedicine().getActive(), treatmentMedicineDtos.get(1).medication().active()),
            () -> assertEquals(treatmentMedicine2.getAmount(), treatmentMedicineDtos.get(1).amount()),
            () -> assertEquals(treatmentMedicine2.getUnitOfMeasurement(), treatmentMedicineDtos.get(1).unitOfMeasurement()),
            () -> assertEquals(treatmentMedicine2.getTimeOfAdministration(), treatmentMedicineDtos.get(1).medicineAdministrationDate())
        );
    }
}
