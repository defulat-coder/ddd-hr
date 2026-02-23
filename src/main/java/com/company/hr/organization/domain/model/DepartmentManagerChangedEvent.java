package com.company.hr.organization.domain.model;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

/**
 * 部门负责人变更事件
 */
@Getter
public class DepartmentManagerChangedEvent extends DomainEvent {
    private final DepartmentId departmentId;
    private final EmployeeId oldManagerId;
    private final EmployeeId newManagerId;
    
    public DepartmentManagerChangedEvent(DepartmentId departmentId, 
                                        EmployeeId oldManagerId, 
                                        EmployeeId newManagerId) {
        super();
        this.departmentId = departmentId;
        this.oldManagerId = oldManagerId;
        this.newManagerId = newManagerId;
    }
}

