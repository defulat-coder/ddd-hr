package com.cy.hr.attendance.infrastructure.mapper;

/**
 * 文件说明：LeaveBalanceMapper
 */
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cy.hr.attendance.infrastructure.po.LeaveBalancePO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface LeaveBalanceMapper extends BaseMapper<LeaveBalancePO> {

    @Insert("""
            INSERT INTO leave_balances(employee_id, leave_type, balance_days)
            VALUES (#{employeeId}, #{leaveType}, #{days})
            ON CONFLICT (employee_id, leave_type) DO UPDATE SET balance_days = EXCLUDED.balance_days
            """)
    int upsert(@Param("employeeId") String employeeId, @Param("leaveType") String leaveType, @Param("days") int days);

    @Update("""
            UPDATE leave_balances
            SET balance_days = balance_days - #{days}
            WHERE employee_id = #{employeeId}
              AND leave_type = #{leaveType}
              AND balance_days >= #{days}
            """)
    int deduct(@Param("employeeId") String employeeId, @Param("leaveType") String leaveType, @Param("days") int days);
}
