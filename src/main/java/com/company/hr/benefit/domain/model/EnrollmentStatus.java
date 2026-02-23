package com.company.hr.benefit.domain.model;

/**
 * 福利参加状态枚举
 */
public enum EnrollmentStatus {
    PENDING("待审批"),
    APPROVED("已批准"),
    ACTIVE("生效中"),
    SUSPENDED("已暂停"),
    CANCELLED("已取消"),
    EXPIRED("已过期");
    
    private final String description;
    
    EnrollmentStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

