package com.cy.hr.payroll.interfaces.rest;

/**
 * 文件说明：PayrollController
 */
import com.cy.hr.payroll.application.CalculatePayrollCommand;
import com.cy.hr.payroll.application.PayrollApplicationService;
import com.cy.hr.payroll.application.SaveSalaryProfileCommand;
import com.cy.hr.payroll.domain.PayrollRecord;
import com.cy.hr.payroll.domain.SalaryProfile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/payrolls")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollApplicationService service;

    @PostMapping("/profiles")
    public SalaryProfileResponse saveProfile(@Valid @RequestBody SaveSalaryProfileCommand command) {
        return toResponse(service.saveProfile(command));
    }

    @PostMapping("/attendance-lock")
    public void lockAttendance(@RequestParam @NotBlank String period) {
        service.lockAttendance(period);
    }

    @PostMapping("/calculate")
    public PayrollRecordResponse calculate(@Valid @RequestBody CalculatePayrollCommand command) {
        return toResponse(service.calculate(command));
    }

    @GetMapping
    public List<PayrollRecordResponse> list(@RequestParam @NotBlank String period) {
        return service.listByPeriod(period).stream().map(this::toResponse).toList();
    }

    private SalaryProfileResponse toResponse(SalaryProfile salaryProfile) {
        return new SalaryProfileResponse(
                salaryProfile.getEmployeeId(),
                salaryProfile.getBaseSalary(),
                salaryProfile.getPositionSalary(),
                salaryProfile.getPerformanceSalary(),
                salaryProfile.getAllowance()
        );
    }

    private PayrollRecordResponse toResponse(PayrollRecord payrollRecord) {
        return new PayrollRecordResponse(
                payrollRecord.getId(),
                payrollRecord.getEmployeeId(),
                payrollRecord.getPeriod().toString(),
                payrollRecord.getGrossSalary(),
                payrollRecord.getDeduction(),
                payrollRecord.getSocialSecurity(),
                payrollRecord.getTax(),
                payrollRecord.getNetSalary()
        );
    }

    public record SalaryProfileResponse(
            String employeeId,
            java.math.BigDecimal baseSalary,
            java.math.BigDecimal positionSalary,
            java.math.BigDecimal performanceSalary,
            java.math.BigDecimal allowance
    ) {
    }

    public record PayrollRecordResponse(
            String id,
            String employeeId,
            String period,
            java.math.BigDecimal grossSalary,
            java.math.BigDecimal deduction,
            java.math.BigDecimal socialSecurity,
            java.math.BigDecimal tax,
            java.math.BigDecimal netSalary
    ) {
    }
}
