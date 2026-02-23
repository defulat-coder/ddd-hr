package com.company.hr.performance.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.infrastructure.persistence.BaseEntity;
import com.company.hr.performance.domain.model.*;
import com.company.hr.shared.infrastructure.persistence.JsonUtils;
import com.company.hr.shared.infrastructure.persistence.ReflectionUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("goals")
public class GoalEntity extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private String id;
    private String employeeId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationDeadline;
    private String status;
    private String objectivesJson;

    public static GoalEntity fromDomain(Goal goal) {
        GoalEntity entity = new GoalEntity();
        entity.setId(goal.getId().getValue());
        entity.setEmployeeId(goal.getEmployeeId().getValue());
        entity.setTitle(goal.getTitle());
        entity.setDescription(goal.getDescription());
        entity.setStartDate(goal.getPeriod().getStartDate());
        entity.setEndDate(goal.getPeriod().getEndDate());
        entity.setRegistrationDeadline(goal.getPeriod().getRegistrationDeadline());
        entity.setStatus(goal.getStatus().name());

        List<ObjectiveSnapshot> snapshots = new ArrayList<>();
        for (Objective objective : goal.getObjectives()) {
            ObjectiveSnapshot snapshot = new ObjectiveSnapshot();
            snapshot.id = objective.getId().getValue();
            snapshot.description = objective.getDescription();
            snapshot.keyResult = objective.getKeyResult();
            snapshot.weight = objective.getWeight();
            snapshot.targetValue = objective.getTargetValue();
            snapshot.actualValue = objective.getActualValue();
            snapshot.status = objective.getStatus().name();
            snapshots.add(snapshot);
        }
        entity.setObjectivesJson(JsonUtils.toJson(snapshots));
        return entity;
    }

    public Goal toDomain() {
        GoalPeriod period = new GoalPeriod(startDate, endDate, registrationDeadline);
        Goal goal = new Goal(GoalId.of(id), EmployeeId.of(employeeId), title, description, period);
        goal.clearDomainEvents();

        List<Objective> objectives = new ArrayList<>();
        if (objectivesJson != null && !objectivesJson.isBlank()) {
            List<ObjectiveSnapshot> snapshots = JsonUtils.fromJson(objectivesJson, new TypeReference<List<ObjectiveSnapshot>>() {});
            for (ObjectiveSnapshot snapshot : snapshots) {
                Objective objective = new Objective(
                    ObjectiveId.of(snapshot.id),
                    snapshot.description,
                    snapshot.keyResult,
                    snapshot.weight,
                    snapshot.targetValue
                );
                objective.updateProgress(snapshot.actualValue, ObjectiveStatus.valueOf(snapshot.status));
                objectives.add(objective);
            }
        }

        ReflectionUtils.setField(goal, "objectives", objectives);
        ReflectionUtils.setField(goal, "status", GoalStatus.valueOf(status));
        return goal;
    }

    public static class ObjectiveSnapshot {
        public String id;
        public String description;
        public String keyResult;
        public BigDecimal weight;
        public BigDecimal targetValue;
        public BigDecimal actualValue;
        public String status;
    }
}
