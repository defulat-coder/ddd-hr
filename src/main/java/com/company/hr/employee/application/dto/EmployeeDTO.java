package com.company.hr.employee.application.dto;

import com.company.hr.employee.domain.model.Employee;
import com.company.hr.employee.domain.model.EmployeeStatus;
import com.company.hr.employee.domain.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 员工DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private String id;
    private String employeeNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private String idCardNumber;
    private LocalDate birthDate;
    private Gender gender;
    private String email;
    private String phoneNumber;
    private String address;
    private String departmentId;
    private String positionId;
    private EmployeeStatus status;
    private LocalDate hireDate;
    private LocalDate probationEndDate;
    private LocalDate resignDate;
    
    public static EmployeeDTO fromDomain(Employee employee) {
        return EmployeeDTO.builder()
            .id(employee.getId().getValue())
            .employeeNumber(employee.getEmployeeNumber())
            .firstName(employee.getPersonalInfo().getFirstName())
            .lastName(employee.getPersonalInfo().getLastName())
            .fullName(employee.getPersonalInfo().getFullName())
            .idCardNumber(employee.getPersonalInfo().getIdCardNumber())
            .birthDate(employee.getPersonalInfo().getBirthDate())
            .gender(employee.getPersonalInfo().getGender())
            .email(employee.getContactInfo().getEmail())
            .phoneNumber(employee.getContactInfo().getPhoneNumber())
            .address(employee.getContactInfo().getAddress())
            .departmentId(employee.getDepartmentId().getValue())
            .positionId(employee.getPositionId().getValue())
            .status(employee.getStatus())
            .hireDate(employee.getHireDate())
            .probationEndDate(employee.getProbationEndDate())
            .resignDate(employee.getResignDate())
            .build();
    }
}

