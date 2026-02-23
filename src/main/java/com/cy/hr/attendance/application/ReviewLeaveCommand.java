package com.cy.hr.attendance.application;

/**
 * 文件说明：ReviewLeaveCommand
 */
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "ReviewLeaveCommand", description = "请假审批请求")
public record ReviewLeaveCommand(
        @Schema(description = "请假申请ID")
        @NotBlank String leaveApplicationId,
        @Schema(description = "审批动作（APPROVE/REJECT）")
        @NotBlank String action,
        @Schema(description = "驳回原因")
        String rejectReason
) {
}
