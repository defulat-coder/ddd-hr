package com.company.hr.performance.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "新增目标项请求")
public class AddObjectiveCommand {
    @Schema(description = "目标ID")
    private String goalId;
    @Schema(description = "目标项描述")
    private String description;
    @Schema(description = "关键结果")
    private String keyResult;
    @Schema(description = "权重")
    private BigDecimal weight;
    @Schema(description = "目标值")
    private BigDecimal targetValue;
}
