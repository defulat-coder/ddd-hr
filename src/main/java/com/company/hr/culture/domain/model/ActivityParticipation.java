package com.company.hr.culture.domain.model;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.domain.Entity;
import com.company.hr.shared.exception.DomainException;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 活动参与实体
 */
@Getter
public class ActivityParticipation extends Entity<ParticipationId> {
    
    private EmployeeId employeeId;
    private LocalDateTime registrationTime;
    private ParticipationStatus status;
    private String feedback;
    private Integer rating; // 1-5星评分
    
    public ActivityParticipation(ParticipationId id, EmployeeId employeeId) {
        super(id);
        this.employeeId = employeeId;
        this.registrationTime = LocalDateTime.now();
        this.status = ParticipationStatus.REGISTERED;
    }
    
    /**
     * 确认参加
     */
    public void confirm() {
        if (status != ParticipationStatus.REGISTERED) {
            throw new DomainException("只有已报名状态才能确认");
        }
        this.status = ParticipationStatus.CONFIRMED;
    }
    
    /**
     * 标记出席
     */
    public void markAttended() {
        if (status != ParticipationStatus.CONFIRMED && status != ParticipationStatus.REGISTERED) {
            throw new DomainException("只有已确认或已报名状态才能标记出席");
        }
        this.status = ParticipationStatus.ATTENDED;
    }
    
    /**
     * 标记缺席
     */
    public void markAbsent() {
        if (status == ParticipationStatus.ATTENDED) {
            throw new DomainException("已参加的不能标记为缺席");
        }
        this.status = ParticipationStatus.ABSENT;
    }
    
    /**
     * 取消参加
     */
    public void cancel() {
        if (status == ParticipationStatus.ATTENDED || status == ParticipationStatus.ABSENT) {
            throw new DomainException("活动已结束，不能取消");
        }
        this.status = ParticipationStatus.CANCELLED;
    }
    
    /**
     * 提交反馈和评分
     */
    public void submitFeedback(String feedback, Integer rating) {
        if (status != ParticipationStatus.ATTENDED) {
            throw new DomainException("只有参加过的活动才能提交反馈");
        }
        if (rating < 1 || rating > 5) {
            throw new DomainException("评分必须在1-5之间");
        }
        this.feedback = feedback;
        this.rating = rating;
    }
    
    /**
     * 是否已参加
     */
    public boolean hasAttended() {
        return status == ParticipationStatus.ATTENDED;
    }
}

