package com.cy.hr.payroll.domain;

/**
 * 文件说明：AttendanceLockChecker
 */
import java.time.YearMonth;

public interface AttendanceLockChecker {

    boolean isLocked(YearMonth period);

    void markLocked(YearMonth period);
}
