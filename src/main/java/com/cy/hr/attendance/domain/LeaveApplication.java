package com.cy.hr.attendance.domain;

/**
 * 文件说明：LeaveApplication
 */
import com.cy.hr.shared.domain.DomainException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LeaveApplication {

    /** 请假申请ID */
    private final String id;
    /** 员工ID */
    private final String employeeId;
    /** 请假类型 */
    private final String leaveType;
    /** 开始日期 */
    private final LocalDate startDate;
    /** 结束日期 */
    private final LocalDate endDate;
    /** 请假天数 */
    private final int days;
    /** 请假原因 */
    private final String reason;
    /** 审批状态 */
    private LeaveStatus status;
    /** 驳回原因 */
    private String rejectReason;

    /**
     * 提交请假申请。
     */
    public static LeaveApplication apply(String employeeId,
                                         String leaveType,
                                         LocalDate startDate,
                                         LocalDate endDate,
                                         int days,
                                         String reason) {
        // 业务规则：申请单关键字段必须完整
        if (employeeId == null || employeeId.isBlank()) {
            throw new DomainException("员工ID不能为空");
        }
        if (leaveType == null || leaveType.isBlank()) {
            throw new DomainException("请假类型不能为空");
        }
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new DomainException("请假时间不合法");
        }
        // 业务规则：请假天数必须大于0
        if (days <= 0) {
            throw new DomainException("请假天数必须大于0");
        }
        return new LeaveApplication(
                UUID.randomUUID().toString(),
                employeeId,
                leaveType,
                startDate,
                endDate,
                days,
                reason,
                LeaveStatus.PENDING,
                null
        );
    }

    /**
     * 从持久化数据恢复请假申请聚合。
     */
    public static LeaveApplication restore(String id,
                                           String employeeId,
                                           String leaveType,
                                           LocalDate startDate,
                                           LocalDate endDate,
                                           int days,
                                           String reason,
                                           LeaveStatus status,
                                           String rejectReason) {
        return new LeaveApplication(id, employeeId, leaveType, startDate, endDate, days, reason, status, rejectReason);
    }

    /**
     * 审批通过请假申请。
     */
    public void approve() {
        // 业务规则：仅待审批状态可通过
        if (status != LeaveStatus.PENDING) {
            throw new DomainException("当前状态不可审批");
        }
        status = LeaveStatus.APPROVED;
    }

    /**
     * 驳回请假申请。
     */
    public void reject(String rejectReason) {
        // 业务规则：仅待审批状态可驳回
        if (status != LeaveStatus.PENDING) {
            throw new DomainException("当前状态不可驳回");
        }
        status = LeaveStatus.REJECTED;
        this.rejectReason = rejectReason;
    }

}
