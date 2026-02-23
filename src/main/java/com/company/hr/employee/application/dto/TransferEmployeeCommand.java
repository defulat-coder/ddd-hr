package com.company.hr.employee.application.dto;

import lombok.Data;

/**
 * 员工调动命令
 */
@Data
public class TransferEmployeeCommand {
    private String employeeId;
    private String newDepartmentId;
    private String newPositionId;
    private String reason;
}

