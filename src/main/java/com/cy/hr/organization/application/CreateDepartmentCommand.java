package com.cy.hr.organization.application;

/**
 * 文件说明：CreateDepartmentCommand
 */
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateDepartmentCommand(
        @NotBlank String name,
        String parentDepartmentId,
        @Min(1) @Max(5) int level,
        @NotBlank String leader,
        @Min(0) int staffingQuota,
        @NotNull LocalDate effectiveDate
) {
}
