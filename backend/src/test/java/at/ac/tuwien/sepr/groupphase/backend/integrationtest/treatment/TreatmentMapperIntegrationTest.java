package at.ac.tuwien.sepr.groupphase.backend.integrationtest.treatment;


import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.TreatmentMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import at.ac.tuwien.sepr.groupphase.backend.entity.Treatment;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.integrationtest.treatment.util.TreatmentTestUtils;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.DoctorServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.MedicationServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.OutpatientDepartmentServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.PatientServiceImpl;
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

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@ActiveProfiles({"test", "datagen"})
public class TreatmentMapperIntegrationTest extends TestBase {

    @Autowired
    private TreatmentMapper treatmentMapper;

    @Autowired
    private PatientServiceImpl patientServiceImpl;

    @Autowired
    private DoctorServiceImpl doctorServiceImpl;

    @Autowired
    private OutpatientDepartmentServiceImpl outpatientDepartmentServiceImpl;

    @Autowired
    private MedicationServiceImpl medicationServiceImpl;

    @Autowired
    private TreatmentTestUtils treatmentTestUtils;

    private TreatmentDtoCreate TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED;

    public TreatmentMapperIntegrationTest() {
        super("treatment");
    }

    @BeforeEach
    void setUp() {
        PatientDtoSparse patient = patientServiceImpl.getAllPatients().get(0);
        DoctorDtoSparse doctor = doctorServiceImpl.getAllDoctors().get(0);
        OutpatientDepartmentDto outpatientDepartment1 = outpatientDepartmentServiceImpl.getAllOutpatientDepartments().get(0);
        MedicationDto medication = medicationServiceImpl.getAllMedications().get(0);

        Date treatmentMedicationDate = treatmentTestUtils.createDate(2022, Calendar.JANUARY, 1, 10, 10);
        TreatmentMedicineDto treatmentMedicineDto = treatmentTestUtils.createTreatmentMedicineDto(medication, treatmentMedicationDate);

        Date treatmentStartDate = treatmentTestUtils.createDate(2022, Calendar.JANUARY, 1, 4, 0);
        Date treatmentEndDate = treatmentTestUtils.createDate(2023, Calendar.JANUARY, 1, 3, 30);
        String treatmentText = "Treatment Text";
        String treatmentTitle = "Treatment Title";

        TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED = new TreatmentDtoCreate(
            treatmentTitle,
            treatmentStartDate,
            treatmentEndDate,
            patient,
            outpatientDepartment1,
            treatmentText,
            List.of(doctor),
            List.of(treatmentMedicineDto)
        );
    }

    @Test
    @DisplayName("dtoToEntity: valid TreatmentDtoCreate - expect success")
    void testDtoToEntity_givenValidTreatmentDtoCreate_expectSuccess() {
        Treatment treatment = treatmentMapper.dtoToEntity(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);

        assertNotNull(treatment);
        assertAll("Verify treatment properties",
            () -> assertEquals(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.treatmentTitle(), treatment.getTreatmentTitle()),
            () -> assertEquals(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.treatmentText(), treatment.getTreatmentText()),
            () -> assertEquals(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.patient().id(), treatment.getPatient().getPatientId()),
            () -> assertEquals(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.outpatientDepartment().id(), treatment.getOutpatientDepartment().getId()),
            () -> assertEquals(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.doctors().size(), treatment.getDoctors().size()),
            () -> assertEquals(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.medicines().size(), treatment.getMedicines().size())
        );

        checkIfDoctorIsEqual(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED, treatment);
        checkIfMedicineIsEqual(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED, treatment);
    }

    @Test
    @DisplayName("entityToDto: valid Treatment - expect success")
    void testEntityToDto_givenValidTreatment_expectSuccess() {
        Treatment treatment = treatmentMapper.dtoToEntity(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
        treatment.setId(1L);

        TreatmentDto treatmentDto = treatmentMapper.entityToDto(treatment);

        assertNotNull(treatmentDto);
        assertAll("Verify treatment DTO properties",
            () -> assertEquals(treatment.getId(), treatmentDto.id()),
            () -> assertEquals(treatment.getTreatmentTitle(), treatmentDto.treatmentTitle()),
            () -> assertEquals(treatment.getTreatmentText(), treatmentDto.treatmentText()),
            () -> assertEquals(treatment.getPatient().getPatientId(), treatmentDto.patient().id()),
            () -> assertEquals(treatment.getOutpatientDepartment().getId(), treatmentDto.outpatientDepartment().id()),
            () -> assertEquals(treatment.getDoctors().size(), treatmentDto.doctors().size()),
            () -> assertEquals(treatment.getMedicines().size(), treatmentDto.medicines().size())
        );
        checkIfDoctorIsEqual(treatmentDto, treatment);
        checkIfMedicineIsEqual(treatmentDto, treatment);
    }

    @Test
    @DisplayName("entityListToDtoList: valid Treatment list - expect success")
    void testEntityListToDtoList_givenValidTreatmentList_expectSuccess() {
        Treatment treatment = treatmentMapper.dtoToEntity(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
        treatment.setId(1L);
        Treatment treatment2 = treatmentMapper.dtoToEntity(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
        treatment2.setId(2L);
        List<Treatment> treatmentList = List.of(treatment, treatment2);

        List<TreatmentDto> treatmentDtoList = treatmentMapper.entityListToDtoList(treatmentList);

        assertNotNull(treatmentDtoList);
        assertEquals(treatmentList.size(), treatmentDtoList.size());
        for (int i = 0; i < treatmentList.size(); i++) {
            Treatment treatmentEntity = treatmentList.get(i);
            TreatmentDto treatmentDto = treatmentDtoList.get(i);
            assertAll("Verify treatment DTO properties",
                () -> assertEquals(treatmentEntity.getId(), treatmentDto.id()),
                () -> assertEquals(treatmentEntity.getTreatmentTitle(), treatmentDto.treatmentTitle()),
                () -> assertEquals(treatmentEntity.getTreatmentText(), treatmentDto.treatmentText()),
                () -> assertEquals(treatmentEntity.getPatient().getPatientId(), treatmentDto.patient().id()),
                () -> assertEquals(treatmentEntity.getOutpatientDepartment().getId(), treatmentDto.outpatientDepartment().id()),
                () -> assertEquals(treatmentEntity.getDoctors().size(), treatmentDto.doctors().size()),
                () -> assertEquals(treatmentEntity.getMedicines().size(), treatmentDto.medicines().size())
            );
            checkIfDoctorIsEqual(treatmentDto, treatmentEntity);
            checkIfMedicineIsEqual(treatmentDto, treatmentEntity);
        }
    }

    void checkIfDoctorIsEqual(TreatmentDtoCreate treatmentDtoCreate, Treatment treatment) {
        for (int i = 0; i < treatmentDtoCreate.doctors().size(); i++) {
            DoctorDtoSparse expectedDoctor = treatmentDtoCreate.doctors().get(i);
            Doctor actualDoctor = treatment.getDoctors().get(i);
            assertAll("Verify doctor properties",
                () -> assertEquals(expectedDoctor.id(), actualDoctor.getDoctorId()),
                () -> assertEquals(expectedDoctor.email(), actualDoctor.getCredential().getEmail()),
                () -> assertEquals(expectedDoctor.firstname(), actualDoctor.getCredential().getFirstName()),
                () -> assertEquals(expectedDoctor.lastname(), actualDoctor.getCredential().getLastName())
            );
        }
    }

    void checkIfDoctorIsEqual(TreatmentDto treatmentDto, Treatment treatment) {
        for (int i = 0; i < treatmentDto.doctors().size(); i++) {
            DoctorDtoSparse expectedDoctor = treatmentDto.doctors().get(i);
            Doctor actualDoctor = treatment.getDoctors().get(i);
            assertAll("Verify doctor properties",
                () -> assertEquals(expectedDoctor.id(), actualDoctor.getDoctorId()),
                () -> assertEquals(expectedDoctor.email(), actualDoctor.getCredential().getEmail()),
                () -> assertEquals(expectedDoctor.firstname(), actualDoctor.getCredential().getFirstName()),
                () -> assertEquals(expectedDoctor.lastname(), actualDoctor.getCredential().getLastName())
            );
        }
    }

    void checkIfMedicineIsEqual(TreatmentDtoCreate treatmentDto, Treatment treatment) {
        for (int i = 0; i < treatmentDto.medicines().size(); i++) {
            TreatmentMedicineDto expectedMedicine = treatmentDto.medicines().get(i);
            TreatmentMedicine actualMedicine = treatment.getMedicines().get(i);
            assertAll("Verify treatment medicine properties",
                () -> assertEquals(expectedMedicine.medication().id(), actualMedicine.getMedicine().getId()),
                () -> assertEquals(expectedMedicine.medicineAdministrationDate(), actualMedicine.getTimeOfAdministration()),
                () -> assertEquals(expectedMedicine.amount(), actualMedicine.getAmount())
            );
        }
    }

    void checkIfMedicineIsEqual(TreatmentDto treatmentDto, Treatment treatment) {
        for (int i = 0; i < treatmentDto.medicines().size(); i++) {
            TreatmentMedicineDto expectedMedicine = treatmentDto.medicines().get(i);
            TreatmentMedicine actualMedicine = treatment.getMedicines().get(i);
            assertAll("Verify treatment medicine properties",
                () -> assertEquals(expectedMedicine.medication().id(), actualMedicine.getMedicine().getId()),
                () -> assertEquals(expectedMedicine.medicineAdministrationDate(), actualMedicine.getTimeOfAdministration()),
                () -> assertEquals(expectedMedicine.amount(), actualMedicine.getAmount())
            );
        }
    }


}
