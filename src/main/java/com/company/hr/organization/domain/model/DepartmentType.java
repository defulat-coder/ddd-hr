package com.company.hr.organization.domain.model;

/**
 * 部门类型枚举
 */
public enum DepartmentType {
    HEADQUARTERS("总部"),
    BRANCH("分公司"),
    DEPARTMENT("部门"),
    TEAM("团队");
    
    private final String description;
    
    DepartmentType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

