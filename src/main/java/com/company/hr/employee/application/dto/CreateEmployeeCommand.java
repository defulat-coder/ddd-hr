package com.company.hr.employee.application.dto;

import com.company.hr.employee.domain.model.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 创建员工命令
 */
@Data
@Schema(description = "创建员工请求")
public class CreateEmployeeCommand {
    @Schema(description = "名", example = "三")
    private String firstName;
    @Schema(description = "姓", example = "张")
    private String lastName;
    @Schema(description = "身份证号", example = "110101199001011234")
    private String idCardNumber;
    @Schema(description = "出生日期", example = "1990-01-01")
    private LocalDate birthDate;
    @Schema(description = "性别")
    private Gender gender;
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;
    @Schema(description = "手机号", example = "13800138000")
    private String phoneNumber;
    @Schema(description = "住址", example = "北京市朝阳区")
    private String address;
    @Schema(description = "紧急联系人", example = "李四")
    private String emergencyContact;
    @Schema(description = "紧急联系人电话", example = "13900139000")
    private String emergencyPhone;
    @Schema(description = "部门ID")
    private String departmentId;
    @Schema(description = "职位ID")
    private String positionId;
}
