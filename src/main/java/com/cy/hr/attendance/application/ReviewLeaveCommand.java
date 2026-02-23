package com.cy.hr.attendance.application;

/**
 * 文件说明：ReviewLeaveCommand
 */
import jakarta.validation.constraints.NotBlank;

public record ReviewLeaveCommand(
        @NotBlank String leaveApplicationId,
        @NotBlank String action,
        String rejectReason
) {
}
