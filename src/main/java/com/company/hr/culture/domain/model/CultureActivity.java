package com.company.hr.culture.domain.model;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.domain.AggregateRoot;
import com.company.hr.shared.exception.DomainException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 企业文化活动聚合根
 */
@Getter
public class CultureActivity extends AggregateRoot<ActivityId> {
    
    private String title;
    private String description;
    private ActivityType type;
    private ActivitySchedule schedule;
    private String location;
    private EmployeeId organizerId;
    private int maxParticipants;
    private BigDecimal budget;
    private ActivityStatus status;
    private List<ActivityParticipation> participations;
    
    public CultureActivity(ActivityId id, String title, String description, 
                          ActivityType type, ActivitySchedule schedule, 
                          String location, EmployeeId organizerId, 
                          int maxParticipants, BigDecimal budget) {
        super(id);
        this.title = title;
        this.description = description;
        this.type = type;
        this.schedule = schedule;
        this.location = location;
        this.organizerId = organizerId;
        this.maxParticipants = maxParticipants;
        this.budget = budget;
        this.status = ActivityStatus.PLANNED;
        this.participations = new ArrayList<>();
        
        schedule.validate();
        registerEvent(new ActivityCreatedEvent(id, title, type, schedule.getStartTime()));
    }
    
    /**
     * 开放报名
     */
    public void openRegistration() {
        if (status != ActivityStatus.PLANNED) {
            throw new DomainException("只有计划中的活动才能开放报名");
        }
        if (LocalDateTime.now().isAfter(schedule.getRegistrationDeadline())) {
            throw new DomainException("报名截止时间已过");
        }
        this.status = ActivityStatus.REGISTRATION_OPEN;
    }
    
    /**
     * 员工报名
     */
    public void registerParticipant(EmployeeId employeeId) {
        if (status != ActivityStatus.REGISTRATION_OPEN) {
            throw new DomainException("活动未开放报名");
        }
        
        if (!schedule.isRegistrationOpen()) {
            throw new DomainException("报名时间已截止");
        }
        
        // 检查是否已报名
        boolean alreadyRegistered = participations.stream()
            .anyMatch(p -> p.getEmployeeId().equals(employeeId) 
                && p.getStatus() != ParticipationStatus.CANCELLED);
        
        if (alreadyRegistered) {
            throw new DomainException("该员工已经报名");
        }
        
        // 检查人数限制
        long activeParticipants = participations.stream()
            .filter(p -> p.getStatus() != ParticipationStatus.CANCELLED)
            .count();
        
        if (activeParticipants >= maxParticipants) {
            throw new DomainException("活动人数已满");
        }
        
        ParticipationId participationId = ParticipationId.generate();
        ActivityParticipation participation = new ActivityParticipation(participationId, employeeId);
        this.participations.add(participation);
        
        registerEvent(new EmployeeRegisteredForActivityEvent(getId(), employeeId));
    }
    
    /**
     * 关闭报名
     */
    public void closeRegistration() {
        if (status != ActivityStatus.REGISTRATION_OPEN) {
            throw new DomainException("活动未开放报名");
        }
        this.status = ActivityStatus.REGISTRATION_CLOSED;
    }
    
    /**
     * 开始活动
     */
    public void start() {
        if (status != ActivityStatus.REGISTRATION_CLOSED && status != ActivityStatus.REGISTRATION_OPEN) {
            throw new DomainException("活动状态不允许开始");
        }
        if (LocalDateTime.now().isBefore(schedule.getStartTime())) {
            throw new DomainException("活动开始时间未到");
        }
        this.status = ActivityStatus.IN_PROGRESS;
    }
    
    /**
     * 完成活动
     */
    public void complete() {
        if (status != ActivityStatus.IN_PROGRESS) {
            throw new DomainException("只有进行中的活动才能完成");
        }
        this.status = ActivityStatus.COMPLETED;
        registerEvent(new ActivityCompletedEvent(getId(), getAttendeeCount()));
    }
    
    /**
     * 取消活动
     */
    public void cancel(String reason) {
        if (status == ActivityStatus.COMPLETED || status == ActivityStatus.CANCELLED) {
            throw new DomainException("活动已结束");
        }
        this.status = ActivityStatus.CANCELLED;
    }
    
    /**
     * 更新活动信息
     */
    public void updateInfo(String title, String description, String location) {
        if (status == ActivityStatus.COMPLETED || status == ActivityStatus.CANCELLED) {
            throw new DomainException("已结束的活动不能修改");
        }
        this.title = title;
        this.description = description;
        this.location = location;
    }
    
    /**
     * 获取参与记录列表（不可修改）
     */
    public List<ActivityParticipation> getParticipations() {
        return Collections.unmodifiableList(participations);
    }
    
    /**
     * 获取报名人数
     */
    public long getRegistrationCount() {
        return participations.stream()
            .filter(p -> p.getStatus() != ParticipationStatus.CANCELLED)
            .count();
    }
    
    /**
     * 获取实际参加人数
     */
    public long getAttendeeCount() {
        return participations.stream()
            .filter(ActivityParticipation::hasAttended)
            .count();
    }
    
    /**
     * 获取平均评分
     */
    public Double getAverageRating() {
        return participations.stream()
            .filter(p -> p.getRating() != null)
            .mapToInt(ActivityParticipation::getRating)
            .average()
            .orElse(0.0);
    }
    
    /**
     * 是否还有名额
     */
    public boolean hasVacancy() {
        return getRegistrationCount() < maxParticipants;
    }
}

