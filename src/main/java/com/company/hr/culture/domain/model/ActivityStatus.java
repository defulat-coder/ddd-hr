package com.company.hr.culture.domain.model;

/**
 * 活动状态枚举
 */
public enum ActivityStatus {
    PLANNED("计划中"),
    REGISTRATION_OPEN("报名开放"),
    REGISTRATION_CLOSED("报名截止"),
    IN_PROGRESS("进行中"),
    COMPLETED("已完成"),
    CANCELLED("已取消");
    
    private final String description;
    
    ActivityStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

