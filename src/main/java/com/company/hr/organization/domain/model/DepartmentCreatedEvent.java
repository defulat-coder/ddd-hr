package com.company.hr.organization.domain.model;

import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

/**
 * 部门创建事件
 */
@Getter
public class DepartmentCreatedEvent extends DomainEvent {
    private final DepartmentId departmentId;
    private final String name;
    private final String code;
    
    public DepartmentCreatedEvent(DepartmentId departmentId, String name, String code) {
        super();
        this.departmentId = departmentId;
        this.name = name;
        this.code = code;
    }
}

