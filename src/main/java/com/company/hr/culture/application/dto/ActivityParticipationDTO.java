package com.company.hr.culture.application.dto;

import com.company.hr.culture.domain.model.ActivityParticipation;
import com.company.hr.culture.domain.model.ParticipationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "活动参与记录")
public class ActivityParticipationDTO {
    private String id;
    private String employeeId;
    private LocalDateTime registrationTime;
    private ParticipationStatus status;
    private String feedback;
    private Integer rating;

    public static ActivityParticipationDTO fromDomain(ActivityParticipation participation) {
        return ActivityParticipationDTO.builder()
            .id(participation.getId().getValue())
            .employeeId(participation.getEmployeeId().getValue())
            .registrationTime(participation.getRegistrationTime())
            .status(participation.getStatus())
            .feedback(participation.getFeedback())
            .rating(participation.getRating())
            .build();
    }
}
