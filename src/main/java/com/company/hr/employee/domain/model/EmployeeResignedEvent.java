package com.company.hr.employee.domain.model;

import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 员工离职事件
 */
@Getter
public class EmployeeResignedEvent extends DomainEvent {
    private final EmployeeId employeeId;
    private final String employeeNumber;
    private final LocalDate resignDate;
    private final String reason;
    private final String resignType; // RESIGNATION(辞职) 或 TERMINATION(辞退)
    
    public EmployeeResignedEvent(EmployeeId employeeId, String employeeNumber, 
                                LocalDate resignDate, String reason, String resignType) {
        super();
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.resignDate = resignDate;
        this.reason = reason;
        this.resignType = resignType;
    }
}

