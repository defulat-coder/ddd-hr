package com.cy.hr.payroll.infrastructure.po;

/**
 * 文件说明：PayrollRecordPO
 */
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("payroll_records")
public class PayrollRecordPO {

    @TableId
    /** 记录ID */
    private String id;
    /** 员工ID */
    private String employeeId;
    /** 薪资期间 */
    private String period;
    /** 应发工资 */
    private BigDecimal grossSalary;
    /** 扣款 */
    private BigDecimal deduction;
    /** 社保公积金 */
    private BigDecimal socialSecurity;
    /** 个税 */
    private BigDecimal tax;
    /** 实发工资 */
    private BigDecimal netSalary;
}
