package com.company.hr.performance.domain.model;

/**
 * 目标项状态枚举
 */
public enum ObjectiveStatus {
    NOT_STARTED("未开始"),
    IN_PROGRESS("进行中"),
    COMPLETED("已完成"),
    DELAYED("延期"),
    CANCELLED("已取消");
    
    private final String description;
    
    ObjectiveStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

