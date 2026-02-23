package com.cy.hr.organization.application;

/**
 * 文件说明：DepartmentApplicationService
 */
import com.cy.hr.organization.domain.Department;
import com.cy.hr.organization.domain.DepartmentRepository;
import com.cy.hr.shared.domain.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentApplicationService {

    private final DepartmentRepository repository;

    /**
     * 创建部门。
     */
    public Department create(CreateDepartmentCommand command) {
        if (command.parentDepartmentId() != null && !command.parentDepartmentId().isBlank()) {
            repository.findById(command.parentDepartmentId())
                    .orElseThrow(() -> new DomainException("上级部门不存在"));
        }

        Department department = Department.create(
                command.name(),
                command.parentDepartmentId(),
                command.level(),
                command.leader(),
                command.staffingQuota(),
                command.effectiveDate()
        );
        return repository.save(department);
    }

    /**
     * 查询部门列表。
     */
    public List<Department> list() {
        return repository.findAll();
    }
}
