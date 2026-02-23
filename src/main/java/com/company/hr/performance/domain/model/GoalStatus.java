package com.company.hr.performance.domain.model;

/**
 * 目标状态枚举
 */
public enum GoalStatus {
    DRAFT("草稿"),
    ACTIVE("进行中"),
    COMPLETED("已完成"),
    CANCELLED("已取消");
    
    private final String description;
    
    GoalStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

