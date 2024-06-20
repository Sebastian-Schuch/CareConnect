package at.ac.tuwien.sepr.groupphase.backend.integrationtest.treatment;

import at.ac.tuwien.sepr.groupphase.backend.TestBase;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Treatment;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.integrationtest.treatment.util.TreatmentTestUtils;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TreatmentService;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@ActiveProfiles({"test", "datagen"})
public class TreatmentServiceIntegrationTest extends TestBase {
    @Autowired
    private TreatmentService treatmentService;
    @Autowired
    private PatientServiceImpl patientService;
    @Autowired
    private OutpatientDepartmentServiceImpl outpatientDepartmentService;
    @Autowired
    private MedicationServiceImpl medicationService;
    @Autowired
    private DoctorServiceImpl doctorService;
    @Autowired
    private TreatmentTestUtils treatmentTestUtils;

    private PatientDtoSparse PATIENT1;
    private PatientDtoSparse PATIENT2;
    private DoctorDtoSparse DOCTOR1;
    private DoctorDtoSparse DOCTOR2;
    private OutpatientDepartmentDto OUTPATIENT_DEPARTMENT2;
    private MedicationDto MEDICATION1;
    private MedicationDto MEDICATION2;
    private Date TREATMENT_MEDICATION_DATE2;
    private Date TREATMENT_START_DATE2;
    private Date TREATMENT_END_DATE2;
    private TreatmentDtoCreate TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED;
    @Autowired
    private TreatmentRepository treatmentRepository;

    public TreatmentServiceIntegrationTest() {
        super("treatment");
    }

    @BeforeEach
    void createTestData() {

        PATIENT1 = patientService.getAllPatients().get(0);
        PATIENT2 = patientService.getAllPatients().get(2);
        DOCTOR1 = doctorService.getAllDoctors().get(0);
        DOCTOR2 = doctorService.getAllDoctors().get(2);
        OutpatientDepartmentDto OUTPATIENT_DEPARTMENT1 = outpatientDepartmentService.getAllOutpatientDepartments().get(0);
        OUTPATIENT_DEPARTMENT2 = outpatientDepartmentService.getAllOutpatientDepartments().get(2);
        MEDICATION1 = medicationService.getAllMedications().get(1);
        MEDICATION2 = medicationService.getAllMedications().get(2);

        Date TREATMENT_MEDICATION_DATE1 = treatmentTestUtils.createDate(2022, Calendar.JANUARY, 1, 10, 10);
        TREATMENT_MEDICATION_DATE2 = treatmentTestUtils.createDate(2022, Calendar.FEBRUARY, 1, 10, 10);
        Date TREATMENT_START_DATE1 = treatmentTestUtils.createDate(2022, Calendar.JANUARY, 1, 4, 0);
        TREATMENT_START_DATE2 = treatmentTestUtils.createDate(2022, Calendar.FEBRUARY, 1, 4, 0);
        Date TREATMENT_END_DATE1 = treatmentTestUtils.createDate(2023, Calendar.JANUARY, 1, 3, 30);
        TREATMENT_END_DATE2 = treatmentTestUtils.createDate(2022, Calendar.FEBRUARY, 4, 4, 30);
        String TREATMENT_TEXT1 = "Treatment Text";
        String TREATMENT_TITLE1 = "Treatment Title";

        TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED = new TreatmentDtoCreate(
            TREATMENT_TITLE1,
            TREATMENT_START_DATE1,
            TREATMENT_END_DATE1,
            PATIENT1,
            OUTPATIENT_DEPARTMENT1,
            TREATMENT_TEXT1,
            List.of(DOCTOR1),
            List.of(treatmentTestUtils.createTreatmentMedicineDto(MEDICATION1, TREATMENT_MEDICATION_DATE1))
        );
    }

    @Test
    @DisplayName("createTreatment: valid treatment dto - expect success")
    void testCreateTreatment_givenValidTreatmentDtoCreate_expectCreateSuccess() {
        TreatmentDto createdTreatment = treatmentService.createTreatment(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
        assertNotNull(createdTreatment);
        assertAll("Verify treatment properties",
            () -> assertEquals(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.treatmentTitle(), createdTreatment.treatmentTitle()),
            () -> assertEquals(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.treatmentText(), createdTreatment.treatmentText()),
            () -> assertEquals(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.patient().email(), createdTreatment.patient().email()),
            () -> assertEquals(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.outpatientDepartment().id(), createdTreatment.outpatientDepartment().id()),
            () -> assertEquals(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.doctors().size(), createdTreatment.doctors().size()),
            () -> assertEquals(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.medicines().size(), createdTreatment.medicines().size()),
            () -> assertEquals(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.treatmentStart(), createdTreatment.treatmentStart()),
            () -> assertEquals(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED.treatmentEnd(), createdTreatment.treatmentEnd())
        );

        checkIfTreatmentMedicineIsEqualAfterCreateOrUpdate(createdTreatment, TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
        checkIfDoctorIsEqualAfterCreateOrUpdate(createdTreatment, TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
    }

    @Test
    @DisplayName("updateTreatment: valid treatment dto - expect success")
    void testUpdateTreatment_givenValidUpdateDto_expectUpdateSuccess() throws NotFoundException {

        TreatmentDto createdTreatment = treatmentService.createTreatment(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
        TreatmentDtoCreate updateDto = new TreatmentDtoCreate(
            "Updated Title",
            TREATMENT_START_DATE2,
            TREATMENT_END_DATE2,
            PATIENT2,
            OUTPATIENT_DEPARTMENT2,
            "Updated Treatment Text",
            List.of(DOCTOR2, DOCTOR1),
            List.of(treatmentTestUtils.createTreatmentMedicineDto(MEDICATION2, TREATMENT_MEDICATION_DATE2), treatmentTestUtils.createTreatmentMedicineDto(MEDICATION1, TREATMENT_MEDICATION_DATE2),
                treatmentTestUtils.createTreatmentMedicineDto(MEDICATION2, TREATMENT_MEDICATION_DATE2))
        );

        TreatmentDto updatedTreatment = treatmentService.updateTreatment(createdTreatment.id(), updateDto);

        assertNotNull(updatedTreatment);
        assertAll("Verify updated treatment properties",
            () -> assertEquals(createdTreatment.id(), updatedTreatment.id()),
            () -> assertEquals(updateDto.treatmentTitle(), updatedTreatment.treatmentTitle()),
            () -> assertEquals(updateDto.treatmentText(), updatedTreatment.treatmentText()),
            () -> assertEquals(updateDto.patient().email(), updatedTreatment.patient().email()),
            () -> assertEquals(updateDto.outpatientDepartment().id(), updatedTreatment.outpatientDepartment().id()),
            () -> assertEquals(updateDto.doctors().size(), updatedTreatment.doctors().size()),
            () -> assertEquals(updateDto.medicines().size(), updatedTreatment.medicines().size()),
            () -> assertEquals(updateDto.treatmentStart(), updatedTreatment.treatmentStart()),
            () -> assertEquals(updateDto.treatmentEnd(), updatedTreatment.treatmentEnd())
        );

        checkIfTreatmentMedicineIsEqualAfterCreateOrUpdate(updatedTreatment, updateDto);
        checkIfDoctorIsEqualAfterCreateOrUpdate(updatedTreatment, updateDto);

    }

    @Test
    @DisplayName("getTreatmentById: valid treatment id - expect success")
    void testGetTreatmentById_givenValidId_expectSuccess() throws NotFoundException {
        TreatmentDto createdTreatment = treatmentService.createTreatment(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
        TreatmentDto foundTreatment = treatmentService.getTreatmentById(createdTreatment.id());

        assertNotNull(foundTreatment);
        assertAll("Verify found treatment properties",
            () -> assertEquals(createdTreatment.id(), foundTreatment.id()),
            () -> assertEquals(createdTreatment.treatmentTitle(), foundTreatment.treatmentTitle()),
            () -> assertEquals(createdTreatment.treatmentText(), foundTreatment.treatmentText()),
            () -> assertEquals(createdTreatment.patient().email(), foundTreatment.patient().email()),
            () -> assertEquals(createdTreatment.outpatientDepartment().id(), foundTreatment.outpatientDepartment().id()),
            () -> assertEquals(createdTreatment.doctors().size(), foundTreatment.doctors().size()),
            () -> assertEquals(createdTreatment.medicines().size(), foundTreatment.medicines().size()),
            () -> assertEquals(createdTreatment.treatmentStart(), foundTreatment.treatmentStart()),
            () -> assertEquals(createdTreatment.treatmentEnd(), foundTreatment.treatmentEnd())
        );
        checkIfTreatmentMedicineIsEqualAfterCreateOrUpdate(foundTreatment, TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
        checkIfDoctorIsEqualAfterCreateOrUpdate(foundTreatment, TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
    }

    @Test
    @DisplayName("getTreatmentById: invalid treatment id - expect NotFoundException")
    void testGetTreatmentById_givenInvalidId_ThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> treatmentService.getTreatmentById(999L));
    }

    @Test
    @DisplayName("getAllTreatmentsFromPatient: valid patient id - expect success")
    void testGetAllTreatmentsFromPatient_givenValidPatientId_ExpectSuccess() {
        treatmentService.createTreatment(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
        List<TreatmentDto> treatments = treatmentService.getAllTreatmentsFromPatient(PATIENT1.id());

        assertNotNull(treatments);
        assertFalse(treatments.isEmpty());
        assertTrue(treatments.stream().allMatch(treatment -> treatment.patient().id() == PATIENT1.id()));
    }

    @Test
    @DisplayName("getAllTreatmentsFromDoctor: valid doctor id - expect success")
    void testGetAllTreatmentsFromDoctor_givenValidDoctorId_ExpectSuccess() {
        treatmentService.createTreatment(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
        List<TreatmentDto> treatments = treatmentService.getAllTreatmentsFromDoctor(DOCTOR1.id());

        assertNotNull(treatments);
        assertFalse(treatments.isEmpty());
        assertTrue(treatments.stream().allMatch(treatment -> treatment.doctors().stream().anyMatch(doctor -> doctor.id() == (DOCTOR1.id()))));
    }

    @Test
    @DisplayName("getTreatmentEntityById: valid treatment id - expect success")
    void testGetTreatmentEntityById_givenValidTreatmentId_ExpectSuccess() throws NotFoundException {
        TreatmentDto createdTreatment = treatmentService.createTreatment(TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
        Treatment foundTreatment = treatmentService.getTreatmentEntityById(createdTreatment.id());

        assertNotNull(foundTreatment);
        assertAll("Verify found treatment properties",
            () -> assertEquals(createdTreatment.id(), foundTreatment.getId()),
            () -> assertEquals(createdTreatment.treatmentTitle(), foundTreatment.getTreatmentTitle()),
            () -> assertEquals(createdTreatment.treatmentText(), foundTreatment.getTreatmentText()),
            () -> assertEquals(createdTreatment.patient().email(), foundTreatment.getPatient().getCredential().getEmail()),
            () -> assertEquals(createdTreatment.outpatientDepartment().id(), foundTreatment.getOutpatientDepartment().getId()),
            () -> assertEquals(createdTreatment.doctors().size(), foundTreatment.getDoctors().size()),
            () -> assertEquals(createdTreatment.medicines().size(), foundTreatment.getMedicines().size()),
            () -> assertEquals(createdTreatment.treatmentStart(), foundTreatment.getTreatmentStart()),
            () -> assertEquals(createdTreatment.treatmentEnd(), foundTreatment.getTreatmentEnd())
        );
        checkIfTreatmentMedicineIsEqualAfterCreateOrUpdate(createdTreatment, TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
        checkIfDoctorIsEqualAfterCreateOrUpdate(createdTreatment, TREATMENT_DTO_CREATE_VALID_ONE_DOC_ONE_MED);
    }

    @Test
    @DisplayName("searchTreatments: valid treatment search - expect success")
    void testSearchTreatments_givenValidSearchParams_ExpectSuccess() throws NotFoundException {
        Treatment testTreatment = treatmentRepository.findByPatient_PatientId(PATIENT1.id()).get(0);
        TreatmentPageDto foundTreatment = treatmentService.searchTreatments(new TreatmentDtoSearch(0, 10, PATIENT1.id(), null, null, null, null, null, null, null, null));

        assertNotNull(foundTreatment);
        assertAll("Verify found treatment properties",
            () -> assertEquals(1, foundTreatment.treatments().size()),
            () -> assertEquals(testTreatment.getId(), foundTreatment.treatments().get(0).id()),
            () -> assertEquals(testTreatment.getTreatmentTitle(), foundTreatment.treatments().get(0).treatmentTitle()),
            () -> assertEquals(testTreatment.getTreatmentText(), foundTreatment.treatments().get(0).treatmentText()),
            () -> assertEquals(testTreatment.getPatient().getCredential().getEmail(), foundTreatment.treatments().get(0).patient().email()),
            () -> assertEquals(testTreatment.getOutpatientDepartment().getId(), foundTreatment.treatments().get(0).outpatientDepartment().id()),
            () -> assertEquals(testTreatment.getDoctors().size(), foundTreatment.treatments().get(0).doctors().size()),
            () -> assertEquals(testTreatment.getMedicines().size(), foundTreatment.treatments().get(0).medicines().size()),
            () -> assertEquals(testTreatment.getTreatmentStart(), foundTreatment.treatments().get(0).treatmentStart()),
            () -> assertEquals(testTreatment.getTreatmentEnd(), foundTreatment.treatments().get(0).treatmentEnd())
        );
    }

    @Test
    @DisplayName("searchTreatments: valid treatment search no matching - expect success")
    void testSearchTreatments_givenValidSearchParamsWithNoMatchingTreatments_ExpectSuccessWithNoTreatments() throws NotFoundException {
        TreatmentPageDto foundTreatment = treatmentService.searchTreatments(new TreatmentDtoSearch(0, 10, null, null, null, null, null, null, null, null, "Some random name that will not be of a actual outpatient department"));
        assertNotNull(foundTreatment);
        assertEquals(0, foundTreatment.treatments().size());
    }

    /**
     * Helper method to check if the doctor is equal after create or update
     *
     * @param updatedTreatment updated treatment
     * @param updateDto        update dto
     */
    void checkIfDoctorIsEqualAfterCreateOrUpdate(TreatmentDto updatedTreatment, TreatmentDtoCreate updateDto) {
        for (int i = 0; i < updatedTreatment.doctors().size(); i++) {
            DoctorDtoSparse expectedDoctor = updateDto.doctors().get(i);
            DoctorDtoSparse actualDoctor = updatedTreatment.doctors().get(i);
            assertAll("Verify treatment doctor properties",
                () -> assertEquals(expectedDoctor.id(), actualDoctor.id()),
                () -> assertEquals(expectedDoctor.email(), actualDoctor.email())
            );
        }
    }

    /**
     * Helper method to check if the treatment medicine is equal after create or update
     *
     * @param createdTreatment     created treatment
     * @param theTreatmentToCreate valid treatment dto
     */
    void checkIfTreatmentMedicineIsEqualAfterCreateOrUpdate(TreatmentDto createdTreatment, TreatmentDtoCreate theTreatmentToCreate) {
        for (int i = 0; i < createdTreatment.medicines().size(); i++) {
            TreatmentMedicineDto expectedMedicine = theTreatmentToCreate.medicines().get(i);
            TreatmentMedicineDto actualMedicine = createdTreatment.medicines().get(i);
            assertAll("Verify treatment medicine properties",
                () -> assertEquals(expectedMedicine.medication().id(), actualMedicine.medication().id()),
                () -> assertEquals(expectedMedicine.amount(), actualMedicine.amount()),
                () -> assertEquals(expectedMedicine.medicineAdministrationDate(), actualMedicine.medicineAdministrationDate()),
                () -> assertEquals(expectedMedicine.unitOfMeasurement(), actualMedicine.unitOfMeasurement())
            );
        }
    }

}
