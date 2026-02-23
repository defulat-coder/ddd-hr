package com.cy.hr.attendance.application;

/**
 * 文件说明：ApplyLeaveCommand
 */
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ApplyLeaveCommand(
        @NotBlank String employeeId,
        @NotBlank String leaveType,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @Min(1) int days,
        @NotBlank String reason
) {
}
