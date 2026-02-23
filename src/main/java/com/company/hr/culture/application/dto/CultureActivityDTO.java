package com.company.hr.culture.application.dto;

import com.company.hr.culture.domain.model.ActivityStatus;
import com.company.hr.culture.domain.model.ActivityType;
import com.company.hr.culture.domain.model.CultureActivity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@Schema(description = "文化活动响应")
public class CultureActivityDTO {
    private String id;
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
    private ActivityStatus status;
    private Long registrationCount;
    private Long attendeeCount;
    private Double averageRating;
    private List<ActivityParticipationDTO> participations;

    public static CultureActivityDTO fromDomain(CultureActivity activity) {
        return CultureActivityDTO.builder()
            .id(activity.getId().getValue())
            .title(activity.getTitle())
            .description(activity.getDescription())
            .type(activity.getType())
            .startTime(activity.getSchedule().getStartTime())
            .endTime(activity.getSchedule().getEndTime())
            .registrationDeadline(activity.getSchedule().getRegistrationDeadline())
            .location(activity.getLocation())
            .organizerId(activity.getOrganizerId().getValue())
            .maxParticipants(activity.getMaxParticipants())
            .budget(activity.getBudget())
            .status(activity.getStatus())
            .registrationCount(activity.getRegistrationCount())
            .attendeeCount(activity.getAttendeeCount())
            .averageRating(activity.getAverageRating())
            .participations(activity.getParticipations().stream().map(ActivityParticipationDTO::fromDomain).collect(Collectors.toList()))
            .build();
    }
}
