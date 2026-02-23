package com.cy.hr.payroll.application;

/**
 * 文件说明：SaveSalaryProfileCommand
 */
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(name = "SaveSalaryProfileCommand", description = "保存薪资档案请求")
public record SaveSalaryProfileCommand(
        @Schema(description = "员工ID")
        @NotBlank String employeeId,
        @Schema(description = "基本工资")
        @NotNull @DecimalMin("0.0") BigDecimal baseSalary,
        @Schema(description = "岗位工资")
        @NotNull @DecimalMin("0.0") BigDecimal positionSalary,
        @Schema(description = "绩效工资")
        @NotNull @DecimalMin("0.0") BigDecimal performanceSalary,
        @Schema(description = "补贴")
        @NotNull @DecimalMin("0.0") BigDecimal allowance
) {
}
