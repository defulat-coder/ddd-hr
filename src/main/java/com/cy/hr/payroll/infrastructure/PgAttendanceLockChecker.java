package com.cy.hr.payroll.infrastructure;

/**
 * 文件说明：PgAttendanceLockChecker
 */
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.hr.payroll.domain.AttendanceLockChecker;
import com.cy.hr.payroll.infrastructure.mapper.AttendanceLockMapper;
import com.cy.hr.payroll.infrastructure.po.AttendanceLockPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

@Component
@RequiredArgsConstructor
public class PgAttendanceLockChecker implements AttendanceLockChecker {

    private final AttendanceLockMapper mapper;

    @Override
    public boolean isLocked(YearMonth period) {
        Long count = mapper.selectCount(new LambdaQueryWrapper<AttendanceLockPO>()
                .eq(AttendanceLockPO::getPeriod, period.toString()));
        return count != null && count > 0;
    }

    @Override
    public void markLocked(YearMonth period) {
        mapper.insertIgnore(period.toString());
    }
}
