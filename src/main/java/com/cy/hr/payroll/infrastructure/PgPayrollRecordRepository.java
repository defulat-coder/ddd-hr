package com.cy.hr.payroll.infrastructure;

/**
 * 文件说明：PgPayrollRecordRepository
 */
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.hr.payroll.domain.PayrollRecord;
import com.cy.hr.payroll.domain.PayrollRecordRepository;
import com.cy.hr.payroll.infrastructure.mapper.PayrollRecordMapper;
import com.cy.hr.payroll.infrastructure.po.PayrollRecordPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PgPayrollRecordRepository implements PayrollRecordRepository {

    private final PayrollRecordMapper mapper;

    @Override
    public PayrollRecord save(PayrollRecord payrollRecord) {
        PayrollRecordPO po = new PayrollRecordPO();
        po.setId(payrollRecord.getId());
        po.setEmployeeId(payrollRecord.getEmployeeId());
        po.setPeriod(payrollRecord.getPeriod().toString());
        po.setGrossSalary(payrollRecord.getGrossSalary());
        po.setDeduction(payrollRecord.getDeduction());
        po.setSocialSecurity(payrollRecord.getSocialSecurity());
        po.setTax(payrollRecord.getTax());
        po.setNetSalary(payrollRecord.getNetSalary());
        if (mapper.selectById(po.getId()) == null) {
            mapper.insert(po);
        } else {
            mapper.updateById(po);
        }
        return payrollRecord;
    }

    @Override
    public List<PayrollRecord> findByPeriod(YearMonth period) {
        return mapper.selectList(new LambdaQueryWrapper<PayrollRecordPO>()
                        .eq(PayrollRecordPO::getPeriod, period.toString())
                        .orderByAsc(PayrollRecordPO::getEmployeeId))
                .stream()
                .map(po -> PayrollRecord.restore(
                        po.getId(),
                        po.getEmployeeId(),
                        YearMonth.parse(po.getPeriod()),
                        po.getGrossSalary(),
                        po.getDeduction(),
                        po.getSocialSecurity(),
                        po.getTax(),
                        po.getNetSalary()))
                .toList();
    }
}
