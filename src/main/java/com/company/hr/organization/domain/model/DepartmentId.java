package com.company.hr.organization.domain.model;

import lombok.Value;

import java.util.UUID;

/**
 * 部门ID值对象
 */
@Value
public class DepartmentId {
    String value;
    
    public static DepartmentId generate() {
        return new DepartmentId(UUID.randomUUID().toString());
    }
    
    public static DepartmentId of(String value) {
        return new DepartmentId(value);
    }
}

