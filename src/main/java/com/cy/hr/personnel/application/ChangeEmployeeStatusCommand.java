package com.cy.hr.personnel.application;

/**
 * 文件说明：ChangeEmployeeStatusCommand
 */
public record ChangeEmployeeStatusCommand(
        String employeeId,
        String targetDepartmentId,
        String targetPosition
) {
}
