package com.company.hr.employee.domain.model;

import com.company.hr.organization.domain.model.DepartmentId;
import com.company.hr.organization.domain.model.PositionId;
import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

/**
 * 员工调动事件
 */
@Getter
public class EmployeeTransferredEvent extends DomainEvent {
    private final EmployeeId employeeId;
    private final DepartmentId oldDepartmentId;
    private final DepartmentId newDepartmentId;
    private final PositionId oldPositionId;
    private final PositionId newPositionId;
    private final String reason;
    
    public EmployeeTransferredEvent(EmployeeId employeeId, 
                                   DepartmentId oldDepartmentId, DepartmentId newDepartmentId,
                                   PositionId oldPositionId, PositionId newPositionId,
                                   String reason) {
        super();
        this.employeeId = employeeId;
        this.oldDepartmentId = oldDepartmentId;
        this.newDepartmentId = newDepartmentId;
        this.oldPositionId = oldPositionId;
        this.newPositionId = newPositionId;
        this.reason = reason;
    }
}

