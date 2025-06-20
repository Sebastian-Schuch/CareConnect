package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DataInterfaceDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorWorkingHoursDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MedicationAmountDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.DataInterfaceMapper;
import at.ac.tuwien.sepr.groupphase.backend.repository.TreatmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DataInterfaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class DataInterfaceServiceImpl implements DataInterfaceService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TreatmentRepository treatmentRepository;
    private final DataInterfaceMapper dataInterfaceMapper;


    public DataInterfaceServiceImpl(TreatmentRepository treatmentRepository,
                                    DataInterfaceMapper dataInterfaceMapper) {
        this.treatmentRepository = treatmentRepository;
        this.dataInterfaceMapper = dataInterfaceMapper;
    }

    public List<DoctorWorkingHoursDto> getDoctorWorkingHours(DataInterfaceDtoSearch search) {
        LOG.trace("getDoctorWorkingHours({})", search);
        List<Object[]> summedHours = treatmentRepository.findTreatmentAndSumHoursGroupedByDoctors(search.startInterval(), search.endInterval());
        return dataInterfaceMapper.doctorHoursObjectToDoctorWorkingHoursDto(summedHours);
    }

    public List<MedicationAmountDto> getMedicationAmounts(DataInterfaceDtoSearch search) {
        LOG.trace("getMedicationAmounts({})", search);
        List<Object[]> summedAmounts = treatmentRepository.findTreatmentAndSumAmountGroupedByMedication(search.startInterval(), search.endInterval());
        return dataInterfaceMapper.medicationAmountObjectToMedicationAmountDto(summedAmounts);
    }
}
