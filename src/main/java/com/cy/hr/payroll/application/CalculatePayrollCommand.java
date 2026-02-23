package com.cy.hr.payroll.application;

/**
 * 文件说明：CalculatePayrollCommand
 */
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(name = "CalculatePayrollCommand", description = "月度薪资核算请求")
public record CalculatePayrollCommand(
        @Schema(description = "员工ID")
        @NotBlank String employeeId,
        @Schema(description = "核算期间（yyyy-MM）")
        @NotBlank String period,
        @Schema(description = "扣款项")
        @NotNull @DecimalMin("0.0") BigDecimal deduction
) {
}
