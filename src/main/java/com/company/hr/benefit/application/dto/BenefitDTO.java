package com.company.hr.benefit.application.dto;

import com.company.hr.benefit.domain.model.Benefit;
import com.company.hr.benefit.domain.model.BenefitType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@Schema(description = "福利响应")
public class BenefitDTO {
    private String id;
    private String name;
    private String description;
    private BenefitType type;
    private BigDecimal employerCost;
    private BigDecimal employeeCost;
    private BigDecimal totalCost;
    private Boolean active;
    private String eligibilityCriteria;
    private Long activeEnrollmentCount;
    private List<BenefitEnrollmentDTO> enrollments;

    public static BenefitDTO fromDomain(Benefit benefit) {
        return BenefitDTO.builder()
            .id(benefit.getId().getValue())
            .name(benefit.getName())
            .description(benefit.getDescription())
            .type(benefit.getType())
            .employerCost(benefit.getCost().getEmployerCost())
            .employeeCost(benefit.getCost().getEmployeeCost())
            .totalCost(benefit.getCost().getTotalCost())
            .active(benefit.isActive())
            .eligibilityCriteria(benefit.getEligibilityCriteria())
            .activeEnrollmentCount(benefit.getActiveEnrollmentCount())
            .enrollments(benefit.getEnrollments().stream().map(BenefitEnrollmentDTO::fromDomain).collect(Collectors.toList()))
            .build();
    }
}
