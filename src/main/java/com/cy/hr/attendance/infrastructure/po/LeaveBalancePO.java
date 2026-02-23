package com.cy.hr.attendance.infrastructure.po;

/**
 * 文件说明：LeaveBalancePO
 */
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("leave_balances")
public class LeaveBalancePO {

    /** 员工ID */
    private String employeeId;
    /** 假期类型 */
    private String leaveType;
    /** 余额天数 */
    private Integer balanceDays;
}
