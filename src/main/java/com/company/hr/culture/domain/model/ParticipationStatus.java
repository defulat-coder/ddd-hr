package com.company.hr.culture.domain.model;

/**
 * 参与状态枚举
 */
public enum ParticipationStatus {
    REGISTERED("已报名"),
    CONFIRMED("已确认"),
    ATTENDED("已参加"),
    ABSENT("缺席"),
    CANCELLED("已取消");
    
    private final String description;
    
    ParticipationStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

