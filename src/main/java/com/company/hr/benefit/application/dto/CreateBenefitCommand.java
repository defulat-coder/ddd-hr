package com.company.hr.benefit.application.dto;

import com.company.hr.benefit.domain.model.BenefitType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "创建福利请求")
public class CreateBenefitCommand {
    private String name;
    private String description;
    private BenefitType type;
    private BigDecimal employerCost;
    private BigDecimal employeeCost;
    private String eligibilityCriteria;
}
