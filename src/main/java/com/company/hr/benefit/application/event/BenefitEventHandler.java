package com.company.hr.benefit.application.event;

import com.company.hr.employee.domain.model.EmployeeHiredEvent;
import com.company.hr.employee.domain.model.EmployeeResignedEvent;
import com.company.hr.employee.domain.model.EmployeeStatusChangedEvent;
import com.company.hr.shared.event.DomainEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 福利上下文的事件处理器
 * 监听员工上下文的事件，实现福利的自动化管理
 */
@Component
@DomainEventHandler(
    value = "BenefitEventHandler",
    description = "处理员工事件对福利管理的影响"
)
@Slf4j
public class BenefitEventHandler {
    
    /**
     * 处理员工入职事件
     * 自动为新员工配置基础福利
     */
    @EventListener
    @Async
    public void handleEmployeeHired(EmployeeHiredEvent event) {
        log.info("【福利上下文】收到员工入职事件: 员工号={}", event.getEmployeeNumber());
        
        // 自动为新员工配置基础福利
        enrollBasicBenefits(event.getEmployeeId().getValue());
        
        // 发送福利说明
        sendBenefitIntroduction(event.getEmployeeId().getValue());
        
        log.info("【福利上下文】员工入职事件处理完成");
    }
    
    /**
     * 处理员工转正事件
     * 转正后开放更多福利选项
     */
    @EventListener
    @Async
    public void handleEmployeeStatusChanged(EmployeeStatusChangedEvent event) {
        log.info("【福利上下文】收到员工状态变更事件: 员工ID={}", 
            event.getEmployeeId().getValue());
        
        // 如果是转正，开放正式员工福利
        if (event.getOldStatus().name().equals("PROBATION") 
            && event.getNewStatus().name().equals("ACTIVE")) {
            enableFormalEmployeeBenefits(event.getEmployeeId().getValue());
        }
        
        log.info("【福利上下文】员工状态变更事件处理完成");
    }
    
    /**
     * 处理员工离职事件
     * 自动终止所有福利
     */
    @EventListener
    @Async
    public void handleEmployeeResigned(EmployeeResignedEvent event) {
        log.info("【福利上下文】收到员工离职事件: 员工号={}", event.getEmployeeNumber());
        
        // 终止所有福利
        terminateAllBenefits(event.getEmployeeId().getValue(), event.getResignDate());
        
        // 生成福利结算报告
        generateBenefitSettlementReport(event.getEmployeeId().getValue());
        
        log.info("【福利上下文】员工离职事件处理完成");
    }
    
    // ========== 私有方法 ==========
    
    private void enrollBasicBenefits(String employeeId) {
        log.debug("为新员工配置基础福利: {}", employeeId);
        // TODO: 自动参加五险一金、餐补等基础福利
        // List<Benefit> basicBenefits = benefitRepository.findByType(BenefitType.BASIC);
        // basicBenefits.forEach(benefit -> benefit.addEnrollment(...));
    }
    
    private void sendBenefitIntroduction(String employeeId) {
        log.debug("发送福利说明: {}", employeeId);
        // TODO: 发送福利手册和说明
    }
    
    private void enableFormalEmployeeBenefits(String employeeId) {
        log.debug("开放正式员工福利: {}", employeeId);
        // TODO: 开放年假、补充医疗保险等正式员工福利
    }
    
    private void terminateAllBenefits(String employeeId, java.time.LocalDate resignDate) {
        log.debug("终止所有福利: 员工={}, 离职日期={}", employeeId, resignDate);
        // TODO: 查询并终止该员工的所有福利
        // benefitRepository.findByEmployeeId(employeeId)
        //     .forEach(benefit -> benefit.terminate(resignDate));
    }
    
    private void generateBenefitSettlementReport(String employeeId) {
        log.debug("生成福利结算报告: {}", employeeId);
        // TODO: 计算福利费用，生成结算报告
    }
}

