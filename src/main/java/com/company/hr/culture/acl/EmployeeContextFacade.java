package com.company.hr.culture.acl;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.acl.AntiCorruptionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文化上下文的员工防腐门面
 * 文化活动需要员工信息时，通过此门面访问
 */
@Service
@Slf4j
public class EmployeeContextFacade implements AntiCorruptionService {
    
    /**
     * 验证员工是否可以报名活动
     */
    public boolean canEmployeeRegisterActivity(EmployeeId employeeId) {
        log.debug("检查员工是否可以报名活动: {}", employeeId.getValue());
        
        // 通过防腐层查询员工状态
        EmployeeSimpleInfo info = getEmployeeSimpleInfo(employeeId);
        
        // 文化领域的规则：只有在职员工才能报名
        boolean canRegister = info.isActive();
        
        log.debug("员工 {} 活动报名权限: {}", employeeId.getValue(), canRegister);
        return canRegister;
    }
    
    /**
     * 获取员工简要信息
     */
    public EmployeeSimpleInfo getEmployeeSimpleInfo(EmployeeId employeeId) {
        log.debug("获取员工简要信息: {}", employeeId.getValue());
        
        // 简化实现：实际应该调用员工上下文的查询接口
        EmployeeSimpleInfo info = new EmployeeSimpleInfo();
        info.setEmployeeId(employeeId.getValue());
        info.setEmployeeName("员工-" + employeeId.getValue());
        info.setActive(true);
        info.setEmail("employee@company.com");
        
        return info;
    }
    
    /**
     * 批量获取员工信息
     * 用于活动通知等场景
     */
    public List<EmployeeSimpleInfo> getEmployeesSimpleInfo(List<EmployeeId> employeeIds) {
        log.debug("批量获取员工信息，数量: {}", employeeIds.size());
        
        // 简化实现：实际应该批量查询以提高性能
        return employeeIds.stream()
            .map(this::getEmployeeSimpleInfo)
            .toList();
    }
    
    /**
     * 员工简要信息DTO
     * 文化上下文只需要这些信息用于通知和展示
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeSimpleInfo {
        private String employeeId;
        private String employeeName;
        private boolean active;
        private String email;
        private String phoneNumber;
    }
}

