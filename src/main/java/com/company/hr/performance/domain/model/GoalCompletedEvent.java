package com.company.hr.performance.domain.model;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 目标完成事件
 */
@Getter
public class GoalCompletedEvent extends DomainEvent {
    private final GoalId goalId;
    private final EmployeeId employeeId;
    private final BigDecimal totalScore;
    
    public GoalCompletedEvent(GoalId goalId, EmployeeId employeeId, BigDecimal totalScore) {
        super();
        this.goalId = goalId;
        this.employeeId = employeeId;
        this.totalScore = totalScore;
    }
}

