package com.cy.hr.payroll.infrastructure.po;

/**
 * 文件说明：SalaryProfilePO
 */
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("salary_profiles")
public class SalaryProfilePO {

    @TableId
    /** 员工ID */
    private String employeeId;
    /** 基本工资 */
    private BigDecimal baseSalary;
    /** 岗位工资 */
    private BigDecimal positionSalary;
    /** 绩效工资 */
    private BigDecimal performanceSalary;
    /** 补贴 */
    private BigDecimal allowance;
}
