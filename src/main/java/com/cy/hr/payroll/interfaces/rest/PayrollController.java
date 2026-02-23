package com.cy.hr.payroll.interfaces.rest;

/**
 * 文件说明：PayrollController
 */
import com.cy.hr.payroll.application.CalculatePayrollCommand;
import com.cy.hr.payroll.application.PayrollApplicationService;
import com.cy.hr.payroll.application.SaveSalaryProfileCommand;
import com.cy.hr.payroll.domain.PayrollRecord;
import com.cy.hr.payroll.domain.SalaryProfile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "薪酬管理")
public class PayrollController {

    private final PayrollApplicationService service;

    @PostMapping("/profiles")
    @Operation(summary = "保存薪资档案")
    public SalaryProfileResponse saveProfile(@Valid @RequestBody SaveSalaryProfileCommand command) {
        return toResponse(service.saveProfile(command));
    }

    @PostMapping("/attendance-lock")
    @Operation(summary = "锁定考勤期间")
    public void lockAttendance(@Parameter(description = "期间（yyyy-MM）") @RequestParam @NotBlank String period) {
        service.lockAttendance(period);
    }

    @PostMapping("/calculate")
    @Operation(summary = "执行薪资核算")
    public PayrollRecordResponse calculate(@Valid @RequestBody CalculatePayrollCommand command) {
        return toResponse(service.calculate(command));
    }

    @GetMapping
    @Operation(summary = "按期间查询薪资记录")
    public List<PayrollRecordResponse> list(@Parameter(description = "期间（yyyy-MM）") @RequestParam @NotBlank String period) {
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

    @Schema(name = "SalaryProfileResponse", description = "薪资档案响应")
    public record SalaryProfileResponse(
            @Schema(description = "员工ID") String employeeId,
            @Schema(description = "基本工资") java.math.BigDecimal baseSalary,
            @Schema(description = "岗位工资") java.math.BigDecimal positionSalary,
            @Schema(description = "绩效工资") java.math.BigDecimal performanceSalary,
            @Schema(description = "补贴") java.math.BigDecimal allowance
    ) {
    }

    @Schema(name = "PayrollRecordResponse", description = "薪资记录响应")
    public record PayrollRecordResponse(
            @Schema(description = "记录ID") String id,
            @Schema(description = "员工ID") String employeeId,
            @Schema(description = "核算期间") String period,
            @Schema(description = "应发工资") java.math.BigDecimal grossSalary,
            @Schema(description = "扣款项") java.math.BigDecimal deduction,
            @Schema(description = "社保公积金") java.math.BigDecimal socialSecurity,
            @Schema(description = "个税") java.math.BigDecimal tax,
            @Schema(description = "实发工资") java.math.BigDecimal netSalary
    ) {
    }
}
