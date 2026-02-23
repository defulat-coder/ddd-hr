package com.company.hr.culture.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hr.culture.domain.model.*;
import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.infrastructure.persistence.BaseEntity;
import com.company.hr.shared.infrastructure.persistence.JsonUtils;
import com.company.hr.shared.infrastructure.persistence.ReflectionUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("culture_activities")
public class CultureActivityEntity extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private String id;
    private String title;
    private String description;
    private String type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime registrationDeadline;
    private String location;
    private String organizerId;
    private Integer maxParticipants;
    private BigDecimal budget;
    private String status;
    private String participationsJson;

    public static CultureActivityEntity fromDomain(CultureActivity activity) {
        CultureActivityEntity entity = new CultureActivityEntity();
        entity.setId(activity.getId().getValue());
        entity.setTitle(activity.getTitle());
        entity.setDescription(activity.getDescription());
        entity.setType(activity.getType().name());
        entity.setStartTime(activity.getSchedule().getStartTime());
        entity.setEndTime(activity.getSchedule().getEndTime());
        entity.setRegistrationDeadline(activity.getSchedule().getRegistrationDeadline());
        entity.setLocation(activity.getLocation());
        entity.setOrganizerId(activity.getOrganizerId().getValue());
        entity.setMaxParticipants(activity.getMaxParticipants());
        entity.setBudget(activity.getBudget());
        entity.setStatus(activity.getStatus().name());

        List<ParticipationSnapshot> snapshots = new ArrayList<>();
        for (ActivityParticipation participation : activity.getParticipations()) {
            ParticipationSnapshot snapshot = new ParticipationSnapshot();
            snapshot.id = participation.getId().getValue();
            snapshot.employeeId = participation.getEmployeeId().getValue();
            snapshot.registrationTime = participation.getRegistrationTime();
            snapshot.status = participation.getStatus().name();
            snapshot.feedback = participation.getFeedback();
            snapshot.rating = participation.getRating();
            snapshots.add(snapshot);
        }
        entity.setParticipationsJson(JsonUtils.toJson(snapshots));
        return entity;
    }

    public CultureActivity toDomain() {
        ActivitySchedule schedule = new ActivitySchedule(startTime, endTime, registrationDeadline);
        CultureActivity activity = new CultureActivity(
            ActivityId.of(id),
            title,
            description,
            ActivityType.valueOf(type),
            schedule,
            location,
            EmployeeId.of(organizerId),
            maxParticipants,
            budget
        );
        activity.clearDomainEvents();

        List<ActivityParticipation> participations = new ArrayList<>();
        if (participationsJson != null && !participationsJson.isBlank()) {
            List<ParticipationSnapshot> snapshots = JsonUtils.fromJson(participationsJson, new TypeReference<List<ParticipationSnapshot>>() {});
            for (ParticipationSnapshot snapshot : snapshots) {
                ActivityParticipation participation = new ActivityParticipation(
                    ParticipationId.of(snapshot.id),
                    EmployeeId.of(snapshot.employeeId)
                );
                ReflectionUtils.setField(participation, "registrationTime", snapshot.registrationTime);
                ReflectionUtils.setField(participation, "status", ParticipationStatus.valueOf(snapshot.status));
                ReflectionUtils.setField(participation, "feedback", snapshot.feedback);
                ReflectionUtils.setField(participation, "rating", snapshot.rating);
                participations.add(participation);
            }
        }

        ReflectionUtils.setField(activity, "participations", participations);
        ReflectionUtils.setField(activity, "status", ActivityStatus.valueOf(status));
        return activity;
    }

    public static class ParticipationSnapshot {
        public String id;
        public String employeeId;
        public LocalDateTime registrationTime;
        public String status;
        public String feedback;
        public Integer rating;
    }
}
