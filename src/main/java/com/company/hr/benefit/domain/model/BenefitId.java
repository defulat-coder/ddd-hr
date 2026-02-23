package com.company.hr.benefit.domain.model;

import lombok.Value;

import java.util.UUID;

/**
 * 福利ID值对象
 */
@Value
public class BenefitId {
    String value;
    
    public static BenefitId generate() {
        return new BenefitId(UUID.randomUUID().toString());
    }
    
    public static BenefitId of(String value) {
        return new BenefitId(value);
    }
}

