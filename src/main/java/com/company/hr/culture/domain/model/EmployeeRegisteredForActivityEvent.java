package com.company.hr.culture.domain.model;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

/**
 * 员工报名活动事件
 */
@Getter
public class EmployeeRegisteredForActivityEvent extends DomainEvent {
    private final ActivityId activityId;
    private final EmployeeId employeeId;
    
    public EmployeeRegisteredForActivityEvent(ActivityId activityId, EmployeeId employeeId) {
        super();
        this.activityId = activityId;
        this.employeeId = employeeId;
    }
}

