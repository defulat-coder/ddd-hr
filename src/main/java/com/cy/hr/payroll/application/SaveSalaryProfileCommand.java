package com.cy.hr.payroll.application;

/**
 * 文件说明：SaveSalaryProfileCommand
 */
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SaveSalaryProfileCommand(
        @NotBlank String employeeId,
        @NotNull @DecimalMin("0.0") BigDecimal baseSalary,
        @NotNull @DecimalMin("0.0") BigDecimal positionSalary,
        @NotNull @DecimalMin("0.0") BigDecimal performanceSalary,
        @NotNull @DecimalMin("0.0") BigDecimal allowance
) {
}
