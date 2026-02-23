package com.company.hr.organization.domain.model;

import com.company.hr.shared.domain.Entity;
import com.company.hr.shared.exception.DomainException;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 职位实体
 */
@Getter
public class Position extends Entity<PositionId> {
    
    private String title;
    private String code;
    private PositionLevel level;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String description;
    private int headcount; // 在职人数
    private int maxHeadcount; // 最大编制
    
    public Position(PositionId id, String title, String code, PositionLevel level, 
                   BigDecimal minSalary, BigDecimal maxSalary, 
                   String description, int maxHeadcount) {
        super(id);
        this.title = title;
        this.code = code;
        this.level = level;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.description = description;
        this.headcount = 0;
        this.maxHeadcount = maxHeadcount;
        
        validateSalaryRange();
    }
    
    private void validateSalaryRange() {
        if (minSalary.compareTo(maxSalary) > 0) {
            throw new DomainException("最低薪资不能大于最高薪资");
        }
        if (minSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("薪资不能为负数");
        }
    }
    
    /**
     * 更新职位信息
     */
    public void updateInfo(String title, String description, 
                          BigDecimal minSalary, BigDecimal maxSalary) {
        this.title = title;
        this.description = description;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        validateSalaryRange();
    }
    
    /**
     * 增加在职人数
     */
    public void incrementHeadcount() {
        if (headcount >= maxHeadcount) {
            throw new DomainException("该职位已达到最大编制");
        }
        this.headcount++;
    }
    
    /**
     * 减少在职人数
     */
    public void decrementHeadcount() {
        if (headcount <= 0) {
            throw new DomainException("在职人数不能为负数");
        }
        this.headcount--;
    }
    
    /**
     * 调整编制
     */
    public void adjustMaxHeadcount(int newMaxHeadcount) {
        if (newMaxHeadcount < headcount) {
            throw new DomainException("新编制不能小于当前在职人数");
        }
        this.maxHeadcount = newMaxHeadcount;
    }
    
    /**
     * 是否有空缺
     */
    public boolean hasVacancy() {
        return headcount < maxHeadcount;
    }
}

