package com.cy.hr.payroll.domain;

/**
 * 文件说明：PayrollCalculationDomainService
 */
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

@Component
public class PayrollCalculationDomainService {

    private static final BigDecimal SOCIAL_SECURITY_RATE = new BigDecimal("0.105");
    private static final BigDecimal TAX_THRESHOLD = new BigDecimal("5000");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");

    /**
     * 执行薪资领域核算规则，生成薪资记录。
     */
    public PayrollRecord calculate(String employeeId, YearMonth period, SalaryProfile profile, BigDecimal deduction) {
        BigDecimal gross = profile.getBaseSalary()
                .add(profile.getPositionSalary())
                .add(profile.getPerformanceSalary())
                .add(profile.getAllowance());

        BigDecimal socialSecurity = gross.multiply(SOCIAL_SECURITY_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxableIncome = gross.subtract(socialSecurity).subtract(deduction).subtract(TAX_THRESHOLD);
        BigDecimal tax = taxableIncome.compareTo(BigDecimal.ZERO) > 0
                ? taxableIncome.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal net = gross.subtract(socialSecurity).subtract(deduction).subtract(tax).setScale(2, RoundingMode.HALF_UP);

        return new PayrollRecord(
                employeeId,
                period,
                gross.setScale(2, RoundingMode.HALF_UP),
                deduction.setScale(2, RoundingMode.HALF_UP),
                socialSecurity,
                tax,
                net
        );
    }
}

