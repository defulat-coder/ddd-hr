package com.company.hr.employee.domain.model;

/**
 * 员工状态枚举
 */
public enum EmployeeStatus {
    PROBATION("试用期"),
    ACTIVE("在职"),
    SUSPENDED("停职"),
    RESIGNED("离职");
    
    private final String description;
    
    EmployeeStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean canTransitionTo(EmployeeStatus newStatus) {
        return switch (this) {
            case PROBATION -> newStatus == ACTIVE || newStatus == RESIGNED;
            case ACTIVE -> newStatus == SUSPENDED || newStatus == RESIGNED;
            case SUSPENDED -> newStatus == ACTIVE || newStatus == RESIGNED;
            case RESIGNED -> false;
        };
    }
}

