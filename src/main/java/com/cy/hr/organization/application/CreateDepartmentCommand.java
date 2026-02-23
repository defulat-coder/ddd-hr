package com.cy.hr.organization.application;

/**
 * 文件说明：CreateDepartmentCommand
 */
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(name = "CreateDepartmentCommand", description = "创建部门请求")
public record CreateDepartmentCommand(
        @Schema(description = "部门名称")
        @NotBlank String name,
        @Schema(description = "上级部门ID")
        String parentDepartmentId,
        @Schema(description = "部门层级")
        @Min(1) @Max(5) int level,
        @Schema(description = "部门负责人")
        @NotBlank String leader,
        @Schema(description = "编制数")
        @Min(0) int staffingQuota,
        @Schema(description = "生效日期")
        @NotNull LocalDate effectiveDate
) {
}
