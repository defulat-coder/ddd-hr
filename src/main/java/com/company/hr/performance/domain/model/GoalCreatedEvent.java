package com.company.hr.performance.domain.model;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

/**
 * 目标创建事件
 */
@Getter
public class GoalCreatedEvent extends DomainEvent {
    private final GoalId goalId;
    private final EmployeeId employeeId;
    private final String title;
    
    public GoalCreatedEvent(GoalId goalId, EmployeeId employeeId, String title) {
        super();
        this.goalId = goalId;
        this.employeeId = employeeId;
        this.title = title;
    }
}

