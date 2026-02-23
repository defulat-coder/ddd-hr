package com.company.hr.performance.domain.model;

import lombok.Value;

import java.util.UUID;

/**
 * 目标ID值对象
 */
@Value
public class GoalId {
    String value;
    
    public static GoalId generate() {
        return new GoalId(UUID.randomUUID().toString());
    }
    
    public static GoalId of(String value) {
        return new GoalId(value);
    }
}

