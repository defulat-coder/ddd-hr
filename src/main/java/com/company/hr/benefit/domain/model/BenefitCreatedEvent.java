package com.company.hr.benefit.domain.model;

import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

/**
 * 福利创建事件
 */
@Getter
public class BenefitCreatedEvent extends DomainEvent {
    private final BenefitId benefitId;
    private final String name;
    private final BenefitType type;
    
    public BenefitCreatedEvent(BenefitId benefitId, String name, BenefitType type) {
        super();
        this.benefitId = benefitId;
        this.name = name;
        this.type = type;
    }
}

