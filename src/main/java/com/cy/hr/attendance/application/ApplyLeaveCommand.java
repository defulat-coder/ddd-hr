package com.cy.hr.attendance.application;

/**
 * 文件说明：ApplyLeaveCommand
 */
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(name = "ApplyLeaveCommand", description = "请假申请请求")
public record ApplyLeaveCommand(
        @Schema(description = "员工ID")
        @NotBlank String employeeId,
        @Schema(description = "请假类型")
        @NotBlank String leaveType,
        @Schema(description = "开始日期")
        @NotNull LocalDate startDate,
        @Schema(description = "结束日期")
        @NotNull LocalDate endDate,
        @Schema(description = "请假天数")
        @Min(1) int days,
        @Schema(description = "请假原因")
        @NotBlank String reason
) {
}
