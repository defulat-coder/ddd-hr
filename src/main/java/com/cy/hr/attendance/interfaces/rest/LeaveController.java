package com.cy.hr.attendance.interfaces.rest;

/**
 * 文件说明：LeaveController
 */
import com.cy.hr.attendance.application.ApplyLeaveCommand;
import com.cy.hr.attendance.application.LeaveApplicationService;
import com.cy.hr.attendance.application.ReviewLeaveCommand;
import com.cy.hr.attendance.domain.LeaveApplication;
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
public class LeaveController {

    private final LeaveApplicationService service;

    @PostMapping("/apply")
    public LeaveResponse apply(@Valid @RequestBody ApplyLeaveCommand command) {
        return toResponse(service.apply(command));
    }

    @PostMapping("/{leaveId}/review")
    public LeaveResponse review(@PathVariable String leaveId,
                                @RequestParam @NotBlank String action,
                                @RequestParam(required = false) String rejectReason) {
        return toResponse(service.review(new ReviewLeaveCommand(leaveId, action, rejectReason)));
    }

    @PostMapping("/balances")
    public void setBalance(@RequestParam @NotBlank String employeeId,
                           @RequestParam @NotBlank String leaveType,
                           @RequestParam @Min(0) int days) {
        service.setBalance(employeeId, leaveType, days);
    }

    @GetMapping
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

    public record LeaveResponse(
            String id,
            String employeeId,
            String leaveType,
            String startDate,
            String endDate,
            int days,
            String reason,
            String status,
            String rejectReason
    ) {
    }
}
