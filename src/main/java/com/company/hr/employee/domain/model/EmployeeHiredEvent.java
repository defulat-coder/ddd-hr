package com.company.hr.employee.domain.model;

import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 员工入职事件
 */
@Getter
public class EmployeeHiredEvent extends DomainEvent {
    private final EmployeeId employeeId;
    private final String employeeNumber;
    private final LocalDate hireDate;
    
    public EmployeeHiredEvent(EmployeeId employeeId, String employeeNumber, LocalDate hireDate) {
        super();
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.hireDate = hireDate;
    }
}

