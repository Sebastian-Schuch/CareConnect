package at.ac.tuwien.sepr.groupphase.backend.unittests.treatment;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DoctorMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PatientMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TreatmentMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TreatmentMedicineMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Treatment;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.service.DoctorService;
import at.ac.tuwien.sepr.groupphase.backend.service.OutpatientDepartmentService;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.TreatmentMedicineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TreatmentMapperTest {
    @Mock
    private PatientService patientService;

    @Mock
    private DoctorService doctorService;

    @Mock
    private OutpatientDepartmentService outpatientDepartmentService;

    @Mock
    private TreatmentMedicineService treatmentMedicineService;

    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private TreatmentMedicineMapper treatmentMedicineMapper;

    @InjectMocks
    private TreatmentMapper treatmentMapper;

    private static TreatmentDtoCreate getTreatmentDtoCreate() {
        PatientDtoSparse patientDto = new PatientDtoSparse(1L, "1234567891", List.of(), List.of(), "John", "Doe", "john@email.com", true);
        DoctorDtoSparse doctorDto = new DoctorDtoSparse(2L, "John", "Smith", "john@email.com", false);
        DoctorDtoSparse doctorDto2 = new DoctorDtoSparse(3L, "John", "Smith", "john@email.com", false);
        TreatmentMedicineDto treatmentMedicineDto = new TreatmentMedicineDto(3L, new MedicationDto(1L, "Med1", true, "mg"), 100, new Date());
        TreatmentMedicineDto treatmentMedicineDto2 = new TreatmentMedicineDto(4L, new MedicationDto(1L, "Med1", true, "mg"), 100, new Date());
        OutpatientDepartmentDto outpatientDepartmentDto = new OutpatientDepartmentDto(1L, "Cardiology", "Cardiology Department", 20, null, true);

        return new TreatmentDtoCreate(
            "Treatment Title",
            new Date(),
            new Date(),
            patientDto,
            outpatientDepartmentDto,
            "Treatment Text",
            List.of(doctorDto, doctorDto2),
            List.of(treatmentMedicineDto, treatmentMedicineDto2)
        );
    }

    private static Treatment createTreatment(long id, Patient patient, Doctor doctor, TreatmentMedicine treatmentMedicine,
                                             OutpatientDepartment outpatientDepartment, Date treatmentStart, Date treatmentEnd) {
        Treatment treatment = new Treatment();
        treatment.setId(id);
        treatment.setTreatmentTitle("Treatment Title");
        treatment.setTreatmentText("Treatment Text");
        treatment.setPatient(patient);
        treatment.setDoctors(Collections.singletonList(doctor));
        treatment.setMedicines(Collections.singletonList(treatmentMedicine));
        treatment.setOutpatientDepartment(outpatientDepartment);
        treatment.setTreatmentStart(treatmentStart);
        treatment.setTreatmentEnd(treatmentEnd);
        return treatment;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("dtoToEntity: valid TreatmentDtoCreate - expect success")
    void shouldConvertDtoToEntity_whenGivenValidDtoCreate() {
        Patient patient = new Patient();
        patient.setPatientId(1L);

        Doctor doctor1 = new Doctor();
        doctor1.setDoctorId(2L);

        Doctor doctor2 = new Doctor();
        doctor2.setDoctorId(3L);

        TreatmentMedicine treatmentMedicine = new TreatmentMedicine();
        treatmentMedicine.setId(3L);

        TreatmentMedicine treatmentMedicine2 = new TreatmentMedicine();
        treatmentMedicine2.setId(4L);

        OutpatientDepartment outpatientDepartment = new OutpatientDepartment();
        outpatientDepartment.setId(1L);

        when(patientService.getPatientEntityById(1L)).thenReturn(patient);
        when(doctorService.getDoctorEntityById(2L)).thenReturn(doctor1);
        when(doctorService.getDoctorEntityById(3L)).thenReturn(doctor2);
        when(treatmentMedicineService.getTreatmentMedicineEntityById(3L)).thenReturn(treatmentMedicine);
        when(treatmentMedicineService.getTreatmentMedicineEntityById(4L)).thenReturn(treatmentMedicine2);
        when(outpatientDepartmentService.getOutpatientDepartmentEntityById(1L)).thenReturn(outpatientDepartment);

        TreatmentDtoCreate treatmentDtoCreate = getTreatmentDtoCreate();

        // call dtoToEntity() method
        Treatment treatment = treatmentMapper.dtoToEntity(treatmentDtoCreate);

        List<Long> expectedDoctorIds = treatmentDtoCreate.doctors().stream()
            .map(DoctorDtoSparse::id)
            .toList();
        List<Long> actualDoctorIds = treatment.getDoctors().stream()
            .map(Doctor::getDoctorId)
            .toList();

        List<Long> expectedMedicineIds = treatmentDtoCreate.medicines().stream()
            .map(TreatmentMedicineDto::id)
            .toList();
        List<Long> actualMedicineIds = treatment.getMedicines().stream()
            .map(TreatmentMedicine::getId)
            .toList();

        assertNotNull(treatment);
        assertAll(
            () -> assertEquals("Treatment Title", treatment.getTreatmentTitle()),
            () -> assertEquals("Treatment Text", treatment.getTreatmentText()),
            () -> assertEquals(treatmentDtoCreate.treatmentStart(), treatment.getTreatmentStart()),
            () -> assertEquals(treatmentDtoCreate.treatmentEnd(), treatment.getTreatmentEnd()),
            () -> assertEquals(patient.getPatientId(), treatment.getPatient().getPatientId()),
            () -> assertEquals(outpatientDepartment.getId(), treatment.getOutpatientDepartment().getId()),
            () -> assertEquals(2, treatment.getDoctors().size()),
            () -> assertEquals(2, treatment.getMedicines().size())
        );

        assertAll("Verify doctor IDs",
            () -> assertTrue(actualDoctorIds.containsAll(expectedDoctorIds)),
            () -> assertEquals(expectedDoctorIds.size(), actualDoctorIds.size())
        );

        assertAll("Verify medicine IDs",
            () -> assertTrue(actualMedicineIds.containsAll(expectedMedicineIds)),
            () -> assertEquals(expectedMedicineIds.size(), actualMedicineIds.size())
        );

        verify(patientService, times(1)).getPatientEntityById(1L);
        verify(doctorService, times(1)).getDoctorEntityById(2L);
        verify(doctorService, times(1)).getDoctorEntityById(3L);
        verify(treatmentMedicineService, times(1)).getTreatmentMedicineEntityById(3L);
        verify(treatmentMedicineService, times(1)).getTreatmentMedicineEntityById(4L);
        verify(outpatientDepartmentService, times(1)).getOutpatientDepartmentEntityById(1L);
    }

    @Test
    @DisplayName("entityToDto: valid Treatment - expect success")
    void shouldConvertEntityToDto_whenGivenValidEntity() {
        // Arrange
        Patient patient = new Patient();
        patient.setPatientId(1L);

        Doctor doctor1 = new Doctor();
        doctor1.setDoctorId(2L);

        TreatmentMedicine treatmentMedicine = new TreatmentMedicine();
        treatmentMedicine.setId(3L);

        OutpatientDepartment outpatientDepartment = new OutpatientDepartment();
        outpatientDepartment.setId(1L);

        Treatment treatment = createTreatment(1L, patient, doctor1, treatmentMedicine, outpatientDepartment, new Date(), new Date());
        TreatmentDto expectedTreatmentDto =
            new TreatmentDto(
                1L,
                "Treatment Title",
                new Date(),
                new Date(),
                new PatientDtoSparse(1L, null, null, null, null, null, null, false),
                new OutpatientDepartmentDto(
                    1L, null, null, 0, null, true),
                "Treatment Text",
                Collections.singletonList(new DoctorDtoSparse(2L, null, null, null, false)),
                Collections.singletonList(new TreatmentMedicineDto(3L, null, 0, null))
            );
        when(patientMapper.patientToPatientDtoSparse(patient)).thenReturn(new PatientDtoSparse(1L, null, null, null, null, null, null, true));
        when(doctorMapper.doctorToDoctorDtoSparse(doctor1)).thenReturn(new DoctorDtoSparse(2L, null, null, null, true));
        when(treatmentMedicineMapper.entityToDto(treatmentMedicine)).thenReturn(new TreatmentMedicineDto(3L, null, 0, null));
        when(outpatientDepartmentService.getOutpatientDepartmentById(1L)).thenReturn(new OutpatientDepartmentDto(1L, null, null, 0, null, true));


        TreatmentDto treatmentDto = treatmentMapper.entityToDto(treatment);

        assertNotNull(treatmentDto);
        assertEquals(expectedTreatmentDto.treatmentTitle(), treatmentDto.treatmentTitle());
        assertEquals(expectedTreatmentDto.treatmentText(), treatmentDto.treatmentText());
        assertEquals(expectedTreatmentDto.patient().id(), treatmentDto.patient().id());
        assertEquals(expectedTreatmentDto.outpatientDepartment().id(), treatmentDto.outpatientDepartment().id());
        assertEquals(expectedTreatmentDto.doctors().size(), treatmentDto.doctors().size());
        assertEquals(expectedTreatmentDto.doctors().get(0).id(), treatmentDto.doctors().get(0).id());
        assertEquals(expectedTreatmentDto.medicines().size(), treatmentDto.medicines().size());
        assertEquals(expectedTreatmentDto.medicines().get(0).id(), treatmentDto.medicines().get(0).id());

        verify(patientMapper, times(1)).patientToPatientDtoSparse(patient);
        verify(doctorMapper, times(1)).doctorToDoctorDtoSparse(doctor1);
        verify(treatmentMedicineMapper, times(1)).entityToDto(treatmentMedicine);
        verify(outpatientDepartmentService, times(1)).getOutpatientDepartmentById(anyLong());
    }

    @Test
    @DisplayName("entityListToDtoList: valid Treatment list - expect success")
    void shouldConvertEntityListToDtoList_whenGivenValidEntityList() {

        Treatment treatment1 = createTreatment(1L, new Patient(), new Doctor(), new TreatmentMedicine(), new OutpatientDepartment(), new Date(), new Date());
        Treatment treatment2 = createTreatment(2L, new Patient(), new Doctor(), new TreatmentMedicine(), new OutpatientDepartment(), new Date(), new Date());

        TreatmentDto treatmentDto1 = new TreatmentDto(1L, "Treatment Title", new Date(), new Date(), new PatientDtoSparse(
            1L, "1234567891", Collections.emptyList(), Collections.emptyList(), "John", "Doe", null, true
        ), new OutpatientDepartmentDto(
            1L, "Cardiology", "Cardiology Department", 20, null, true
        ), "Treatment Text", Collections.emptyList(), Collections.emptyList());
        TreatmentDto treatmentDto2 = new TreatmentDto(2L, "Treatment Title", new Date(), new Date(), new PatientDtoSparse(
            1L, "1234567891", Collections.emptyList(), Collections.emptyList(), "John", "Doe", null, true
        ), new OutpatientDepartmentDto(
            1L, "Cardiology", "Cardiology Department", 20, null, true
        ), "Treatment Text", Collections.emptyList(), Collections.emptyList());

        when(patientMapper.patientToPatientDto(any(Patient.class))).thenReturn(new PatientDto(1L, null, null, null, null, null, null, null, false, false));
        when(doctorMapper.doctorToDoctorDto(any(Doctor.class))).thenReturn(new DoctorDto(2L, null, null, null, null, false, false));
        when(treatmentMedicineMapper.entityToDto(any(TreatmentMedicine.class))).thenReturn(new TreatmentMedicineDto(3L, null, 0, null));
        when(outpatientDepartmentService.getOutpatientDepartmentById(any())).thenReturn(new OutpatientDepartmentDto(1L, null, null, 0, null, true));

        List<Treatment> treatments = Arrays.asList(treatment1, treatment2);
        List<TreatmentDto> treatmentDtos = treatmentMapper.entityListToDtoList(treatments);

        assertNotNull(treatmentDtos);
        assertEquals(2, treatmentDtos.size());
        assertEquals(treatmentDto1.id(), treatmentDtos.get(0).id());
        assertEquals(treatmentDto2.id(), treatmentDtos.get(1).id());
    }
}
