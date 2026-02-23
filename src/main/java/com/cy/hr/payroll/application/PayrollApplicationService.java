package com.cy.hr.payroll.application;

/**
 * 文件说明：PayrollApplicationService
 */
import com.cy.hr.payroll.domain.AttendanceLockChecker;
import com.cy.hr.payroll.domain.PayrollCalculationDomainService;
import com.cy.hr.payroll.domain.PayrollRecord;
import com.cy.hr.payroll.domain.PayrollRecordRepository;
import com.cy.hr.payroll.domain.SalaryProfile;
import com.cy.hr.payroll.domain.SalaryProfileRepository;
import com.cy.hr.personnel.domain.EmployeeRepository;
import com.cy.hr.shared.domain.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollApplicationService {

    private final SalaryProfileRepository salaryProfileRepository;
    private final PayrollRecordRepository payrollRecordRepository;
    private final AttendanceLockChecker attendanceLockChecker;
    private final EmployeeRepository employeeRepository;
    private final PayrollCalculationDomainService payrollCalculationDomainService;

    /**
     * 保存员工薪资档案。
     */
    public SalaryProfile saveProfile(SaveSalaryProfileCommand command) {
        employeeRepository.findById(command.employeeId()).orElseThrow(() -> new DomainException("员工不存在"));
        SalaryProfile profile = new SalaryProfile(
                command.employeeId(),
                command.baseSalary(),
                command.positionSalary(),
                command.performanceSalary(),
                command.allowance()
        );
        return salaryProfileRepository.save(profile);
    }

    /**
     * 执行月度薪资核算。
     */
    public PayrollRecord calculate(CalculatePayrollCommand command) {
        YearMonth period = YearMonth.parse(command.period());
        // 业务规则：薪资核算前必须锁定考勤
        if (!attendanceLockChecker.isLocked(period)) {
            throw new DomainException("考勤数据未锁定");
        }

        SalaryProfile profile = salaryProfileRepository.findByEmployeeId(command.employeeId())
                .orElseThrow(() -> new DomainException("薪资档案缺失"));

        // 应用层职责：编排流程；领域层职责：薪资规则计算
        PayrollRecord record = payrollCalculationDomainService.calculate(
                command.employeeId(),
                period,
                profile,
                command.deduction());

        return payrollRecordRepository.save(record);
    }

    /**
     * 锁定考勤期间。
     */
    public void lockAttendance(String period) {
        attendanceLockChecker.markLocked(YearMonth.parse(period));
    }

    /**
     * 按期间查询薪资记录。
     */
    public List<PayrollRecord> listByPeriod(String period) {
        return payrollRecordRepository.findByPeriod(YearMonth.parse(period));
    }
}
