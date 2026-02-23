package com.company.hr.benefit.acl;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.acl.AntiCorruptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 员工上下文防腐服务
 * 福利上下文通过此服务访问员工上下文，避免直接耦合
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeContextService implements AntiCorruptionService {
    
    private final EmployeeContextTranslator translator;
    
    /**
     * 验证员工是否有资格参加福利
     * 通过防腐层访问员工信息，保护福利领域不受员工领域变化影响
     */
    public boolean isEmployeeEligibleForBenefit(EmployeeId employeeId) {
        log.debug("检查员工福利资格: {}", employeeId.getValue());
        
        // 通过翻译器获取员工信息
        EmployeeContextTranslator.EmployeeInfo employeeInfo = 
            translator.translate(employeeId);
        
        // 福利领域的业务规则：只有在职员工才能参加福利
        boolean eligible = employeeInfo.isActive();
        
        log.debug("员工 {} 福利资格检查结果: {}", employeeId.getValue(), eligible);
        return eligible;
    }
    
    /**
     * 获取员工信息
     */
    public EmployeeContextTranslator.EmployeeInfo getEmployeeInfo(EmployeeId employeeId) {
        log.debug("获取员工信息: {}", employeeId.getValue());
        return translator.translate(employeeId);
    }
}

