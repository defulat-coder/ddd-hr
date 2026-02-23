package com.cy.hr.payroll.domain;

/**
 * 文件说明：PayrollRecord
 */
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Getter
public class PayrollRecord {

    /** 薪资记录ID */
    private final String id;
    /** 员工ID */
    private final String employeeId;
    /** 薪资期间（yyyy-MM） */
    private final YearMonth period;
    /** 应发工资 */
    private final BigDecimal grossSalary;
    /** 扣款项 */
    private final BigDecimal deduction;
    /** 社保公积金 */
    private final BigDecimal socialSecurity;
    /** 个税 */
    private final BigDecimal tax;
    /** 实发工资 */
    private final BigDecimal netSalary;

    private PayrollRecord(String id,
                          String employeeId,
                          YearMonth period,
                          BigDecimal grossSalary,
                          BigDecimal deduction,
                          BigDecimal socialSecurity,
                          BigDecimal tax,
                          BigDecimal netSalary) {
        this.id = id;
        this.employeeId = employeeId;
        this.period = period;
        this.grossSalary = grossSalary;
        this.deduction = deduction;
        this.socialSecurity = socialSecurity;
        this.tax = tax;
        this.netSalary = netSalary;
    }

    /**
     * 创建薪资核算记录。
     */
    public PayrollRecord(String employeeId,
                         YearMonth period,
                         BigDecimal grossSalary,
                         BigDecimal deduction,
                         BigDecimal socialSecurity,
                         BigDecimal tax,
                         BigDecimal netSalary) {
        this.id = UUID.randomUUID().toString();
        this.employeeId = employeeId;
        this.period = period;
        this.grossSalary = grossSalary;
        this.deduction = deduction;
        this.socialSecurity = socialSecurity;
        this.tax = tax;
        this.netSalary = netSalary;
    }

    /**
     * 从持久化数据恢复薪资记录。
     */
    public static PayrollRecord restore(String id,
                                        String employeeId,
                                        YearMonth period,
                                        BigDecimal grossSalary,
                                        BigDecimal deduction,
                                        BigDecimal socialSecurity,
                                        BigDecimal tax,
                                        BigDecimal netSalary) {
        return new PayrollRecord(id, employeeId, period, grossSalary, deduction, socialSecurity, tax, netSalary);
    }

}
