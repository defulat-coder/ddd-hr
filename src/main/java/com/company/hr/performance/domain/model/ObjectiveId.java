package com.company.hr.performance.domain.model;

import lombok.Value;

import java.util.UUID;

/**
 * 目标项ID值对象
 */
@Value
public class ObjectiveId {
    String value;
    
    public static ObjectiveId generate() {
        return new ObjectiveId(UUID.randomUUID().toString());
    }
    
    public static ObjectiveId of(String value) {
        return new ObjectiveId(value);
    }
}

