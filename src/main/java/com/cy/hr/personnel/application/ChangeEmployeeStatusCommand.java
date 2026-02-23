package com.cy.hr.personnel.application;

/**
 * 文件说明：ChangeEmployeeStatusCommand
 */
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ChangeEmployeeStatusCommand", description = "员工状态变更请求")
public record ChangeEmployeeStatusCommand(
        @Schema(description = "员工ID")
        String employeeId,
        @Schema(description = "目标部门ID")
        String targetDepartmentId,
        @Schema(description = "目标岗位")
        String targetPosition
) {
}
