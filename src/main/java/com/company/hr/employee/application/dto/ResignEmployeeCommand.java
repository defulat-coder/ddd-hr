package com.company.hr.employee.application.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 员工离职命令
 */
@Data
public class ResignEmployeeCommand {
    private String employeeId;
    private LocalDate resignDate;
    private String reason;
    private String resignType; // RESIGNATION 或 TERMINATION
}

