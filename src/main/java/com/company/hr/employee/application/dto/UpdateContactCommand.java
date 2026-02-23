package com.company.hr.employee.application.dto;

import lombok.Data;

/**
 * 更新联系信息命令
 */
@Data
public class UpdateContactCommand {
    private String employeeId;
    private String email;
    private String phoneNumber;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
}

