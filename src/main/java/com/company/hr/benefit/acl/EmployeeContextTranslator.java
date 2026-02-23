package com.company.hr.benefit.acl;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.acl.Translator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 员工上下文翻译器
 * 防腐层：将员工上下文的数据翻译为福利上下文可用的数据
 */
@Component
public class EmployeeContextTranslator implements Translator<EmployeeId, EmployeeContextTranslator.EmployeeInfo> {
    
    @Override
    public EmployeeInfo translate(EmployeeId source) {
        // 这里应该通过防腐层查询员工信息
        // 而不是直接依赖员工聚合根
        
        // 简化实现：实际应该调用员工上下文的查询接口
        EmployeeInfo info = new EmployeeInfo();
        info.setEmployeeId(source.getValue());
        info.setActive(true); // 简化：应该从员工上下文查询
        
        return info;
    }
    
    /**
     * 员工信息DTO
     * 福利上下文只需要知道员工的基本信息，不需要完整的员工聚合
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeInfo {
        private String employeeId;
        private String employeeNumber;
        private String fullName;
        private boolean active;
        private String departmentId;
    }
}

