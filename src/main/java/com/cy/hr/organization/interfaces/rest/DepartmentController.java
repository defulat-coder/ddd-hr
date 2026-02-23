package com.cy.hr.organization.interfaces.rest;

/**
 * 文件说明：DepartmentController
 */
import com.cy.hr.organization.application.CreateDepartmentCommand;
import com.cy.hr.organization.application.DepartmentApplicationService;
import com.cy.hr.organization.domain.Department;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "组织管理")
public class DepartmentController {

    private final DepartmentApplicationService service;

    @PostMapping
    @Operation(summary = "创建部门")
    public DepartmentResponse create(@Valid @RequestBody CreateDepartmentCommand command) {
        return toResponse(service.create(command));
    }

    @GetMapping
    @Operation(summary = "查询部门列表")
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

    @Schema(name = "DepartmentResponse", description = "部门响应")
    public record DepartmentResponse(
            @Schema(description = "部门ID") String id,
            @Schema(description = "部门名称") String name,
            @Schema(description = "上级部门ID") String parentDepartmentId,
            @Schema(description = "部门层级") int level,
            @Schema(description = "部门负责人") String leader,
            @Schema(description = "编制数") int staffingQuota,
            @Schema(description = "生效日期") String effectiveDate
    ) {
    }
}
