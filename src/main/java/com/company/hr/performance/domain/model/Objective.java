package com.company.hr.performance.domain.model;

import com.company.hr.shared.domain.Entity;
import com.company.hr.shared.exception.DomainException;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 目标项实体
 */
@Getter
public class Objective extends Entity<ObjectiveId> {
    
    private String description;
    private String keyResult; // 关键结果
    private BigDecimal weight; // 权重（百分比）
    private BigDecimal targetValue; // 目标值
    private BigDecimal actualValue; // 实际值
    private ObjectiveStatus status;
    
    public Objective(ObjectiveId id, String description, String keyResult, 
                    BigDecimal weight, BigDecimal targetValue) {
        super(id);
        this.description = description;
        this.keyResult = keyResult;
        this.weight = weight;
        this.targetValue = targetValue;
        this.actualValue = BigDecimal.ZERO;
        this.status = ObjectiveStatus.NOT_STARTED;
        
        validateWeight();
    }
    
    private void validateWeight() {
        if (weight.compareTo(BigDecimal.ZERO) <= 0 || 
            weight.compareTo(new BigDecimal("100")) > 0) {
            throw new DomainException("权重必须在0-100之间");
        }
    }
    
    /**
     * 更新进度
     */
    public void updateProgress(BigDecimal actualValue, ObjectiveStatus status) {
        this.actualValue = actualValue;
        this.status = status;
    }
    
    /**
     * 计算完成率
     */
    public BigDecimal getCompletionRate() {
        if (targetValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return actualValue.divide(targetValue, 2, BigDecimal.ROUND_HALF_UP)
            .multiply(new BigDecimal("100"));
    }
    
    /**
     * 计算得分（权重 * 完成率）
     */
    public BigDecimal calculateScore() {
        return weight.multiply(getCompletionRate())
            .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
    }
}

