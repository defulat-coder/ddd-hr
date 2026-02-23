package com.cy.hr.attendance.interfaces.rest;

/**
 * 文件说明：LeaveController
 */
import com.cy.hr.attendance.application.ApplyLeaveCommand;
import com.cy.hr.attendance.application.LeaveApplicationService;
import com.cy.hr.attendance.application.ReviewLeaveCommand;
import com.cy.hr.attendance.domain.LeaveApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@Tag(name = "考勤管理")
public class LeaveController {

    private final LeaveApplicationService service;

    @PostMapping("/apply")
    @Operation(summary = "提交请假申请")
    public LeaveResponse apply(@Valid @RequestBody ApplyLeaveCommand command) {
        return toResponse(service.apply(command));
    }

    @PostMapping("/{leaveId}/review")
    @Operation(summary = "审批请假申请")
    public LeaveResponse review(@Parameter(description = "请假申请ID") @PathVariable String leaveId,
                                @Parameter(description = "审批动作（APPROVE/REJECT）") @RequestParam @NotBlank String action,
                                @Parameter(description = "驳回原因") @RequestParam(required = false) String rejectReason) {
        return toResponse(service.review(new ReviewLeaveCommand(leaveId, action, rejectReason)));
    }

    @PostMapping("/balances")
    @Operation(summary = "设置假期余额")
    public void setBalance(@Parameter(description = "员工ID") @RequestParam @NotBlank String employeeId,
                           @Parameter(description = "假期类型") @RequestParam @NotBlank String leaveType,
                           @Parameter(description = "余额天数") @RequestParam @Min(0) int days) {
        service.setBalance(employeeId, leaveType, days);
    }

    @GetMapping
    @Operation(summary = "查询请假申请列表")
    public List<LeaveResponse> list() {
        return service.list().stream().map(this::toResponse).toList();
    }

    private LeaveResponse toResponse(LeaveApplication leaveApplication) {
        return new LeaveResponse(
                leaveApplication.getId(),
                leaveApplication.getEmployeeId(),
                leaveApplication.getLeaveType(),
                leaveApplication.getStartDate().toString(),
                leaveApplication.getEndDate().toString(),
                leaveApplication.getDays(),
                leaveApplication.getReason(),
                leaveApplication.getStatus().name(),
                leaveApplication.getRejectReason()
        );
    }

    @Schema(name = "LeaveResponse", description = "请假申请响应")
    public record LeaveResponse(
            @Schema(description = "申请ID") String id,
            @Schema(description = "员工ID") String employeeId,
            @Schema(description = "请假类型") String leaveType,
            @Schema(description = "开始日期") String startDate,
            @Schema(description = "结束日期") String endDate,
            @Schema(description = "请假天数") int days,
            @Schema(description = "请假原因") String reason,
            @Schema(description = "审批状态") String status,
            @Schema(description = "驳回原因") String rejectReason
    ) {
    }
}
