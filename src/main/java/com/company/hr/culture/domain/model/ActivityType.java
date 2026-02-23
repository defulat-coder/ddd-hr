package com.company.hr.culture.domain.model;

/**
 * 活动类型枚举
 */
public enum ActivityType {
    TEAM_BUILDING("团建活动"),
    TRAINING("培训"),
    WORKSHOP("工作坊"),
    CELEBRATION("庆祝活动"),
    SPORTS("体育活动"),
    VOLUNTEER("志愿者活动"),
    CULTURE_SHARING("文化分享"),
    ANNUAL_MEETING("年会"),
    OTHER("其他");
    
    private final String description;
    
    ActivityType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

