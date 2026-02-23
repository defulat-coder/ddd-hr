package com.cy.hr.payroll.domain;

/**
 * 文件说明：SalaryProfile
 */
import com.cy.hr.shared.domain.DomainException;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SalaryProfile {

    /** 员工ID */
    private final String employeeId;
    /** 基本工资 */
    private final BigDecimal baseSalary;
    /** 岗位工资 */
    private final BigDecimal positionSalary;
    /** 绩效工资 */
    private final BigDecimal performanceSalary;
    /** 补贴 */
    private final BigDecimal allowance;

    /**
     * 创建员工薪资档案。
     */
    public SalaryProfile(String employeeId,
                         BigDecimal baseSalary,
                         BigDecimal positionSalary,
                         BigDecimal performanceSalary,
                         BigDecimal allowance) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new DomainException("员工ID不能为空");
        }
        this.employeeId = employeeId;
        this.baseSalary = notNegative(baseSalary, "基本工资");
        this.positionSalary = notNegative(positionSalary, "岗位工资");
        this.performanceSalary = notNegative(performanceSalary, "绩效工资");
        this.allowance = notNegative(allowance, "补贴");
    }

    private BigDecimal notNegative(BigDecimal value, String field) {
        // 业务规则：薪资项不可为负数
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException(field + "不能小于0");
        }
        return value;
    }

}
