package com.company.hr.benefit.application.dto;

import com.company.hr.benefit.domain.model.BenefitEnrollment;
import com.company.hr.benefit.domain.model.EnrollmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@Schema(description = "福利参加记录")
public class BenefitEnrollmentDTO {
    private String id;
    private String employeeId;
    private LocalDate enrollmentDate;
    private LocalDate effectiveDate;
    private LocalDate expirationDate;
    private EnrollmentStatus status;
    private String notes;

    public static BenefitEnrollmentDTO fromDomain(BenefitEnrollment enrollment) {
        return BenefitEnrollmentDTO.builder()
            .id(enrollment.getId().getValue())
            .employeeId(enrollment.getEmployeeId().getValue())
            .enrollmentDate(enrollment.getEnrollmentDate())
            .effectiveDate(enrollment.getEffectiveDate())
            .expirationDate(enrollment.getExpirationDate())
            .status(enrollment.getStatus())
            .notes(enrollment.getNotes())
            .build();
    }
}
