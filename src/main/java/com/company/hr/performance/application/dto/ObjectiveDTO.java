package com.company.hr.performance.application.dto;

import com.company.hr.performance.domain.model.Objective;
import com.company.hr.performance.domain.model.ObjectiveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "目标项响应")
public class ObjectiveDTO {
    private String id;
    private String description;
    private String keyResult;
    private BigDecimal weight;
    private BigDecimal targetValue;
    private BigDecimal actualValue;
    private ObjectiveStatus status;
    private BigDecimal completionRate;
    private BigDecimal score;

    public static ObjectiveDTO fromDomain(Objective objective) {
        return ObjectiveDTO.builder()
            .id(objective.getId().getValue())
            .description(objective.getDescription())
            .keyResult(objective.getKeyResult())
            .weight(objective.getWeight())
            .targetValue(objective.getTargetValue())
            .actualValue(objective.getActualValue())
            .status(objective.getStatus())
            .completionRate(objective.getCompletionRate())
            .score(objective.calculateScore())
            .build();
    }
}
