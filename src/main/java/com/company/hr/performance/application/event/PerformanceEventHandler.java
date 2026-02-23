package com.company.hr.performance.application.event;

import com.company.hr.employee.domain.model.EmployeeResignedEvent;
import com.company.hr.employee.domain.model.EmployeeStatusChangedEvent;
import com.company.hr.employee.domain.model.EmployeeTransferredEvent;
import com.company.hr.shared.event.DomainEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 绩效上下文的事件处理器
 * 监听员工上下文的事件，实现跨上下文响应
 */
@Component
@DomainEventHandler(
    value = "PerformanceEventHandler",
    description = "处理员工事件对绩效管理的影响"
)
@Slf4j
public class PerformanceEventHandler {
    
    /**
     * 处理员工离职事件
     * 当员工离职时，自动取消其所有活跃的目标
     */
    @EventListener
    @Async
    public void handleEmployeeResigned(EmployeeResignedEvent event) {
        log.info("【绩效上下文】收到员工离职事件: 员工号={}", event.getEmployeeNumber());
        
        // 取消该员工的所有活跃目标
        cancelActiveGoals(event.getEmployeeId().getValue());
        
        // 生成最终绩效报告
        generateFinalPerformanceReport(event.getEmployeeId().getValue());
        
        log.info("【绩效上下文】员工离职事件处理完成");
    }
    
    /**
     * 处理员工调动事件
     * 当员工调动时，通知其经理进行绩效评估
     */
    @EventListener
    @Async
    public void handleEmployeeTransferred(EmployeeTransferredEvent event) {
        log.info("【绩效上下文】收到员工调动事件: 员工ID={}", 
            event.getEmployeeId().getValue());
        
        // 通知原经理进行调动前评估
        notifyOldManagerForEvaluation(
            event.getEmployeeId().getValue(),
            event.getOldDepartmentId().getValue()
        );
        
        // 通知新经理设置新目标
        notifyNewManagerForGoalSetting(
            event.getEmployeeId().getValue(),
            event.getNewDepartmentId().getValue()
        );
        
        log.info("【绩效上下文】员工调动事件处理完成");
    }
    
    /**
     * 处理员工状态变更事件
     * 当员工转正时，启动正式员工的绩效考核
     */
    @EventListener
    @Async
    public void handleEmployeeStatusChanged(EmployeeStatusChangedEvent event) {
        log.info("【绩效上下文】收到员工状态变更事件: 员工ID={}, 状态: {} -> {}", 
            event.getEmployeeId().getValue(),
            event.getOldStatus(),
            event.getNewStatus());
        
        // 如果是转正，初始化正式员工的绩效体系
        if (event.getOldStatus().name().equals("PROBATION") 
            && event.getNewStatus().name().equals("ACTIVE")) {
            initializeFormalPerformanceSystem(event.getEmployeeId().getValue());
        }
        
        log.info("【绩效上下文】员工状态变更事件处理完成");
    }
    
    // ========== 私有方法 ==========
    
    private void cancelActiveGoals(String employeeId) {
        log.debug("取消员工的所有活跃目标: {}", employeeId);
        // TODO: 查询并取消该员工的所有活跃Goal
        // goalRepository.findActiveGoalsByEmployeeId(employeeId)
        //     .forEach(goal -> goal.cancel());
    }
    
    private void generateFinalPerformanceReport(String employeeId) {
        log.debug("生成最终绩效报告: {}", employeeId);
        // TODO: 生成离职员工的绩效总结报告
    }
    
    private void notifyOldManagerForEvaluation(String employeeId, String oldDepartmentId) {
        log.debug("通知原经理进行调动前评估: 员工={}, 部门={}", employeeId, oldDepartmentId);
        // TODO: 发送通知给原部门经理
    }
    
    private void notifyNewManagerForGoalSetting(String employeeId, String newDepartmentId) {
        log.debug("通知新经理设置新目标: 员工={}, 部门={}", employeeId, newDepartmentId);
        // TODO: 发送通知给新部门经理
    }
    
    private void initializeFormalPerformanceSystem(String employeeId) {
        log.debug("初始化正式员工绩效体系: {}", employeeId);
        // TODO: 创建年度目标模板，设置考核周期等
    }
}

