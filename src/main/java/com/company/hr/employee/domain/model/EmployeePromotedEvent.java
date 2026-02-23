package com.company.hr.employee.domain.model;

import com.company.hr.organization.domain.model.PositionId;
import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

/**
 * 员工晋升事件
 */
@Getter
public class EmployeePromotedEvent extends DomainEvent {
    private final EmployeeId employeeId;
    private final PositionId oldPositionId;
    private final PositionId newPositionId;
    private final String reason;
    
    public EmployeePromotedEvent(EmployeeId employeeId, PositionId oldPositionId, 
                                PositionId newPositionId, String reason) {
        super();
        this.employeeId = employeeId;
        this.oldPositionId = oldPositionId;
        this.newPositionId = newPositionId;
        this.reason = reason;
    }
}

