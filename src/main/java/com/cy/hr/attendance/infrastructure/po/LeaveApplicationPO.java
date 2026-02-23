package com.cy.hr.attendance.infrastructure.po;

/**
 * 文件说明：LeaveApplicationPO
 */
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("leave_applications")
public class LeaveApplicationPO {

    @TableId
    /** 请假申请ID */
    private String id;
    /** 员工ID */
    private String employeeId;
    /** 请假类型 */
    private String leaveType;
    /** 开始日期 */
    private LocalDate startDate;
    /** 结束日期 */
    private LocalDate endDate;
    /** 天数 */
    private Integer days;
    /** 原因 */
    private String reason;
    /** 状态 */
    private String status;
    /** 驳回原因 */
    private String rejectReason;
}
