package com.cy.hr.payroll.application;

/**
 * 文件说明：CalculatePayrollCommand
 */
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CalculatePayrollCommand(
        @NotBlank String employeeId,
        @NotBlank String period,
        @NotNull @DecimalMin("0.0") BigDecimal deduction
) {
}
