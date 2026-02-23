package com.company.hr.performance.acl;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.acl.AntiCorruptionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 绩效上下文的员工防腐适配器
 * 绩效上下文通过此适配器访问员工信息，避免直接依赖
 */
@Service
@Slf4j
public class EmployeeContextAdapter implements AntiCorruptionService {
    
    /**
     * 验证员工是否可以设置目标
     */
    public boolean canEmployeeSetGoal(EmployeeId employeeId) {
        log.debug("检查员工是否可以设置目标: {}", employeeId.getValue());
        
        // 通过防腐层查询员工状态
        EmployeeBasicInfo info = getEmployeeBasicInfo(employeeId);
        
        // 绩效领域的规则：只有在职员工才能设置目标
        boolean canSet = info.isActive();
        
        log.debug("员工 {} 目标设置权限: {}", employeeId.getValue(), canSet);
        return canSet;
    }
    
    /**
     * 获取员工基本信息
     * 注意：这里返回的是简化的DTO，而不是完整的Employee聚合根
     */
    public EmployeeBasicInfo getEmployeeBasicInfo(EmployeeId employeeId) {
        log.debug("获取员工基本信息: {}", employeeId.getValue());
        
        // 简化实现：实际应该调用员工上下文的查询接口
        EmployeeBasicInfo info = new EmployeeBasicInfo();
        info.setEmployeeId(employeeId.getValue());
        info.setEmployeeName("员工-" + employeeId.getValue());
        info.setActive(true);
        info.setDepartmentId("dept-001");
        info.setPositionId("pos-001");
        
        return info;
    }
    
    /**
     * 员工基本信息DTO
     * 绩效上下文只需要这些信息，不需要完整的员工模型
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeBasicInfo {
        private String employeeId;
        private String employeeName;
        private boolean active;
        private String departmentId;
        private String positionId;
    }
}

