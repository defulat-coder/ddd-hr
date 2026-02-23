package com.cy.hr.personnel.application;

/**
 * 文件说明：OnboardEmployeeCommand
 */
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(name = "OnboardEmployeeCommand", description = "员工入职请求")
public record OnboardEmployeeCommand(
        @Schema(description = "姓名")
        @NotBlank String name,
        @Schema(description = "性别")
        @NotBlank String gender,
        @Schema(description = "出生日期")
        @NotNull LocalDate birthDate,
        @Schema(description = "身份证号")
        @NotBlank String idCardNo,
        @Schema(description = "联系电话")
        @NotBlank String phone,
        @Schema(description = "部门ID")
        @NotBlank String departmentId,
        @Schema(description = "岗位")
        @NotBlank String position,
        @Schema(description = "入职日期")
        @NotNull LocalDate onboardingDate,
        @Schema(description = "合同类型")
        @NotBlank String contractType,
        @Schema(description = "合同开始日期")
        @NotNull LocalDate contractStartDate,
        @Schema(description = "合同结束日期")
        @NotNull LocalDate contractEndDate,
        @Schema(description = "试用期（月）")
        int probationMonths,
        @Schema(description = "合同签订日期")
        @NotNull LocalDate contractSignedDate
) {
}
