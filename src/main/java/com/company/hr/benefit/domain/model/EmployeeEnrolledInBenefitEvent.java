package com.company.hr.benefit.domain.model;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

/**
 * 员工参加福利事件
 */
@Getter
public class EmployeeEnrolledInBenefitEvent extends DomainEvent {
    private final BenefitId benefitId;
    private final EmployeeId employeeId;
    
    public EmployeeEnrolledInBenefitEvent(BenefitId benefitId, EmployeeId employeeId) {
        super();
        this.benefitId = benefitId;
        this.employeeId = employeeId;
    }
}

