package com.company.hr.employee.domain.model;

import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

/**
 * 员工状态变更事件
 */
@Getter
public class EmployeeStatusChangedEvent extends DomainEvent {
    private final EmployeeId employeeId;
    private final EmployeeStatus oldStatus;
    private final EmployeeStatus newStatus;
    
    public EmployeeStatusChangedEvent(EmployeeId employeeId, EmployeeStatus oldStatus, 
                                     EmployeeStatus newStatus) {
        super();
        this.employeeId = employeeId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}

