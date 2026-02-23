package com.company.hr.employee.application.dto;

import lombok.Data;

/**
 * 员工晋升命令
 */
@Data
public class PromoteEmployeeCommand {
    private String employeeId;
    private String newPositionId;
    private String reason;
}

