package com.company.hr.benefit.domain.model;

/**
 * 福利类型枚举
 */
public enum BenefitType {
    HEALTH_INSURANCE("健康保险"),
    PENSION("养老金"),
    MEAL_ALLOWANCE("餐补"),
    TRANSPORT_ALLOWANCE("交通补贴"),
    HOUSING_ALLOWANCE("住房补贴"),
    EDUCATION_SUBSIDY("教育补助"),
    GYM_MEMBERSHIP("健身会员"),
    ANNUAL_LEAVE("年假"),
    SICK_LEAVE("病假"),
    OTHER("其他");
    
    private final String description;
    
    BenefitType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

