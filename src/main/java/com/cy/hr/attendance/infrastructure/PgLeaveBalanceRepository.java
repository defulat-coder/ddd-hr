package com.cy.hr.attendance.infrastructure;

/**
 * 文件说明：PgLeaveBalanceRepository
 */
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.hr.attendance.domain.LeaveBalanceRepository;
import com.cy.hr.attendance.infrastructure.mapper.LeaveBalanceMapper;
import com.cy.hr.attendance.infrastructure.po.LeaveBalancePO;
import com.cy.hr.shared.domain.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PgLeaveBalanceRepository implements LeaveBalanceRepository {

    private final LeaveBalanceMapper mapper;

    @Override
    public int getBalanceDays(String employeeId, String leaveType) {
        LeaveBalancePO po = mapper.selectOne(new LambdaQueryWrapper<LeaveBalancePO>()
                .eq(LeaveBalancePO::getEmployeeId, employeeId)
                .eq(LeaveBalancePO::getLeaveType, leaveType)
                .last("LIMIT 1"));
        return po == null ? 0 : po.getBalanceDays();
    }

    @Override
    public void deduct(String employeeId, String leaveType, int days) {
        int affected = mapper.deduct(employeeId, leaveType, days);
        if (affected == 0) {
            throw new DomainException("假期余额不足");
        }
    }

    @Override
    public void setBalance(String employeeId, String leaveType, int days) {
        if (days < 0) {
            throw new DomainException("假期余额不能小于0");
        }
        mapper.upsert(employeeId, leaveType, days);
    }
}
