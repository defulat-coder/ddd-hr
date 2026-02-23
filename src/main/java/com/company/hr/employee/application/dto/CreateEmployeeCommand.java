package com.company.hr.employee.application.dto;

import com.company.hr.employee.domain.model.Gender;
import lombok.Data;

import java.time.LocalDate;

/**
 * 创建员工命令
 */
@Data
public class CreateEmployeeCommand {
    private String firstName;
    private String lastName;
    private String idCardNumber;
    private LocalDate birthDate;
    private Gender gender;
    private String email;
    private String phoneNumber;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private String departmentId;
    private String positionId;
}

