package com.cy.hr.payroll.infrastructure.po;

/**
 * 文件说明：AttendanceLockPO
 */
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("attendance_locks")
public class AttendanceLockPO {

    @TableId
    /** 锁定期间（yyyy-MM） */
    private String period;
}
