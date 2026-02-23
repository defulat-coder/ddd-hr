package com.company.hr.performance.domain.repository;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.performance.domain.model.Goal;
import com.company.hr.performance.domain.model.GoalId;
import com.company.hr.performance.domain.model.GoalStatus;
import com.company.hr.shared.domain.Repository;

import java.util.List;

/**
 * 目标仓储接口
 */
public interface GoalRepository extends Repository<Goal, GoalId> {
    
    /**
     * 根据员工ID查找目标列表
     */
    List<Goal> findByEmployeeId(EmployeeId employeeId);
    
    /**
     * 根据员工ID和状态查找目标列表
     */
    List<Goal> findByEmployeeIdAndStatus(EmployeeId employeeId, GoalStatus status);
    
    /**
     * 查找员工的活跃目标
     */
    List<Goal> findActiveGoalsByEmployeeId(EmployeeId employeeId);
}

