package com.company.hr.organization.domain.model;

/**
 * 职位级别枚举
 */
public enum PositionLevel {
    JUNIOR("初级"),
    INTERMEDIATE("中级"),
    SENIOR("高级"),
    EXPERT("专家"),
    MANAGER("经理"),
    SENIOR_MANAGER("高级经理"),
    DIRECTOR("总监"),
    VP("副总裁"),
    EXECUTIVE("高管");
    
    private final String description;
    
    PositionLevel(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

