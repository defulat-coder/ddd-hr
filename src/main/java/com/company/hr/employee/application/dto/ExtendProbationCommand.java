package com.company.hr.employee.application.dto;

import lombok.Data;

/**
 * 延长试用期命令
 */
@Data
public class ExtendProbationCommand {
    private String employeeId;
    private Integer months;
    private String reason;
}

