package com.company.hr.performance.domain.model;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.domain.AggregateRoot;
import com.company.hr.shared.exception.DomainException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 目标聚合根（OKR）
 */
@Getter
public class Goal extends AggregateRoot<GoalId> {
    
    private EmployeeId employeeId;
    private String title;
    private String description;
    private GoalPeriod period;
    private GoalStatus status;
    private List<Objective> objectives;
    
    public Goal(GoalId id, EmployeeId employeeId, String title, 
               String description, GoalPeriod period) {
        super(id);
        this.employeeId = employeeId;
        this.title = title;
        this.description = description;
        this.period = period;
        this.status = GoalStatus.DRAFT;
        this.objectives = new ArrayList<>();
        
        period.validate();
        registerEvent(new GoalCreatedEvent(id, employeeId, title));
    }
    
    /**
     * 添加目标项
     */
    public void addObjective(Objective objective) {
        if (status != GoalStatus.DRAFT) {
            throw new DomainException("只有草稿状态的目标才能添加目标项");
        }
        
        // 检查权重总和不超过100
        BigDecimal totalWeight = objectives.stream()
            .map(Objective::getWeight)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .add(objective.getWeight());
        
        if (totalWeight.compareTo(new BigDecimal("100")) > 0) {
            throw new DomainException("目标项权重总和不能超过100%");
        }
        
        this.objectives.add(objective);
    }
    
    /**
     * 激活目标
     */
    public void activate() {
        if (status != GoalStatus.DRAFT) {
            throw new DomainException("只有草稿状态的目标才能激活");
        }
        
        if (objectives.isEmpty()) {
            throw new DomainException("目标至少需要一个目标项");
        }
        
        // 检查权重总和
        BigDecimal totalWeight = objectives.stream()
            .map(Objective::getWeight)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalWeight.compareTo(new BigDecimal("100")) != 0) {
            throw new DomainException("目标项权重总和必须等于100%");
        }
        
        this.status = GoalStatus.ACTIVE;
        registerEvent(new GoalActivatedEvent(getId(), employeeId));
    }
    
    /**
     * 完成目标
     */
    public void complete() {
        if (status != GoalStatus.ACTIVE) {
            throw new DomainException("只有激活状态的目标才能完成");
        }
        this.status = GoalStatus.COMPLETED;
        registerEvent(new GoalCompletedEvent(getId(), employeeId, calculateTotalScore()));
    }
    
    /**
     * 取消目标
     */
    public void cancel() {
        if (status == GoalStatus.COMPLETED || status == GoalStatus.CANCELLED) {
            throw new DomainException("已完成或已取消的目标不能再次取消");
        }
        this.status = GoalStatus.CANCELLED;
    }
    
    /**
     * 计算总得分
     */
    public BigDecimal calculateTotalScore() {
        return objectives.stream()
            .map(Objective::calculateScore)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 获取目标项列表（不可修改）
     */
    public List<Objective> getObjectives() {
        return Collections.unmodifiableList(objectives);
    }
}

