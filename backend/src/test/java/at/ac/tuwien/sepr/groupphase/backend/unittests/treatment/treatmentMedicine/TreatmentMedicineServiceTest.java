package at.ac.tuwien.sepr.groupphase.backend.unittests.treatment.treatmentMedicine;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TreatmentMedicineMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentMedicineRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.TreatmentMedicineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TreatmentMedicineServiceTest {

    @Mock
    private TreatmentMedicineRepository treatmentMedicineRepository;

    @Mock
    private TreatmentMedicineMapper treatmentMedicineMapper;

    @InjectMocks
    private TreatmentMedicineServiceImpl treatmentMedicineService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("createTreatmentMedicine: valid TreatmentMedicineDtoCreate - expect success")
    void shouldCreateTreatmentMedicine_whenGivenValidDtoCreate() {

        TreatmentMedicineDtoCreate dtoCreate = mock(TreatmentMedicineDtoCreate.class);
        TreatmentMedicine treatmentMedicine = mock(TreatmentMedicine.class);
        TreatmentMedicineDto treatmentMedicineDto = mock(TreatmentMedicineDto.class);

        when(treatmentMedicineMapper.dtoToEntity(any(TreatmentMedicineDtoCreate.class))).thenReturn(treatmentMedicine);
        when(treatmentMedicineRepository.save(any(TreatmentMedicine.class))).thenReturn(treatmentMedicine);
        when(treatmentMedicineMapper.entityToDto(any(TreatmentMedicine.class))).thenReturn(treatmentMedicineDto);

        TreatmentMedicineDto result = treatmentMedicineService.createTreatmentMedicine(dtoCreate);

        assertNotNull(result);
        verify(treatmentMedicineRepository, times(1)).save(treatmentMedicine);
        verify(treatmentMedicineMapper, times(1)).dtoToEntity(dtoCreate);
        verify(treatmentMedicineMapper, times(1)).entityToDto(treatmentMedicine);
    }

    @Test
    @DisplayName("getTreatmentMedicineById: valid id - expect success")
    void shouldReturnTreatmentMedicine_whenGivenValidId() throws NotFoundException {
        long id = 1L;
        TreatmentMedicine treatmentMedicine = mock(TreatmentMedicine.class);
        TreatmentMedicineDto treatmentMedicineDto = mock(TreatmentMedicineDto.class);

        when(treatmentMedicineRepository.findById(id)).thenReturn(Optional.of(treatmentMedicine));
        when(treatmentMedicineMapper.entityToDto(any(TreatmentMedicine.class))).thenReturn(treatmentMedicineDto);

        TreatmentMedicineDto result = treatmentMedicineService.getTreatmentMedicineById(id);

        assertNotNull(result);
        verify(treatmentMedicineRepository, times(1)).findById(id);
        verify(treatmentMedicineMapper, times(1)).entityToDto(treatmentMedicine);
    }

    @Test
    @DisplayName("getTreatmentMedicineById: invalid id - expect NotFoundException")
    void shouldThrowNotFoundException_whenGivenInvalidId() {
        long id = 999L;

        when(treatmentMedicineRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> treatmentMedicineService.getTreatmentMedicineById(id));
        verify(treatmentMedicineRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("getTreatmentMedicineEntityById: valid id - expect success")
    void shouldReturnTreatmentMedicineEntity_whenGivenValidId() throws NotFoundException {
        long id = 1L;
        TreatmentMedicine treatmentMedicine = mock(TreatmentMedicine.class);

        when(treatmentMedicineRepository.findById(id)).thenReturn(Optional.of(treatmentMedicine));

        TreatmentMedicine result = treatmentMedicineService.getTreatmentMedicineEntityById(id);

        assertNotNull(result);
        verify(treatmentMedicineRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("getTreatmentMedicineEntityById: invalid id - expect NotFoundException")
    void shouldThrowNotFoundException_whenGivenInvalidIdForEntity() {
        long id = 999L;

        when(treatmentMedicineRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> treatmentMedicineService.getTreatmentMedicineEntityById(id));
        verify(treatmentMedicineRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("deleteTreatmentMedicine: valid id - expect success")
    void shouldDeleteTreatmentMedicine_whenGivenValidId() throws NotFoundException {
        long id = 1L;

        when(treatmentMedicineRepository.existsById(id)).thenReturn(true);

        treatmentMedicineService.deleteTreatmentMedicine(id);

        verify(treatmentMedicineRepository, times(1)).existsById(id);
        verify(treatmentMedicineRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("deleteTreatmentMedicine: invalid id - expect NotFoundException")
    void shouldThrowNotFoundException_whenGivenInvalidIdForDeletion() {
        long id = 999L;

        when(treatmentMedicineRepository.existsById(id)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> treatmentMedicineService.deleteTreatmentMedicine(id));
        verify(treatmentMedicineRepository, times(1)).existsById(id);
    }

    @Test
    @DisplayName("getAllTreatmentMedicines: expect success")
    void shouldReturnAllTreatmentMedicines() {
        TreatmentMedicine treatmentMedicine1 = mock(TreatmentMedicine.class);
        TreatmentMedicine treatmentMedicine2 = mock(TreatmentMedicine.class);
        TreatmentMedicineDto treatmentMedicineDto1 = mock(TreatmentMedicineDto.class);
        TreatmentMedicineDto treatmentMedicineDto2 = mock(TreatmentMedicineDto.class);

        when(treatmentMedicineRepository.findAll()).thenReturn(Arrays.asList(treatmentMedicine1, treatmentMedicine2));
        when(treatmentMedicineMapper.entityListToDtoList(anyList())).thenReturn(Arrays.asList(treatmentMedicineDto1, treatmentMedicineDto2));

        List<TreatmentMedicineDto> result = treatmentMedicineService.getAllTreatmentMedicines();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(treatmentMedicineRepository, times(1)).findAll();
        verify(treatmentMedicineMapper, times(1)).entityListToDtoList(anyList());
    }
}
