package com.company.hr.performance.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "创建目标请求")
public class CreateGoalCommand {
    @Schema(description = "员工ID")
    private String employeeId;
    @Schema(description = "目标标题")
    private String title;
    @Schema(description = "目标描述")
    private String description;
    @Schema(description = "开始日期")
    private LocalDate startDate;
    @Schema(description = "结束日期")
    private LocalDate endDate;
    @Schema(description = "设置截止日期")
    private LocalDate registrationDeadline;
}
