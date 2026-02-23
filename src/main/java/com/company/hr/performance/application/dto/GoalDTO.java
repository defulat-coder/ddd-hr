package com.company.hr.performance.application.dto;

import com.company.hr.performance.domain.model.Goal;
import com.company.hr.performance.domain.model.GoalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@Schema(description = "目标响应")
public class GoalDTO {
    private String id;
    private String employeeId;
    private String title;
    private String description;
    private GoalStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationDeadline;
    private java.math.BigDecimal totalScore;
    private List<ObjectiveDTO> objectives;

    public static GoalDTO fromDomain(Goal goal) {
        return GoalDTO.builder()
            .id(goal.getId().getValue())
            .employeeId(goal.getEmployeeId().getValue())
            .title(goal.getTitle())
            .description(goal.getDescription())
            .status(goal.getStatus())
            .startDate(goal.getPeriod().getStartDate())
            .endDate(goal.getPeriod().getEndDate())
            .registrationDeadline(goal.getPeriod().getRegistrationDeadline())
            .totalScore(goal.calculateTotalScore())
            .objectives(goal.getObjectives().stream().map(ObjectiveDTO::fromDomain).collect(Collectors.toList()))
            .build();
    }
}
