package com.company.hr.employee.application.dto;

import com.company.hr.employee.domain.model.Employee;
import com.company.hr.employee.domain.model.EmployeeStatus;
import com.company.hr.employee.domain.model.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "员工响应对象")
public class EmployeeDTO {
    @Schema(description = "员工ID")
    private String id;
    @Schema(description = "工号")
    private String employeeNumber;
    @Schema(description = "名")
    private String firstName;
    @Schema(description = "姓")
    private String lastName;
    @Schema(description = "全名")
    private String fullName;
    @Schema(description = "身份证号")
    private String idCardNumber;
    @Schema(description = "出生日期")
    private LocalDate birthDate;
    @Schema(description = "性别")
    private Gender gender;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "手机号")
    private String phoneNumber;
    @Schema(description = "住址")
    private String address;
    @Schema(description = "部门ID")
    private String departmentId;
    @Schema(description = "职位ID")
    private String positionId;
    @Schema(description = "员工状态")
    private EmployeeStatus status;
    @Schema(description = "入职日期")
    private LocalDate hireDate;
    @Schema(description = "试用期结束日期")
    private LocalDate probationEndDate;
    @Schema(description = "离职日期")
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
