package com.cy.hr.payroll.infrastructure.mapper;

/**
 * 文件说明：AttendanceLockMapper
 */
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cy.hr.payroll.infrastructure.po.AttendanceLockPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface AttendanceLockMapper extends BaseMapper<AttendanceLockPO> {

    @Insert("""
            INSERT INTO attendance_locks(period)
            VALUES (#{period})
            ON CONFLICT (period) DO NOTHING
            """)
    int insertIgnore(@Param("period") String period);
}
