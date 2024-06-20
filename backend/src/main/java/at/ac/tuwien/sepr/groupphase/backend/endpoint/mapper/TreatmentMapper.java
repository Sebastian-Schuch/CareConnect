package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OutpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentMedicineDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import at.ac.tuwien.sepr.groupphase.backend.entity.OutpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.entity.Treatment;
import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import at.ac.tuwien.sepr.groupphase.backend.service.DoctorService;
import at.ac.tuwien.sepr.groupphase.backend.service.OutpatientDepartmentService;
import at.ac.tuwien.sepr.groupphase.backend.service.PatientService;
import at.ac.tuwien.sepr.groupphase.backend.service.TreatmentMedicineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

/**
 * Mapper for converting between Treatment and TreatmentDto objects.
 */


@Service
public class TreatmentMapper {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final OutpatientDepartmentService outpatientDepartmentService;

    private final TreatmentMedicineService treatmentMedicineService;

    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;

    private final TreatmentMedicineMapper treatmentMedicineMapper;


    public TreatmentMapper(PatientService patientService,
                           DoctorService doctorService,
                           OutpatientDepartmentService outpatientDepartmentService,
                           TreatmentMedicineService treatmentMedicineService,
                           DoctorMapper doctorMapper,
                           PatientMapper patientMapper,
                           TreatmentMedicineMapper treatmentMedicineMapper) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.outpatientDepartmentService = outpatientDepartmentService;
        this.treatmentMedicineService = treatmentMedicineService;
        this.doctorMapper = doctorMapper;
        this.patientMapper = patientMapper;
        this.treatmentMedicineMapper = treatmentMedicineMapper;
    }

    /**
     * Converts a TreatmentDtoCreate object to a Treatment object.
     *
     * @param treatmentDtoCreate the TreatmentDtoCreate object to convert
     * @return the converted Treatment object
     */
    public Treatment dtoToEntity(TreatmentDtoCreate treatmentDtoCreate) {
        LOG.trace("dtoToEntity({})", treatmentDtoCreate);

        List<Doctor> doctors = new LinkedList<>();
        for (DoctorDtoSparse doctorDto : treatmentDtoCreate.doctors()) {
            doctors.add(doctorService.getDoctorEntityById(doctorDto.id()));
        }

        List<TreatmentMedicine> medicines = new LinkedList<>();
        for (TreatmentMedicineDto treatmentMedicineDto : treatmentDtoCreate.medicines()) {
            medicines.add(treatmentMedicineService.getTreatmentMedicineEntityById(treatmentMedicineDto.id()));
        }

        OutpatientDepartment outpatientDepartment = this.outpatientDepartmentService.getOutpatientDepartmentEntityById(treatmentDtoCreate.outpatientDepartment().id());

        Treatment t = new Treatment();
        t.setTreatmentTitle(treatmentDtoCreate.treatmentTitle());
        t.setTreatmentText(treatmentDtoCreate.treatmentText());
        t.setPatient(patientService.getPatientEntityById(treatmentDtoCreate.patient().id()));
        t.setDoctors(doctors);
        t.setMedicines(medicines);
        t.setOutpatientDepartment(outpatientDepartment);
        t.setTreatmentStart(treatmentDtoCreate.treatmentStart());
        t.setTreatmentEnd(treatmentDtoCreate.treatmentEnd());
        return t;
    }

    /**
     * Converts a Treatment object to a TreatmentDto object.
     *
     * @param treatment the Treatment entity to convert.
     * @return the converted TreatmentDto object
     */
    public TreatmentDto entityToDto(Treatment treatment) {
        LOG.trace("entityToDto({})", treatment);

        List<DoctorDtoSparse> doctors = new LinkedList<>();
        List<Doctor> doc = treatment.getDoctors();
        for (Doctor doctor : doc) {
            doctors.add(doctorMapper.doctorToDoctorDtoSparse(doctor));
        }

        List<TreatmentMedicineDto> medicines = new LinkedList<>();
        for (TreatmentMedicine treatmentMedicine : treatment.getMedicines()) {
            medicines.add(treatmentMedicineMapper.entityToDto(treatmentMedicine));
        }

        PatientDtoSparse patient = patientMapper.patientToPatientDtoSparse(treatment.getPatient());
        OutpatientDepartmentDto outpatientDepartmentDto = outpatientDepartmentService.getOutpatientDepartmentById(treatment.getOutpatientDepartment().getId());

        return new TreatmentDto(
            treatment.getId(),
            treatment.getTreatmentTitle(),
            treatment.getTreatmentStart(),
            treatment.getTreatmentEnd(),
            patient,
            outpatientDepartmentDto,
            treatment.getTreatmentText(),
            doctors,
            medicines);
    }

    public List<TreatmentDto> entityListToDtoList(List<Treatment> treatments) {
        List<TreatmentDto> treatmentDtos = new LinkedList<>();
        for (Treatment treatment : treatments) {
            treatmentDtos.add(entityToDto(treatment));
        }
        return treatmentDtos;
    }

    public TreatmentPageDto toTreatmentPageDto(Page<Treatment> treatmentPage) {
        return new TreatmentPageDto(
            entityListToDtoList(treatmentPage.getContent()),
            (int) treatmentPage.getTotalElements()
        );
    }
}
