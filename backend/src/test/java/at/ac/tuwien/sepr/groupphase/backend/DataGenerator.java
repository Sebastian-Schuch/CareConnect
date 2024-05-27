package at.ac.tuwien.sepr.groupphase.backend;

import at.ac.tuwien.sepr.groupphase.backend.entity.Allergy;
import at.ac.tuwien.sepr.groupphase.backend.entity.Appointment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Secretary;
import at.ac.tuwien.sepr.groupphase.backend.entity.Station;
import at.ac.tuwien.sepr.groupphase.backend.entity.Treatment;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.repository.AllergyRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.AppointmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.CredentialRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.DoctorRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.MedicationRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.OpeningHoursRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.OutpatientDepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SecretaryRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.StationRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentMedicineRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This component is only created, if the profile {@code datagen} is active
 */
@Component
@Profile("datagen")
public class DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private final AllergyRepository allergyRepository;

    @Autowired
    private final AppointmentRepository appointmentRepository;

    @Autowired
    private final CredentialRepository credentialRepository;

    @Autowired
    private final DoctorRepository doctorRepository;

    @Autowired
    private final MedicationRepository medicationRepository;

    @Autowired
    private final OpeningHoursRepository openingHoursRepository;

    @Autowired
    private final OutpatientDepartmentRepository outpatientDepartmentRepository;

    @Autowired
    private final PatientRepository patientRepository;

    @Autowired
    private final SecretaryRepository secretaryRepository;

    @Autowired
    private final StationRepository stationRepository;

    @Autowired
    private final TreatmentMedicineRepository treatmentMedicineRepository;

    @Autowired
    private final TreatmentRepository treatmentRepository;

    private final int numberOfTestData = 10;


    /**
     * Executed once when the component is instantiated. Inserts some dummy data.
     */
    public DataGenerator(AllergyRepository allergyRepository, AppointmentRepository appointmentRepository, CredentialRepository credentialRepository, DoctorRepository doctorRepository,
                         MedicationRepository medicationRepository, OpeningHoursRepository openingHoursRepository, OutpatientDepartmentRepository outpatientDepartmentRepository, PatientRepository patientRepository,
                         SecretaryRepository secretaryRepository, StationRepository stationRepository, TreatmentMedicineRepository treatmentMedicineRepository, TreatmentRepository treatmentRepository) {
        this.allergyRepository = allergyRepository;
        this.appointmentRepository = appointmentRepository;
        this.credentialRepository = credentialRepository;
        this.doctorRepository = doctorRepository;
        this.medicationRepository = medicationRepository;
        this.openingHoursRepository = openingHoursRepository;
        this.outpatientDepartmentRepository = outpatientDepartmentRepository;
        this.patientRepository = patientRepository;
        this.secretaryRepository = secretaryRepository;
        this.stationRepository = stationRepository;
        this.treatmentMedicineRepository = treatmentMedicineRepository;
        this.treatmentRepository = treatmentRepository;
    }

    /**
     * Generates dummy data in the database. This method is executed once when the component is instantiated.
     */
    public void generateData() {
        LOGGER.info("Generating data…");
        generateDataInDb();
        LOGGER.info("Finished generating data without error.");
    }

    /**
     * Clears all dummy data from the database.
     */
    public void clearData() {
        LOGGER.info("Clearing data…");
        clearDb();
        LOGGER.info("Finished clearing data without error");
    }

    private void clearDb() {
        allergyRepository.deleteAll();
        appointmentRepository.deleteAll();
        treatmentRepository.deleteAll();
        treatmentMedicineRepository.deleteAll();
        outpatientDepartmentRepository.deleteAll();
        openingHoursRepository.deleteAll();
        stationRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
        secretaryRepository.deleteAll();
        medicationRepository.deleteAll();
        credentialRepository.deleteAll();
    }

    private void generateDataInDb() {
        generateDataForAllergies();
        generateDataForDoctors();
        generateDataForSecretary();
        generateDataForPatients();
        generateDataForMedication();
        generateDataForOutpatientDepartments();
        generateDataForStations();
        generateDataForAppointments();
        generateDataForTreatmentMedicines();
        generateDataForTreatments();
    }

    private void generateDataForAllergies() {
        for (long i = 0; i < numberOfTestData; i++) {
            Allergy allergy = new Allergy();
            allergy.setName("Allergy" + i);
            allergyRepository.save(allergy);
        }
    }

    private void generateDataForDoctors() {
        for (long i = 0; i < numberOfTestData; i++) {
            Doctor doctor = new Doctor();

            Credential credential = new Credential();
            credential.setEmail("doctor" + i + "@email.com");
            credential.setFirstName("Doctor" + i);
            credential.setLastName("Doctor" + i);
            credential.setPassword("dPassword" + i);
            credential.setActive(true);
            credential.setRole(Role.DOCTOR);
            credential.setInitialPassword(false);
            doctor.setCredential(credential);

            doctorRepository.save(doctor);
        }
    }

    private void generateDataForSecretary() {
        for (long i = 0; i < numberOfTestData; i++) {
            Secretary secretary = new Secretary();


            Credential credential = new Credential();
            credential.setEmail("secretary" + i + "@email.com");
            credential.setFirstName("Secretary" + i);
            credential.setLastName("Secretary" + i);
            credential.setPassword("sPassword" + i);
            credential.setActive(true);
            credential.setRole(Role.SECRETARY);
            credential.setInitialPassword(false);
            secretary.setCredential(credential);

            secretaryRepository.save(secretary);
        }
    }

    private void generateDataForPatients() {
        for (long i = 0; i < numberOfTestData; i++) {
            Patient patient = new Patient();


            Credential credential = new Credential();
            credential.setEmail("patient" + i + "@email.com");
            credential.setFirstName("Patient" + i);
            credential.setLastName("Patient" + i);
            credential.setPassword("pPassword" + i);
            credential.setActive(true);
            credential.setRole(Role.PATIENT);
            credential.setInitialPassword(false);
            patient.setCredential(credential);

            String svnr = "";
            for (int j = 0; j < 10; j++) {
                svnr += (int) (Math.random() * 10);
            }
            patient.setSvnr(svnr);

            patientRepository.save(patient);
        }
    }

    private void generateDataForMedication() {
        for (long i = 0; i < numberOfTestData; i++) {
            Medication medication = new Medication();
            medication.setName("Medication" + i);
            medication.setActive(true);
            medicationRepository.save(medication);
        }
    }

    private void generateDataForOutpatientDepartments() {
        for (long i = 0; i < numberOfTestData; i++) {
            OutpatientDepartment outpatientDepartment = new OutpatientDepartment();
            outpatientDepartment.setName("OutpatientDepartment" + i);
            outpatientDepartment.setDescription("Description" + i);
            outpatientDepartment.setCapacity((int) (i * 5) % 25 + 5);

            OpeningHours openingHours = new OpeningHours();
            String hours = "00:00-24:00";
            openingHours.setMonday(hours);
            openingHours.setTuesday(hours);
            openingHours.setWednesday(hours);
            openingHours.setThursday(hours);
            openingHours.setFriday(hours);
            openingHours.setSaturday(hours);
            openingHours.setSunday(hours);
            outpatientDepartment.setOpeningHours(openingHours);

            outpatientDepartmentRepository.save(outpatientDepartment);
        }
    }

    private void generateDataForStations() {
        for (long i = 0; i < numberOfTestData; i++) {
            Station station = new Station();
            station.setName("Station" + i);
            station.setCapacity(((i * 5) % 25) + 5);
            stationRepository.save(station);
        }
    }

    private void generateDataForAppointments() {
        for (long i = 0; i < numberOfTestData; i++) {
            Appointment appointment = new Appointment();

            appointment.setPatient(patientRepository.findAll().get((int) (i)));

            appointment.setOutpatientDepartment(outpatientDepartmentRepository.findAll().get(1));

            appointment.setStartDate(new Date(2022, Calendar.JANUARY, 1, 8, 0));
            appointment.setEndDate(new Date(2022, Calendar.JANUARY, 1, 8, 30));
            appointment.setNotes("Notes" + i);
            appointmentRepository.save(appointment);
        }
    }

    private void generateDataForTreatmentMedicines() {
        for (long i = 0; i < numberOfTestData; i++) {
            TreatmentMedicine treatmentMedicine = new TreatmentMedicine();

            treatmentMedicine.setMedicine(medicationRepository.findMedicationById(i));

            treatmentMedicine.setAmount((i * 50) % 300 + 50);
            treatmentMedicine.setUnitOfMeasurement("mg");
            treatmentMedicine.setTimeOfAdministration(new Date());
            treatmentMedicineRepository.save(treatmentMedicine);
        }
    }

    private void generateDataForTreatments() {
        for (long i = 0; i < numberOfTestData; i++) {
            Treatment treatment = new Treatment();
            ;
            treatment.setTreatmentTitle("Title" + i);

            treatment.setTreatmentStart(new Date(2022, Calendar.JANUARY, 1, 8, 0));
            treatment.setTreatmentEnd(new Date(2022, Calendar.JANUARY, 1, 8, 30));

            treatment.setPatient(patientRepository.findAll().get((int) (i)));

            treatment.setOutpatientDepartment(outpatientDepartmentRepository.findAll().get((int) i));

            treatment.setTreatmentText("Text" + i);

            List<Doctor> doctors = new ArrayList<>();
            for (long j = 0; j < i; j++) {
                doctors.add(doctorRepository.findDoctorById(j));
            }
            treatment.setDoctors(doctors);

            List<TreatmentMedicine> treatmentMedicines = new ArrayList<>();
            treatmentMedicines.add(treatmentMedicineRepository.findAll().get((int) (i)));
            treatment.setMedicines(treatmentMedicines);

            treatmentRepository.save(treatment);
        }
    }
}

