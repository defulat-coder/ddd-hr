package com.company.hr.benefit.domain.model;

import lombok.Value;

import java.util.UUID;

/**
 * 福利参加ID值对象
 */
@Value
public class EnrollmentId {
    String value;
    
    public static EnrollmentId generate() {
        return new EnrollmentId(UUID.randomUUID().toString());
    }
    
    public static EnrollmentId of(String value) {
        return new EnrollmentId(value);
    }
}

