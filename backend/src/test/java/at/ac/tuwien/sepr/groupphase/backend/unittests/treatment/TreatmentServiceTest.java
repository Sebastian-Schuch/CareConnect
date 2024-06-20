package at.ac.tuwien.sepr.groupphase.backend.unittests.treatment;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TreatmentMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Treatment;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.TreatmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@ActiveProfiles({"test", "datagen"})
public class TreatmentServiceTest {

    @Mock
    private TreatmentRepository treatmentRepository;

    @Mock
    private TreatmentMapper treatmentMapper;

    @InjectMocks
    private TreatmentServiceImpl treatmentService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    @DisplayName("createTreatment: valid treatment dto - expect success")
    void shouldCreateTreatment_whenGivenValidTreatmentDto() {
        TreatmentDtoCreate treatmentDtoCreate = mock(TreatmentDtoCreate.class);
        Treatment treatment = mock(Treatment.class);
        TreatmentDto treatmentDto = mock(TreatmentDto.class);

        when(treatmentMapper.dtoToEntity(any(TreatmentDtoCreate.class))).thenReturn(treatment);
        when(treatmentRepository.save(any(Treatment.class))).thenReturn(treatment);
        when(treatmentMapper.entityToDto(any(Treatment.class))).thenReturn(treatmentDto);

        // call createTreatment() method
        TreatmentDto createdTreatmentDto = treatmentService.createTreatment(treatmentDtoCreate);

        assertNotNull(createdTreatmentDto);
        verify(treatmentRepository, times(1)).save(treatment);
        verify(treatmentMapper, times(1)).dtoToEntity(treatmentDtoCreate);
        verify(treatmentMapper, times(1)).entityToDto(treatment);
    }

    @Test
    @DisplayName("updateTreatment: valid treatment dto - expect success")
    void shouldUpdateTreatment_whenGivenValidTreatmentDto() throws NotFoundException {
        Long id = 1L;
        TreatmentDtoCreate treatmentDtoCreate = mock(TreatmentDtoCreate.class);
        Treatment treatment = mock(Treatment.class);
        TreatmentDto treatmentDto = mock(TreatmentDto.class);

        when(treatmentMapper.dtoToEntity(any(TreatmentDtoCreate.class))).thenReturn(treatment);
        when(treatmentRepository.save(any(Treatment.class))).thenReturn(treatment);
        when(treatmentMapper.entityToDto(any(Treatment.class))).thenReturn(treatmentDto);

        /// call updateTreatment() method
        TreatmentDto updatedTreatmentDto = treatmentService.updateTreatment(id, treatmentDtoCreate);

        assertNotNull(updatedTreatmentDto);
        verify(treatmentRepository, times(1)).save(treatment);
        verify(treatmentMapper, times(1)).dtoToEntity(treatmentDtoCreate);
        verify(treatmentMapper, times(1)).entityToDto(treatment);
    }

    @Test
    @DisplayName("getTreatmentById: valid id - expect success")
    void shouldReturnTreatment_whenGivenValidId() throws NotFoundException {
        Long id = 1L;
        Treatment treatment = mock(Treatment.class);
        TreatmentDto treatmentDto = mock(TreatmentDto.class);

        when(treatmentRepository.findById(id)).thenReturn(Optional.of(treatment));
        when(treatmentMapper.entityToDto(any(Treatment.class))).thenReturn(treatmentDto);

        // call getTreatmentById() method
        TreatmentDto foundTreatmentDto = treatmentService.getTreatmentById(id);

        assertNotNull(foundTreatmentDto);
        verify(treatmentRepository, times(1)).findById(id);
        verify(treatmentMapper, times(1)).entityToDto(treatment);
    }

    @Test
    @DisplayName("searchTreatments: valid searchParams - expect success")
    void shouldReturnTreatments_whenGivenValidSearchParams() {
        Long patientId = 1L;
        int size = 10;
        int page = 0;
        String startDate = "2021-01-01T";
        String endDate = "2021-12-31T";
        String treatmentTitle = "title";
        String medicationName = "medication";
        String doctorName = "doctor";
        String patientName = "patient";
        String svnr = "svnr";
        String departmentName = "department";

        TreatmentDtoSearch searchParams = mock(TreatmentDtoSearch.class);
        TreatmentPageDto treatmentPageDto = mock(TreatmentPageDto.class);
        Page<Treatment> treatment = mock(Page.class);

        when(treatmentRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(treatment);
        when(treatmentMapper.toTreatmentPageDto(any(Page.class))).thenReturn(treatmentPageDto);
        when(searchParams.patientId()).thenReturn(patientId);
        when(searchParams.size()).thenReturn(size);
        when(searchParams.page()).thenReturn(page);
        when(searchParams.startDate()).thenReturn(startDate);
        when(searchParams.endDate()).thenReturn(endDate);
        when(searchParams.treatmentTitle()).thenReturn(treatmentTitle);
        when(searchParams.medicationName()).thenReturn(medicationName);
        when(searchParams.doctorName()).thenReturn(doctorName);
        when(searchParams.patientName()).thenReturn(patientName);
        when(searchParams.svnr()).thenReturn(svnr);
        when(searchParams.departmentName()).thenReturn(departmentName);
        when(treatmentPageDto.treatments()).thenReturn(Arrays.asList(mock(TreatmentDto.class), mock(TreatmentDto.class)));
        when(treatmentPageDto.totalItems()).thenReturn(2);

        // call getTreatmentById() method
        TreatmentPageDto foundTreatments = treatmentService.searchTreatments(searchParams);

        assertNotNull(foundTreatments);
        verify(treatmentRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(treatmentMapper, times(1)).toTreatmentPageDto(any(Page.class));
    }

    @Test
    @DisplayName("getTreatmentById: invalid id - expect NotFoundException")
    void shouldThrowNotFoundException_whenGivenInvalidId() {
        Long id = 999L;
        when(treatmentRepository.findById(id)).thenReturn(Optional.empty());

        // call getTreatmentById() method and expect NotFoundException
        assertThrows(NotFoundException.class, () -> treatmentService.getTreatmentById(id));
        verify(treatmentRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("getTreatmentEntityById: valid id - expect success")
    void shouldReturnTreatmentEntity_whenGivenValidId() throws NotFoundException {
        Long id = 1L;
        Treatment treatment = mock(Treatment.class);

        when(treatmentRepository.findById(id)).thenReturn(Optional.of(treatment));

        // call getTreatmentEntityById() method
        Treatment foundTreatment = treatmentService.getTreatmentEntityById(id);

        assertNotNull(foundTreatment);
        verify(treatmentRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("getTreatmentEntityById: invalid id - expect NotFoundException")
    void shouldThrowNotFoundException_whenGivenInvalidIdForEntity() {
        Long id = 999L;
        when(treatmentRepository.findById(id)).thenReturn(Optional.empty());

        // call getTreatmentEntityById() method and expect NotFoundException
        assertThrows(NotFoundException.class, () -> treatmentService.getTreatmentEntityById(id));
        verify(treatmentRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("getAllTreatmentsFromPatient: valid patient id - expect success")
    void shouldReturnAllTreatmentsForPatient_whenGivenValidPatientId() {
        Long patientId = 1L;
        List<Treatment> treatments = Arrays.asList(mock(Treatment.class), mock(Treatment.class));
        List<TreatmentDto> treatmentDtos = Arrays.asList(mock(TreatmentDto.class), mock(TreatmentDto.class));

        when(treatmentRepository.findByPatient_PatientId(patientId)).thenReturn(treatments);
        when(treatmentMapper.entityListToDtoList(treatments)).thenReturn(treatmentDtos);

        // call getAllTreatmentsFromPatient() method
        List<TreatmentDto> foundTreatments = treatmentService.getAllTreatmentsFromPatient(patientId);

        assertNotNull(foundTreatments);
        assertEquals(2, foundTreatments.size());
        verify(treatmentRepository, times(1)).findByPatient_PatientId(patientId);
        verify(treatmentMapper, times(1)).entityListToDtoList(treatments);
    }

    @Test
    @DisplayName("getAllTreatmentsFromDoctor: valid doctor id - expect success")
    void shouldReturnAllTreatmentsForDoctor_whenGivenValidDoctorId() {
        Long doctorId = 1L;
        List<Treatment> treatments = Arrays.asList(mock(Treatment.class), mock(Treatment.class));
        List<TreatmentDto> treatmentDtos = Arrays.asList(mock(TreatmentDto.class), mock(TreatmentDto.class));

        when(treatmentRepository.findByDoctors_DoctorId(doctorId)).thenReturn(treatments);
        when(treatmentMapper.entityListToDtoList(treatments)).thenReturn(treatmentDtos);

        // call getAllTreatmentsFromDoctor() method
        List<TreatmentDto> foundTreatments = treatmentService.getAllTreatmentsFromDoctor(doctorId);

        assertNotNull(foundTreatments);
        assertEquals(2, foundTreatments.size());
        verify(treatmentRepository, times(1)).findByDoctors_DoctorId(doctorId);
        verify(treatmentMapper, times(1)).entityListToDtoList(treatments);
    }

}
