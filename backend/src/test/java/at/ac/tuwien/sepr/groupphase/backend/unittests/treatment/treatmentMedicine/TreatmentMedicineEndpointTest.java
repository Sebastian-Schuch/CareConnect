package at.ac.tuwien.sepr.groupphase.backend.unittests.treatment.treatmentMedicine;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.TreatmentMedicineEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.TreatmentMedicineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TreatmentMedicineEndpointTest {


    @Mock
    private TreatmentMedicineServiceImpl treatmentMedicineService;

    @InjectMocks
    private TreatmentMedicineEndpoint treatmentMedicineEndpoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("createTreatmentMedicine: valid TreatmentMedicineDtoCreate - expect success")
    @WithMockUser(roles = "DOCTOR")
    void shouldCreateTreatmentMedicine_whenGivenValidDtoCreate() {
        // Arrange
        TreatmentMedicineDtoCreate treatmentMedicineDtoCreate = mock(TreatmentMedicineDtoCreate.class);
        TreatmentMedicineDto treatmentMedicineDto = mock(TreatmentMedicineDto.class);

        when(treatmentMedicineService.createTreatmentMedicine(any(TreatmentMedicineDtoCreate.class)))
            .thenReturn(treatmentMedicineDto);

        TreatmentMedicineDto result = this.treatmentMedicineEndpoint.createTreatmentMedicine(treatmentMedicineDtoCreate);

        assertNotNull(result);
        verify(treatmentMedicineService, times(1)).createTreatmentMedicine(any(TreatmentMedicineDtoCreate.class));
    }

    @Test
    @DisplayName("deleteTreatmentMedicine: valid id - expect success")
    @WithMockUser(roles = "DOCTOR")
    void shouldDeleteTreatmentMedicine_whenGivenValidId() {
        long id = 1L;

        doNothing().when(treatmentMedicineService).deleteTreatmentMedicine(anyLong());
        assertDoesNotThrow(() -> treatmentMedicineEndpoint.deleteTreatmentMedicine(id));

        verify(treatmentMedicineService, times(1)).deleteTreatmentMedicine(id);
    }

    @Test
    @DisplayName("deleteTreatmentMedicine: invalid id - expect NotFoundException")
    @WithMockUser(roles = "DOCTOR")
    void shouldThrowNotFoundException_whenGivenInvalidId() {
        long id = 999L;

        doThrow(new NotFoundException("Treatment medicine not found")).when(treatmentMedicineService).deleteTreatmentMedicine(anyLong());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> treatmentMedicineEndpoint.deleteTreatmentMedicine(id));
        assertEquals("Treatment medicine not found", exception.getMessage());

        verify(treatmentMedicineService, times(1)).deleteTreatmentMedicine(id);
    }

}
