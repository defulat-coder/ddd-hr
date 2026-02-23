package com.company.hr.benefit.domain.model;

import com.company.hr.shared.domain.ValueObject;
import lombok.Value;

import java.math.BigDecimal;

/**
 * 福利成本值对象
 */
@Value
public class BenefitCost implements ValueObject {
    BigDecimal employerCost; // 企业承担费用
    BigDecimal employeeCost; // 员工承担费用
    
    public BigDecimal getTotalCost() {
        return employerCost.add(employeeCost);
    }
    
    public void validate() {
        if (employerCost.compareTo(BigDecimal.ZERO) < 0 || 
            employeeCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("费用不能为负数");
        }
    }
}

