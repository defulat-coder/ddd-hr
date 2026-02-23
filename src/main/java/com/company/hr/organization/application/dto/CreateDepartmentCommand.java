package com.company.hr.organization.application.dto;

import com.company.hr.organization.domain.model.DepartmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建部门请求")
public class CreateDepartmentCommand {
    @Schema(description = "部门名称", example = "研发中心")
    private String name;
    @Schema(description = "部门编码", example = "RD")
    private String code;
    @Schema(description = "部门类型")
    private DepartmentType type;
    @Schema(description = "父部门ID")
    private String parentId;
    @Schema(description = "负责人员工ID")
    private String managerId;
    @Schema(description = "描述")
    private String description;
}
