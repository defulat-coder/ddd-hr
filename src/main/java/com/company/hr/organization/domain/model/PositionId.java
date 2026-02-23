package com.company.hr.organization.domain.model;

import lombok.Value;

import java.util.UUID;

/**
 * 职位ID值对象
 */
@Value
public class PositionId {
    String value;
    
    public static PositionId generate() {
        return new PositionId(UUID.randomUUID().toString());
    }
    
    public static PositionId of(String value) {
        return new PositionId(value);
    }
}

