package com.company.hr.culture.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "活动报名请求")
public class RegisterParticipantCommand {
    private String activityId;
    private String employeeId;
}
