package com.company.hr.culture.domain.model;

import lombok.Value;

import java.util.UUID;

/**
 * 活动ID值对象
 */
@Value
public class ActivityId {
    String value;
    
    public static ActivityId generate() {
        return new ActivityId(UUID.randomUUID().toString());
    }
    
    public static ActivityId of(String value) {
        return new ActivityId(value);
    }
}

