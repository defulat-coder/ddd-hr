package com.company.hr.culture.domain.model;

import lombok.Value;

import java.util.UUID;

/**
 * 参与ID值对象
 */
@Value
public class ParticipationId {
    String value;
    
    public static ParticipationId generate() {
        return new ParticipationId(UUID.randomUUID().toString());
    }
    
    public static ParticipationId of(String value) {
        return new ParticipationId(value);
    }
}

