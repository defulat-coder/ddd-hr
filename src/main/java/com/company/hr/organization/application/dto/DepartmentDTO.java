package com.company.hr.organization.application.dto;

import com.company.hr.organization.domain.model.Department;
import com.company.hr.organization.domain.model.DepartmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@Schema(description = "部门响应")
public class DepartmentDTO {
    private String id;
    private String name;
    private String code;
    private DepartmentType type;
    private String parentId;
    private String managerId;
    private String description;
    private Boolean active;
    private List<PositionDTO> positions;

    public static DepartmentDTO fromDomain(Department department) {
        return DepartmentDTO.builder()
            .id(department.getId().getValue())
            .name(department.getName())
            .code(department.getCode())
            .type(department.getType())
            .parentId(department.getParentId() == null ? null : department.getParentId().getValue())
            .managerId(department.getManagerId() == null ? null : department.getManagerId().getValue())
            .description(department.getDescription())
            .active(department.isActive())
            .positions(department.getPositions().stream().map(PositionDTO::fromDomain).collect(Collectors.toList()))
            .build();
    }
}
