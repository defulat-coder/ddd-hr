package com.company.hr.organization.application.dto;

import com.company.hr.organization.domain.model.Position;
import com.company.hr.organization.domain.model.PositionLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "职位响应")
public class PositionDTO {
    private String id;
    private String title;
    private String code;
    private PositionLevel level;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String description;
    private Integer headcount;
    private Integer maxHeadcount;

    public static PositionDTO fromDomain(Position position) {
        return PositionDTO.builder()
            .id(position.getId().getValue())
            .title(position.getTitle())
            .code(position.getCode())
            .level(position.getLevel())
            .minSalary(position.getMinSalary())
            .maxSalary(position.getMaxSalary())
            .description(position.getDescription())
            .headcount(position.getHeadcount())
            .maxHeadcount(position.getMaxHeadcount())
            .build();
    }
}
