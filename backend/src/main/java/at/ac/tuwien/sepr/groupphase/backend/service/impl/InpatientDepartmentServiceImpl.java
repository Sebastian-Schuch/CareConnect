package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.InpatientDepartmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.InpatientDepartmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.InpatientDepartmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.InpatientDepartmentMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.InpatientDepartment;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.InpatientDepartmentRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.InpatientDepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class InpatientDepartmentServiceImpl implements InpatientDepartmentService {
    private final InpatientDepartmentMapper inpatientDepartmentMapper;
    InpatientDepartmentRepository inpatientDepartmentRepository;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public InpatientDepartmentServiceImpl(InpatientDepartmentRepository inpatientDepartmentRepository, InpatientDepartmentMapper inpatientDepartmentMapper) {
        this.inpatientDepartmentRepository = inpatientDepartmentRepository;
        this.inpatientDepartmentMapper = inpatientDepartmentMapper;
    }

    @Override
    public InpatientDepartment findById(Long id) {
        LOG.trace("findById({})", id);
        try {
            if (id == null) {
                LOG.warn("Id is null");
                throw new IllegalArgumentException("Id is null");
            }
            InpatientDepartment data = inpatientDepartmentRepository.findByIdAndActiveTrue(id);
            if (data == null) {
                LOG.warn("Could not find inpatient department with id {}", id);
                throw new NotFoundException("Could not find inpatient department");
            } else {
                return data;
            }

        } catch (IllegalArgumentException e) {
            LOG.warn("Can not search for null id.");
            throw new IllegalArgumentException("Can not search for null id.");
        }
    }

    @Override
    public InpatientDepartmentPageDto findAll(Specification<InpatientDepartment> spec, Pageable pageable) {
        LOG.trace("findAll({},{})", spec, pageable);
        Page<InpatientDepartment> inpatientDepartments = inpatientDepartmentRepository.findAll(spec, pageable);
        return inpatientDepartmentMapper.toInpatientDepartmentPageDto(inpatientDepartments);
    }

    @Override
    public InpatientDepartment createInpatientDepartment(InpatientDepartmentDtoCreate toCreate) {
        LOG.trace("createInpatientDepartment({})", toCreate);
        InpatientDepartment inpatientDepartment = new InpatientDepartment();
        inpatientDepartment.setName(toCreate.name());
        inpatientDepartment.setCapacity(toCreate.capacity());
        inpatientDepartment.setActive(true);
        return inpatientDepartmentRepository.save(inpatientDepartment);
    }

    @Override
    public InpatientDepartmentDto updateInpatientDepartment(InpatientDepartmentDto toUpdate) {
        LOG.trace("updateInpatientDepartment({})", toUpdate);
        InpatientDepartment inpatientDepartmentToUpdate = inpatientDepartmentRepository.findById(toUpdate.id()).orElseThrow(() -> new NotFoundException("Inpatient department not found"));
        inpatientDepartmentToUpdate.setName(toUpdate.name());
        inpatientDepartmentToUpdate.setCapacity(toUpdate.capacity());
        return inpatientDepartmentMapper.inpatientDepartmentToDto(inpatientDepartmentRepository.save(inpatientDepartmentToUpdate));
    }

    @Override
    public InpatientDepartmentDto deleteInpatientDepartment(Long id) {
        LOG.trace("deleteInpatientDepartment({})", id);
        InpatientDepartment toDisable = inpatientDepartmentRepository.findById(id).orElseThrow(() -> new NotFoundException("Inpatient department not found"));
        inpatientDepartmentRepository.save(toDisable.setActive(false));
        return inpatientDepartmentMapper.inpatientDepartmentToDto(toDisable);
    }
}
