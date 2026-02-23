package com.company.hr.employee.domain.model;

import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 试用期延长事件
 */
@Getter
public class ProbationExtendedEvent extends DomainEvent {
    private final EmployeeId employeeId;
    private final LocalDate oldProbationEndDate;
    private final LocalDate newProbationEndDate;
    private final String reason;
    
    public ProbationExtendedEvent(EmployeeId employeeId, LocalDate oldProbationEndDate, 
                                 LocalDate newProbationEndDate, String reason) {
        super();
        this.employeeId = employeeId;
        this.oldProbationEndDate = oldProbationEndDate;
        this.newProbationEndDate = newProbationEndDate;
        this.reason = reason;
    }
}

