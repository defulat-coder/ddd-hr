package com.cy.hr.payroll.domain;

/**
 * 文件说明：PayrollRecordRepository
 */
import java.time.YearMonth;
import java.util.List;

public interface PayrollRecordRepository {

    PayrollRecord save(PayrollRecord payrollRecord);

    List<PayrollRecord> findByPeriod(YearMonth period);
}
