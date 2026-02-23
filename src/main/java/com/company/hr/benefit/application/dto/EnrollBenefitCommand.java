package com.company.hr.benefit.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "员工参加福利请求")
public class EnrollBenefitCommand {
    private String benefitId;
    private String employeeId;
    private LocalDate enrollmentDate;
    private LocalDate effectiveDate;
    private LocalDate expirationDate;
}
