package com.company.hr.organization.application;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.organization.application.dto.AddPositionCommand;
import com.company.hr.organization.application.dto.CreateDepartmentCommand;
import com.company.hr.organization.application.dto.DepartmentDTO;
import com.company.hr.organization.domain.factory.DepartmentFactory;
import com.company.hr.organization.domain.model.Department;
import com.company.hr.organization.domain.model.DepartmentId;
import com.company.hr.organization.domain.model.Position;
import com.company.hr.organization.domain.repository.DepartmentRepository;
import com.company.hr.shared.event.DomainEventPublisher;
import com.company.hr.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentApplicationService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentFactory departmentFactory;
    private final DomainEventPublisher eventPublisher;

    public DepartmentDTO createDepartment(CreateDepartmentCommand command) {
        departmentRepository.findByCode(command.getCode()).ifPresent(d -> {
            throw new BusinessException("ORG001", "部门编码已存在");
        });

        Department department = departmentFactory.createDepartment(
            command.getName(),
            command.getCode(),
            command.getType(),
            command.getParentId() == null ? null : DepartmentId.of(command.getParentId()),
            command.getManagerId() == null ? null : EmployeeId.of(command.getManagerId()),
            command.getDescription()
        );

        departmentRepository.save(department);
        publishEvents(department);
        return DepartmentDTO.fromDomain(department);
    }

    public void addPosition(AddPositionCommand command) {
        Department department = getDepartment(command.getDepartmentId());
        Position position = departmentFactory.createPosition(
            command.getTitle(),
            command.getCode(),
            command.getLevel(),
            command.getMinSalary(),
            command.getMaxSalary(),
            command.getDescription(),
            command.getMaxHeadcount()
        );
        department.addPosition(position);
        departmentRepository.save(department);
        publishEvents(department);
    }

    public void changeManager(String departmentId, String managerId) {
        Department department = getDepartment(departmentId);
        department.changeManager(EmployeeId.of(managerId));
        departmentRepository.save(department);
        publishEvents(department);
    }

    public void deactivateDepartment(String departmentId) {
        Department department = getDepartment(departmentId);
        department.deactivate();
        departmentRepository.save(department);
        publishEvents(department);
    }

    public void activateDepartment(String departmentId) {
        Department department = getDepartment(departmentId);
        department.activate();
        departmentRepository.save(department);
        publishEvents(department);
    }

    public DepartmentDTO getById(String departmentId) {
        return DepartmentDTO.fromDomain(getDepartment(departmentId));
    }

    public List<DepartmentDTO> getAll() {
        return departmentRepository.findAll().stream().map(DepartmentDTO::fromDomain).collect(Collectors.toList());
    }

    public List<DepartmentDTO> getTopLevel() {
        return departmentRepository.findTopLevelDepartments().stream().map(DepartmentDTO::fromDomain).collect(Collectors.toList());
    }

    public List<DepartmentDTO> getActive() {
        return departmentRepository.findActiveDepartments().stream().map(DepartmentDTO::fromDomain).collect(Collectors.toList());
    }

    private Department getDepartment(String departmentId) {
        return departmentRepository.findById(DepartmentId.of(departmentId))
            .orElseThrow(() -> new BusinessException("ORG404", "部门不存在"));
    }

    private void publishEvents(Department department) {
        eventPublisher.publishAll(department.getDomainEvents());
        department.clearDomainEvents();
    }
}
