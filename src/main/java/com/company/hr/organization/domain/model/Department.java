package com.company.hr.organization.domain.model;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.domain.AggregateRoot;
import com.company.hr.shared.exception.DomainException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 部门聚合根
 */
@Getter
public class Department extends AggregateRoot<DepartmentId> {
    
    private String name;
    private String code;
    private DepartmentType type;
    private DepartmentId parentId;
    private EmployeeId managerId;
    private String description;
    private boolean active;
    private List<Position> positions;
    
    public Department(DepartmentId id, String name, String code, DepartmentType type, 
                     DepartmentId parentId, EmployeeId managerId, String description) {
        super(id);
        this.name = name;
        this.code = code;
        this.type = type;
        this.parentId = parentId;
        this.managerId = managerId;
        this.description = description;
        this.active = true;
        this.positions = new ArrayList<>();
        
        registerEvent(new DepartmentCreatedEvent(id, name, code));
    }
    
    /**
     * 添加职位
     */
    public void addPosition(Position position) {
        if (!active) {
            throw new DomainException("部门已停用，不能添加职位");
        }
        if (positions.stream().anyMatch(p -> p.getId().equals(position.getId()))) {
            throw new DomainException("职位已存在");
        }
        this.positions.add(position);
    }
    
    /**
     * 移除职位
     */
    public void removePosition(PositionId positionId) {
        Position position = positions.stream()
            .filter(p -> p.getId().equals(positionId))
            .findFirst()
            .orElseThrow(() -> new DomainException("职位不存在"));
        
        if (position.getHeadcount() > 0) {
            throw new DomainException("职位还有在职人员，不能删除");
        }
        
        this.positions.remove(position);
    }
    
    /**
     * 更新部门信息
     */
    public void updateInfo(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    /**
     * 更换部门负责人
     */
    public void changeManager(EmployeeId newManagerId) {
        EmployeeId oldManagerId = this.managerId;
        this.managerId = newManagerId;
        registerEvent(new DepartmentManagerChangedEvent(getId(), oldManagerId, newManagerId));
    }
    
    /**
     * 停用部门
     */
    public void deactivate() {
        if (!active) {
            throw new DomainException("部门已经是停用状态");
        }
        // 检查是否还有在职员工
        int totalHeadcount = positions.stream()
            .mapToInt(Position::getHeadcount)
            .sum();
        if (totalHeadcount > 0) {
            throw new DomainException("部门还有在职员工，不能停用");
        }
        this.active = false;
    }
    
    /**
     * 启用部门
     */
    public void activate() {
        if (active) {
            throw new DomainException("部门已经是启用状态");
        }
        this.active = true;
    }
    
    /**
     * 获取职位列表（不可修改）
     */
    public List<Position> getPositions() {
        return Collections.unmodifiableList(positions);
    }
    
    /**
     * 是否是顶级部门
     */
    public boolean isTopLevel() {
        return parentId == null;
    }
}

