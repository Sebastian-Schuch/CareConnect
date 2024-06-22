package at.ac.tuwien.sepr.groupphase.backend;

import at.ac.tuwien.sepr.groupphase.backend.entity.Admin;
import at.ac.tuwien.sepr.groupphase.backend.entity.Allergy;
import at.ac.tuwien.sepr.groupphase.backend.entity.Appointment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import at.ac.tuwien.sepr.groupphase.backend.entity.InpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import at.ac.tuwien.sepr.groupphase.backend.entity.OpeningHours;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Secretary;
import at.ac.tuwien.sepr.groupphase.backend.entity.Treatment;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.repository.AdminRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.AllergyRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.AppointmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.CredentialRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.DoctorRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.InpatientDepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.MedicationRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.OpeningHoursRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.OutpatientDepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PatientRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SecretaryRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentMedicineRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
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
    private final AdminRepository adminRepository;


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
    private final InpatientDepartmentRepository inpatientDepartmentRepository;

    @Autowired
    private final TreatmentMedicineRepository treatmentMedicineRepository;

    @Autowired
    private final TreatmentRepository treatmentRepository;

    private final int numberOfTestData = 10;


    /**
     * Executed once when the component is instantiated. Inserts some dummy data.
     */
    public DataGenerator(AllergyRepository allergyRepository, AdminRepository adminRepository, AppointmentRepository appointmentRepository, CredentialRepository credentialRepository,
                         DoctorRepository doctorRepository,
                         MedicationRepository medicationRepository, OpeningHoursRepository openingHoursRepository,
                         OutpatientDepartmentRepository outpatientDepartmentRepository, PatientRepository patientRepository,
                         SecretaryRepository secretaryRepository, InpatientDepartmentRepository inpatientDepartmentRepository, TreatmentMedicineRepository treatmentMedicineRepository,
                         TreatmentRepository treatmentRepository) {
        this.allergyRepository = allergyRepository;
        this.adminRepository = adminRepository;
        this.appointmentRepository = appointmentRepository;
        this.credentialRepository = credentialRepository;
        this.doctorRepository = doctorRepository;
        this.medicationRepository = medicationRepository;
        this.openingHoursRepository = openingHoursRepository;
        this.outpatientDepartmentRepository = outpatientDepartmentRepository;
        this.patientRepository = patientRepository;
        this.secretaryRepository = secretaryRepository;
        this.inpatientDepartmentRepository = inpatientDepartmentRepository;
        this.treatmentMedicineRepository = treatmentMedicineRepository;
        this.treatmentRepository = treatmentRepository;

    }

    /**
     * Generates dummy data in the database. This method is executed once when the component is instantiated.
     */
    public void generateData(String dataType) {
        LOGGER.info("Generating data…");
        switch (dataType) {
            case "allergy" -> generateDataInDb(true, false, false, false, false, false, false, false, false, false, false);
            case "administrator" -> generateDataInDb(false, false, true, false, false, false, false, false, false, false, false);
            case "doctor" -> generateDataInDb(false, true, false, false, false, false, false, false, false, false, false);
            case "secretary" -> generateDataInDb(false, false, false, true, false, false, false, false, false, false, false);
            case "patient" -> generateDataInDb(true, false, false, false, true, true, false, false, false, false, false);
            case "medication" -> generateDataInDb(false, false, false, false, false, true, false, false, false, false, false);
            case "outpatientDepartment" -> generateDataInDb(false, false, false, false, true, false, true, false, false, false, false);
            case "inpatientDepartment" -> generateDataInDb(false, false, false, false, false, false, false, true, false, false, false);
            case "appointment" -> generateDataInDb(true, false, false, false, true, true, true, false, true, false, false);
            case "treatmentMedicine" -> generateDataInDb(false, false, false, false, false, true, false, false, false, true, false);
            case "treatment" -> generateDataInDb(true, true, false, false, true, true, true, false, false, true, true);
            case "stay" -> generateDataInDb(false, false, false, true, true, false, false, true, false, false, false);
            default -> generateDataInDb(true, true, true, true, true, true, true, true, true, true, true);
        }
        LOGGER.info("Finished generating data without error.");
    }

    private void generateDataInDb(boolean generateForAllergies, boolean generateForDoctors, boolean generateForAdministrator, boolean generateForSecretary, boolean generateForPatients, boolean generateForMedication,
                                  boolean generateForOutpatientDepartments,
                                  boolean generateForInpatientDepartments, boolean generateForAppointments, boolean generateForTreatmentMedicines, boolean generateForTreatments) {
        if (generateForAllergies) {
            generateDataForAllergies();
        }
        if (generateForMedication) {
            generateDataForMedication();
        }
        if (generateForDoctors) {
            generateDataForDoctors();
        }
        if (generateForSecretary) {
            generateDataForSecretary();
        }
        if (generateForAdministrator) {
            generateDataForAdministrators();
        }
        if (generateForPatients) {
            generateDataForPatients();
        }
        if (generateForOutpatientDepartments) {
            generateDataForOutpatientDepartments();
        }
        if (generateForInpatientDepartments) {
            generateDataForInpatientDepartments();
        }
        if (generateForAppointments) {
            generateDataForAppointments();
        }
        if (generateForTreatmentMedicines) {
            generateDataForTreatmentMedicines();
        }
        if (generateForTreatments) {
            generateDataForTreatments();
        }
    }

    private void generateDataForAllergies() {
        for (long i = 0; i < numberOfTestData; i++) {
            Allergy allergy = new Allergy();
            allergy.setName("Allergy" + i);
            allergyRepository.save(allergy);
        }
    }

    private void generateDataForDoctors() {
        //Normal Doctors
        doctorRepository.save(setDoctor("doctor.eggman@email.com", "Doctor", "Eggman", "ChaosEmeralds", true, false));
        doctorRepository.save(setDoctor("doctor.oetker@email.com", "Doctor", "Oetker", "SchokoladenPizza", true, false));
        doctorRepository.save(setDoctor("doctor.johnnySins@email.com", "Doctor", "JohnnySins", "RohreVerleger", true, false));
        doctorRepository.save(setDoctor("doctor.who@email.com", "Doctor", "Who", "Tardis", true, false));
        doctorRepository.save(setDoctor("doctor.strange@email.com", "Doctor", "Strange", "TimeStone", true, false));
        //Special Doctors (Inactive + Initial Password)
        doctorRepository.save(setDoctor("doctor.inactive@email.com", "Doctor", "Inactive", "404NotFound", false, false));
        doctorRepository.save(setDoctor("doctor.initial@email.com", "Doctor", "Initial", "InitialPassword", true, true));
    }

    private Doctor setDoctor(String email, String firstName, String lastName, String password, Boolean active, Boolean initialPassword) {
        Doctor doctor = new Doctor();
        Credential credential = new Credential();
        credential.setEmail(email);
        credential.setFirstName(firstName);
        credential.setLastName(lastName);
        credential.setPassword(password);
        credential.setActive(active);
        credential.setRole(Role.DOCTOR);
        credential.setInitialPassword(initialPassword);
        doctor.setCredential(credential);
        return doctor;
    }

    private void generateDataForPatients() {
        //Normal Patients
        List<Medication> medications = new ArrayList<>();
        List<Allergy> allergies = new ArrayList<>();

        patientRepository.save(setPatient("chris.anger@email.com", "Chris", "Anger", "AngerManagement", true, false, "6912120520", allergies, medications));
        patientRepository.save(setPatient("jonathan.schort@email.com", "Jonathan", "Schort", "Schorty", true, false, "6912225164", allergies, medications));
        patientRepository.save(setPatient("noah.oguamalam@email.com", "Noah", "Oguamalam", "SepmGroupCarrier", true, false, "6912225111", allergies, medications));
        patientRepository.save(setPatient("philipp.nürnberger@email.com", "Philipp", "Nürnberger", "KintaroOe", true, false, "6912034156", allergies, medications));
        patientRepository.save(setPatient("ryan.foster@email.com", "Ryan", "Foster", "FosterThePeople", true, false, "6912222173", allergies, medications));
        patientRepository.save(setPatient("sebastian.schuch@email.com", "Sebastian", "Schuch", "scHuchWirHabenSchonWiederEinenFehlerBeimEncrypten", true, false, "6912222156", allergies, medications));
        //Special Patients (Inactive + Initial Password)
        patientRepository.save(setPatient("patient.inactive@email.com", "Patient", "Inactive", "404NotFound", false, false, "0000000000", allergies, medications));
        patientRepository.save(setPatient("patient.intial@email.com", "Patient", "Initial", "InitialPassword", true, true, "1111111111", allergies, medications));
    }

    private Patient setPatient(String email, String firstName, String lastName, String password, Boolean active, Boolean initialPassword, String svnr, List<Allergy> allergies, List<Medication> medications) {
        Patient patient = new Patient();
        Credential credential = new Credential();
        credential.setEmail(email);
        credential.setFirstName(firstName);
        credential.setLastName(lastName);
        credential.setPassword(password);
        credential.setActive(active);
        credential.setRole(Role.PATIENT);
        credential.setInitialPassword(initialPassword);
        patient.setCredential(credential);
        patient.setSvnr(svnr);
        patient.setAllergies(allergies);
        patient.setMedicines(medications);
        return patient;
    }

    private void generateDataForAdministrators() {
        //Normal Administrators
        adminRepository.save(setAdministrator("administrator1@email.com", "Administrator", "One", "OnePassword", true, false));
        adminRepository.save(setAdministrator("administrator2@email.com", "Administrator", "Two", "TwoPassword", true, false));
        adminRepository.save(setAdministrator("administrator3@email.com", "Administrator", "Three", "ThreePassword", true, false));
        //Special Patients (Inactive + Initial Password)
        adminRepository.save(setAdministrator("administrator.inactive@email.com", "Administrator", "Inactive", "404NotFound", false, false));
        adminRepository.save(setAdministrator("administrator.initial@email.com", "Administrator", "Initial", "InitialPassword", true, true));
    }

    private Admin setAdministrator(String email, String firstName, String lastName, String password, Boolean active, Boolean initialPassword) {
        Admin admin = new Admin();
        Credential credential = new Credential();
        credential.setEmail(email);
        credential.setFirstName(firstName);
        credential.setLastName(lastName);
        credential.setPassword(password);
        credential.setActive(active);
        credential.setRole(Role.ADMIN);
        credential.setInitialPassword(initialPassword);
        admin.setCredential(credential);
        return admin;
    }

    private void generateDataForSecretary() {
        //Normal Secretary
        secretaryRepository.save(setSecretary("secretary1@email.com", "Secretary", "One", "OnePassword", true, false));
        secretaryRepository.save(setSecretary("secretary2@email.com", "Secretary", "Two", "TwoPassword", true, false));
        secretaryRepository.save(setSecretary("secretary3@email.com", "Secretary", "Three", "ThreePassword", true, false));
        secretaryRepository.save(setSecretary("secretary4@email.com", "Secretary", "Four", "FourPassword", true, false));
        secretaryRepository.save(setSecretary("secretary5@email.com", "Secretary", "Five", "FivePassword", true, false));
        //Special Secretary (Inactive + Initial Password)
        secretaryRepository.save(setSecretary("secretary.inactive.email.com", "Secretary", "Inactive", "404NotFound", false, false));
        secretaryRepository.save(setSecretary("secretary.initial.email.com", "Secretary", "Initial", "InitialPassword", true, true));
    }

    private Secretary setSecretary(String email, String firstName, String lastName, String password, Boolean active, Boolean initialPassword) {
        Secretary secretary = new Secretary();
        Credential credential = new Credential();
        credential.setEmail(email);
        credential.setFirstName(firstName);
        credential.setLastName(lastName);
        credential.setPassword(password);
        credential.setActive(active);
        credential.setRole(Role.SECRETARY);
        credential.setInitialPassword(initialPassword);
        secretary.setCredential(credential);
        return secretary;
    }

    private void generateDataForMedication() {
        medicationRepository.save(setMedication("Medication1", true));
        medicationRepository.save(setMedication("Medication2", true));
        medicationRepository.save(setMedication("Medication3", true));
        medicationRepository.save(setMedication("Medication4", true));
        medicationRepository.save(setMedication("Medication5", true));
        medicationRepository.save(setMedication("WieAgra", true));

        medicationRepository.save(setMedication("InactiveMedication", false));
    }

    private Medication setMedication(String name, Boolean active) {
        Medication medication = new Medication();
        medication.setName(name);
        medication.setActive(active);
        return medication;
    }

    private void generateDataForOutpatientDepartments() {
        outpatientDepartmentRepository.save(setOutpatientDepartment("X-Ray", "Description1", 3, setOpeningHours("08:00-14:00", "08:00-14:00", "08:00-14:00", "08:00-14:00", "08:00-14:00", "08:00-14:00", "08:00-14:00"), true));
        outpatientDepartmentRepository.save(setOutpatientDepartment("Drug Rehabilitation", "Description2", 5, setOpeningHours("10:00-18:00", "10:00-18:00", "10:00-18:00", "10:00-18:00", "10:00-18:00", "10:00-18:00", "10:00-18:00"), true));
        outpatientDepartmentRepository.save(setOutpatientDepartment("Emergency Room", "Description3", 10, setOpeningHours("01:00-22:00", "01:00-22:00", "01:00-22:00", "01:00-22:00", "01:00-22:00", "01:00-22:00", "01:00-22:00"), true));
    }

    private OutpatientDepartment setOutpatientDepartment(String name, String description, int capacity, OpeningHours openingHours, boolean active) {
        OutpatientDepartment outpatientDepartment = new OutpatientDepartment();
        outpatientDepartment.setName(name);
        outpatientDepartment.setDescription(description);
        outpatientDepartment.setCapacity(capacity);
        outpatientDepartment.setOpeningHours(openingHours);
        outpatientDepartment.setActive(active);
        return outpatientDepartment;
    }

    private OpeningHours setOpeningHours(String monday, String tuesday, String wednesday, String thursday, String friday, String saturday, String sunday) {
        OpeningHours openingHours = new OpeningHours();
        openingHours.setMonday(monday);
        openingHours.setTuesday(tuesday);
        openingHours.setWednesday(wednesday);
        openingHours.setThursday(thursday);
        openingHours.setFriday(friday);
        openingHours.setSaturday(saturday);
        openingHours.setSunday(sunday);
        return openingHours;
    }

    private void generateDataForInpatientDepartments() {
        inpatientDepartmentRepository.save(setInpatientDepartment("InpatientDepartment1", 5, true));
        inpatientDepartmentRepository.save(setInpatientDepartment("InpatientDepartment2", 10, true));
        inpatientDepartmentRepository.save(setInpatientDepartment("InpatientDepartment3", 15, true));
        inpatientDepartmentRepository.save(setInpatientDepartment("InpatientDepartment4", 20, true));
        inpatientDepartmentRepository.save(setInpatientDepartment("InpatientDepartment5", 25, true));
    }

    private InpatientDepartment setInpatientDepartment(String name, int capacity, boolean active) {
        InpatientDepartment inpatientDepartment = new InpatientDepartment();
        inpatientDepartment.setName(name);
        inpatientDepartment.setCapacity(capacity);
        inpatientDepartment.setActive(active);
        return inpatientDepartment;
    }

    private void generateDataForAppointments() {
        //Three appointments for the same outpatient department at the same time to fill up the capacity
        appointmentRepository.save(setAppointment(patientRepository.findAll().get(0), outpatientDepartmentRepository.findAll().get(0), dateFromLocalDateTime(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 0)),
            dateFromLocalDateTime(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 30)), "Notes1"));

        appointmentRepository.save(setAppointment(patientRepository.findAll().get(1), outpatientDepartmentRepository.findAll().get(0), dateFromLocalDateTime(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 0)),
            dateFromLocalDateTime(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 30)), "Notes1"));

        appointmentRepository.save(setAppointment(patientRepository.findAll().get(2), outpatientDepartmentRepository.findAll().get(0), dateFromLocalDateTime(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 0)),
            dateFromLocalDateTime(LocalDateTime.of(2023, Month.JANUARY, 1, 8, 30)), "Notes1"));

        appointmentRepository.save(setAppointment(patientRepository.findAll().get(0), outpatientDepartmentRepository.findAll().get(0), Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 8, 0).atZone(ZoneId.systemDefault()).toInstant()),
            Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 8, 30).atZone(ZoneId.systemDefault()).toInstant()), "Notes1"));
        appointmentRepository.save(setAppointment(patientRepository.findAll().get(1), outpatientDepartmentRepository.findAll().get(0), Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 9, 0).atZone(ZoneId.systemDefault()).toInstant()),
            Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 9, 30).atZone(ZoneId.systemDefault()).toInstant()), "Notes2"));
        appointmentRepository.save(setAppointment(patientRepository.findAll().get(2), outpatientDepartmentRepository.findAll().get(0), Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 10, 0).atZone(ZoneId.systemDefault()).toInstant()),
            Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 10, 30).atZone(ZoneId.systemDefault()).toInstant()), "Notes3"));

        //Two appointments for the same outpatient department at the same time - one spot left
        appointmentRepository.save(setAppointment(patientRepository.findAll().get(3), outpatientDepartmentRepository.findAll().get(0), Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 11, 0).atZone(ZoneId.systemDefault()).toInstant()),
            Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 11, 30).atZone(ZoneId.systemDefault()).toInstant()), "Notes4"));
        appointmentRepository.save(setAppointment(patientRepository.findAll().get(4), outpatientDepartmentRepository.findAll().get(0), Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 12, 0).atZone(ZoneId.systemDefault()).toInstant()),
            Date.from(LocalDateTime.of(2022, Month.JANUARY, 1, 12, 30).atZone(ZoneId.systemDefault()).toInstant()), "Notes5"));
    }

    private Date dateFromLocalDateTime(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Appointment setAppointment(Patient patient, OutpatientDepartment outpatientDepartment, Date startDate, Date endDate, String notes) {
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setOutpatientDepartment(outpatientDepartment);
        appointment.setStartDate(startDate);
        appointment.setEndDate(endDate);
        appointment.setNotes(notes);
        return appointment;
    }

    private Date createDate(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        return calendar.getTime();
    }

    private void generateDataForTreatmentMedicines() {
        treatmentMedicineRepository.save(setTreatmentMedicine(medicationRepository.findAll().get(0), 50L, "mg", createDate(2022, Calendar.JANUARY, 1, 8, 0)));
        treatmentMedicineRepository.save(setTreatmentMedicine(medicationRepository.findAll().get(1), 100L, "mg", createDate(2022, Calendar.JANUARY, 1, 8, 0)));
        treatmentMedicineRepository.save(setTreatmentMedicine(medicationRepository.findAll().get(2), 150L, "mg", createDate(2022, Calendar.JANUARY, 1, 8, 0)));
    }

    private TreatmentMedicine setTreatmentMedicine(Medication medication, Long amount, String unitOfMeasurement, Date timeOfAdministration) {
        TreatmentMedicine treatmentMedicine = new TreatmentMedicine();
        treatmentMedicine.setMedicine(medication);
        treatmentMedicine.setAmount(amount);
        treatmentMedicine.setUnitOfMeasurement(unitOfMeasurement);
        treatmentMedicine.setTimeOfAdministration(timeOfAdministration);
        return treatmentMedicine;
    }

    private void generateDataForTreatments() {
        List<TreatmentMedicine> medicine = new ArrayList<>();
        medicine.add(treatmentMedicineRepository.findAll().get(0));
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(doctorRepository.findAll().get(0));
        treatmentRepository.save(
            setTreatment("Treatment1", new Date(2022, Calendar.JANUARY, 1, 8, 0), new Date(2022, Calendar.JANUARY, 1, 9, 0), patientRepository.findAll().get(0), outpatientDepartmentRepository.findAll().get(0), "Text1", doctors, List.of()));
        treatmentRepository.save(
            setTreatment("Treatment2", new Date(2022, Calendar.JANUARY, 1, 8, 0), new Date(2022, Calendar.JANUARY, 1, 9, 0), patientRepository.findAll().get(1), outpatientDepartmentRepository.findAll().get(0), "Text2", doctors, medicine));
        List<Doctor> doctors2 = new ArrayList<>();
        doctors2.add(doctorRepository.findAll().get(0));
        doctors2.add(doctorRepository.findAll().get(1));
        medicine = new ArrayList<>();
        medicine.add(treatmentMedicineRepository.findAll().get(1));
        medicine.add(treatmentMedicineRepository.findAll().get(2));
        treatmentRepository.save(
            setTreatment("Treatment3", new Date(2022, Calendar.JANUARY, 1, 8, 0), new Date(2022, Calendar.JANUARY, 1, 9, 0), patientRepository.findAll().get(2), outpatientDepartmentRepository.findAll().get(0), "Text3", doctors2, medicine));
    }

    private Treatment setTreatment(String treatmentTitle, Date treatmentStart, Date treatmentEnd, Patient patient, OutpatientDepartment outpatientDepartment, String treatmentText, List<Doctor> doctors, List<TreatmentMedicine> medicines) {
        Treatment treatment = new Treatment();
        treatment.setTreatmentTitle(treatmentTitle);
        treatment.setTreatmentStart(treatmentStart);
        treatment.setTreatmentEnd(treatmentEnd);
        treatment.setPatient(patient);
        treatment.setOutpatientDepartment(outpatientDepartment);
        treatment.setTreatmentText(treatmentText);
        treatment.setDoctors(doctors);
        treatment.setMedicines(medicines);
        return treatment;
    }
}

