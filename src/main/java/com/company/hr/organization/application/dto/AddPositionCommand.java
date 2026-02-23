package com.company.hr.organization.application.dto;

import com.company.hr.organization.domain.model.PositionLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "新增职位请求")
public class AddPositionCommand {
    @Schema(description = "部门ID")
    private String departmentId;
    @Schema(description = "职位名称")
    private String title;
    @Schema(description = "职位编码")
    private String code;
    @Schema(description = "职位级别")
    private PositionLevel level;
    @Schema(description = "最低薪资")
    private BigDecimal minSalary;
    @Schema(description = "最高薪资")
    private BigDecimal maxSalary;
    @Schema(description = "职位描述")
    private String description;
    @Schema(description = "最大编制")
    private Integer maxHeadcount;
}
