package com.cy.hr.personnel.application;

/**
 * 文件说明：OnboardEmployeeCommand
 */
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record OnboardEmployeeCommand(
        @NotBlank String name,
        @NotBlank String gender,
        @NotNull LocalDate birthDate,
        @NotBlank String idCardNo,
        @NotBlank String phone,
        @NotBlank String departmentId,
        @NotBlank String position,
        @NotNull LocalDate onboardingDate,
        @NotBlank String contractType,
        @NotNull LocalDate contractStartDate,
        @NotNull LocalDate contractEndDate,
        int probationMonths,
        @NotNull LocalDate contractSignedDate
) {
}
