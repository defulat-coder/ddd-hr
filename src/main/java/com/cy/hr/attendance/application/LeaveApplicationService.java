package com.cy.hr.attendance.application;

/**
 * 文件说明：LeaveApplicationService
 */
import com.cy.hr.attendance.domain.LeaveApplication;
import com.cy.hr.attendance.domain.LeaveApplicationRepository;
import com.cy.hr.attendance.domain.LeaveBalanceRepository;
import com.cy.hr.personnel.domain.EmployeeRepository;
import com.cy.hr.shared.domain.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveApplicationService {

    private final LeaveApplicationRepository leaveApplicationRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * 提交请假申请。
     */
    public LeaveApplication apply(ApplyLeaveCommand command) {
        // 业务规则：请假人必须是有效员工
        employeeRepository.findById(command.employeeId())
                .orElseThrow(() -> new DomainException("员工不存在"));

        // 业务规则：请假申请前先校验余额
        int balance = leaveBalanceRepository.getBalanceDays(command.employeeId(), command.leaveType());
        if (balance < command.days()) {
            throw new DomainException("假期余额不足");
        }

        LeaveApplication leaveApplication = LeaveApplication.apply(
                command.employeeId(),
                command.leaveType(),
                command.startDate(),
                command.endDate(),
                command.days(),
                command.reason()
        );

        return leaveApplicationRepository.save(leaveApplication);
    }

    /**
     * 审批请假申请。
     */
    public LeaveApplication review(ReviewLeaveCommand command) {
        LeaveApplication application = leaveApplicationRepository.findById(command.leaveApplicationId())
                .orElseThrow(() -> new DomainException("请假申请不存在"));

        if ("APPROVE".equalsIgnoreCase(command.action())) {
            application.approve();
            // 业务规则：审批通过后才扣减余额
            leaveBalanceRepository.deduct(application.getEmployeeId(), application.getLeaveType(), application.getDays());
        } else if ("REJECT".equalsIgnoreCase(command.action())) {
            application.reject(command.rejectReason());
        } else {
            throw new DomainException("审批动作不支持");
        }

        return leaveApplicationRepository.save(application);
    }

    /**
     * 设置员工假期余额。
     */
    public void setBalance(String employeeId, String leaveType, int days) {
        leaveBalanceRepository.setBalance(employeeId, leaveType, days);
    }

    /**
     * 查询请假申请列表。
     */
    public List<LeaveApplication> list() {
        return leaveApplicationRepository.findAll();
    }
}
