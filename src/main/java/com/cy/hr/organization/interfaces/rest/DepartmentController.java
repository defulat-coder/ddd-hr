package com.cy.hr.organization.interfaces.rest;

/**
 * 文件说明：DepartmentController
 */
import com.cy.hr.organization.application.CreateDepartmentCommand;
import com.cy.hr.organization.application.DepartmentApplicationService;
import com.cy.hr.organization.domain.Department;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentApplicationService service;

    @PostMapping
    public DepartmentResponse create(@Valid @RequestBody CreateDepartmentCommand command) {
        return toResponse(service.create(command));
    }

    @GetMapping
    public List<DepartmentResponse> list() {
        return service.list().stream().map(this::toResponse).toList();
    }

    private DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getParentDepartmentId(),
                department.getLevel(),
                department.getLeader(),
                department.getStaffingQuota(),
                department.getEffectiveDate().toString());
    }

    public record DepartmentResponse(
            String id,
            String name,
            String parentDepartmentId,
            int level,
            String leader,
            int staffingQuota,
            String effectiveDate
    ) {
    }
}
