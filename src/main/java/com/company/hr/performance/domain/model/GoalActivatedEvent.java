package com.company.hr.performance.domain.model;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

/**
 * 目标激活事件
 */
@Getter
public class GoalActivatedEvent extends DomainEvent {
    private final GoalId goalId;
    private final EmployeeId employeeId;
    
    public GoalActivatedEvent(GoalId goalId, EmployeeId employeeId) {
        super();
        this.goalId = goalId;
        this.employeeId = employeeId;
    }
}

