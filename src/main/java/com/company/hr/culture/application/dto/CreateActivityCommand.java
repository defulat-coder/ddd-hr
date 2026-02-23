package com.company.hr.culture.application.dto;

import com.company.hr.culture.domain.model.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "创建文化活动请求")
public class CreateActivityCommand {
    private String title;
    private String description;
    private ActivityType type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime registrationDeadline;
    private String location;
    private String organizerId;
    private Integer maxParticipants;
    private BigDecimal budget;
}
