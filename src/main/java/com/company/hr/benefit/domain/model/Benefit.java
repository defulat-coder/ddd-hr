package com.company.hr.benefit.domain.model;

import com.company.hr.shared.domain.AggregateRoot;
import com.company.hr.shared.exception.DomainException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 福利聚合根
 */
@Getter
public class Benefit extends AggregateRoot<BenefitId> {
    
    private String name;
    private String description;
    private BenefitType type;
    private BenefitCost cost;
    private boolean active;
    private String eligibilityCriteria; // 资格标准
    private List<BenefitEnrollment> enrollments;
    
    public Benefit(BenefitId id, String name, String description, 
                  BenefitType type, BenefitCost cost, String eligibilityCriteria) {
        super(id);
        this.name = name;
        this.description = description;
        this.type = type;
        this.cost = cost;
        this.eligibilityCriteria = eligibilityCriteria;
        this.active = true;
        this.enrollments = new ArrayList<>();
        
        cost.validate();
        registerEvent(new BenefitCreatedEvent(id, name, type));
    }
    
    /**
     * 添加参加记录
     */
    public void addEnrollment(BenefitEnrollment enrollment) {
        if (!active) {
            throw new DomainException("福利已停用，不能添加参加记录");
        }
        
        // 检查员工是否已经参加
        boolean alreadyEnrolled = enrollments.stream()
            .anyMatch(e -> e.getEmployeeId().equals(enrollment.getEmployeeId()) 
                && e.isActive());
        
        if (alreadyEnrolled) {
            throw new DomainException("员工已经参加该福利");
        }
        
        this.enrollments.add(enrollment);
        registerEvent(new EmployeeEnrolledInBenefitEvent(getId(), enrollment.getEmployeeId()));
    }
    
    /**
     * 更新福利信息
     */
    public void updateInfo(String name, String description, BenefitCost cost) {
        this.name = name;
        this.description = description;
        this.cost = cost;
        cost.validate();
    }
    
    /**
     * 停用福利
     */
    public void deactivate() {
        if (!active) {
            throw new DomainException("福利已经是停用状态");
        }
        this.active = false;
    }
    
    /**
     * 启用福利
     */
    public void activate() {
        if (active) {
            throw new DomainException("福利已经是启用状态");
        }
        this.active = true;
    }
    
    /**
     * 获取参加记录列表（不可修改）
     */
    public List<BenefitEnrollment> getEnrollments() {
        return Collections.unmodifiableList(enrollments);
    }
    
    /**
     * 获取活跃的参加人数
     */
    public long getActiveEnrollmentCount() {
        return enrollments.stream()
            .filter(BenefitEnrollment::isActive)
            .count();
    }
}

