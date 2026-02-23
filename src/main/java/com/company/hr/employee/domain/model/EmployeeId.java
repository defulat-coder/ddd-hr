package com.company.hr.employee.domain.model;

import lombok.Value;

import java.util.UUID;

/**
 * 员工ID值对象
 */
@Value
public class EmployeeId {
    String value;
    
    public static EmployeeId generate() {
        return new EmployeeId(UUID.randomUUID().toString());
    }
    
    public static EmployeeId of(String value) {
        return new EmployeeId(value);
    }
}

