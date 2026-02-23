package com.cy.hr.attendance.domain;

/**
 * 文件说明：LeaveBalanceRepository
 */
public interface LeaveBalanceRepository {

    int getBalanceDays(String employeeId, String leaveType);

    void deduct(String employeeId, String leaveType, int days);

    void setBalance(String employeeId, String leaveType, int days);
}
