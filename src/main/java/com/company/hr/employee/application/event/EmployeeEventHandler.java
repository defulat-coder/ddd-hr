package com.company.hr.employee.application.event;

import com.company.hr.employee.domain.model.*;
import com.company.hr.shared.event.DomainEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 员工领域事件处理器
 * 处理员工相关的所有领域事件
 */
@Component
@DomainEventHandler(
    value = "EmployeeEventHandler",
    description = "处理员工生命周期相关的领域事件"
)
@Slf4j
public class EmployeeEventHandler {
    
    /**
     * 处理员工入职事件
     * 可以触发：发送欢迎邮件、创建系统账号、通知相关部门等
     */
    @EventListener
    @Async
    public void handleEmployeeHired(EmployeeHiredEvent event) {
        log.info("【事件处理】员工入职: 员工号={}, 入职日期={}", 
            event.getEmployeeNumber(), event.getHireDate());
        
        // 业务处理
        sendWelcomeEmail(event);
        notifyHRDepartment(event);
        createSystemAccount(event);
        
        log.info("【事件完成】员工入职事件处理完成: {}", event.getEmployeeNumber());
    }
    
    /**
     * 处理员工状态变更事件
     * 可以触发：更新权限、通知相关系统等
     */
    @EventListener
    @Async
    public void handleEmployeeStatusChanged(EmployeeStatusChangedEvent event) {
        log.info("【事件处理】员工状态变更: 员工ID={}, 状态: {} -> {}", 
            event.getEmployeeId().getValue(), 
            event.getOldStatus().getDescription(), 
            event.getNewStatus().getDescription());
        
        // 业务处理
        updateEmployeePermissions(event);
        notifyRelatedSystems(event);
        
        // 特殊处理：转正
        if (event.getOldStatus() == EmployeeStatus.PROBATION 
            && event.getNewStatus() == EmployeeStatus.ACTIVE) {
            handleEmployeeConfirmation(event);
        }
        
        log.info("【事件完成】员工状态变更事件处理完成");
    }
    
    /**
     * 处理试用期延长事件
     */
    @EventListener
    @Async
    public void handleProbationExtended(ProbationExtendedEvent event) {
        log.info("【事件处理】试用期延长: 员工ID={}, 原结束日期={}, 新结束日期={}, 原因={}", 
            event.getEmployeeId().getValue(),
            event.getOldProbationEndDate(),
            event.getNewProbationEndDate(),
            event.getReason());
        
        // 业务处理
        notifyEmployeeAndManager(event);
        updateHRSystem(event);
        
        log.info("【事件完成】试用期延长事件处理完成");
    }
    
    /**
     * 处理员工调动事件
     * 可以触发：通知新旧部门、更新工位、转移权限等
     */
    @EventListener
    @Async
    public void handleEmployeeTransferred(EmployeeTransferredEvent event) {
        log.info("【事件处理】员工调动: 员工ID={}, 部门: {} -> {}, 职位: {} -> {}, 原因={}", 
            event.getEmployeeId().getValue(),
            event.getOldDepartmentId().getValue(),
            event.getNewDepartmentId().getValue(),
            event.getOldPositionId().getValue(),
            event.getNewPositionId().getValue(),
            event.getReason());
        
        // 业务处理
        notifyOldDepartment(event);
        notifyNewDepartment(event);
        updateWorkstation(event);
        transferPermissions(event);
        
        log.info("【事件完成】员工调动事件处理完成");
    }
    
    /**
     * 处理员工晋升事件
     * 可以触发：更新职级、调整薪资、发送祝贺等
     */
    @EventListener
    @Async
    public void handleEmployeePromoted(EmployeePromotedEvent event) {
        log.info("【事件处理】员工晋升: 员工ID={}, 职位: {} -> {}, 原因={}", 
            event.getEmployeeId().getValue(),
            event.getOldPositionId().getValue(),
            event.getNewPositionId().getValue(),
            event.getReason());
        
        // 业务处理
        sendCongratulations(event);
        updatePositionLevel(event);
        notifyHRForSalaryAdjustment(event);
        
        log.info("【事件完成】员工晋升事件处理完成");
    }
    
    /**
     * 处理员工离职事件
     * 可以触发：离职手续、账号禁用、通知相关人员等
     */
    @EventListener
    @Async
    public void handleEmployeeResigned(EmployeeResignedEvent event) {
        log.info("【事件处理】员工离职: 员工号={}, 离职日期={}, 类型={}, 原因={}", 
            event.getEmployeeNumber(),
            event.getResignDate(),
            event.getResignType(),
            event.getReason());
        
        // 业务处理
        initiateResignationProcess(event);
        disableSystemAccount(event);
        notifyTeamMembers(event);
        scheduleExitInterview(event);
        
        // 根据离职类型做不同处理
        if ("TERMINATION".equals(event.getResignType())) {
            handleTermination(event);
        } else {
            handleResignation(event);
        }
        
        log.info("【事件完成】员工离职事件处理完成");
    }
    
    // ========== 私有辅助方法 ==========
    
    private void sendWelcomeEmail(EmployeeHiredEvent event) {
        log.debug("发送欢迎邮件给员工: {}", event.getEmployeeNumber());
        // TODO: 集成邮件服务
    }
    
    private void notifyHRDepartment(EmployeeHiredEvent event) {
        log.debug("通知HR部门新员工入职: {}", event.getEmployeeNumber());
        // TODO: 通知逻辑
    }
    
    private void createSystemAccount(EmployeeHiredEvent event) {
        log.debug("创建系统账号: {}", event.getEmployeeNumber());
        // TODO: 集成账号系统
    }
    
    private void updateEmployeePermissions(EmployeeStatusChangedEvent event) {
        log.debug("更新员工权限: {}", event.getEmployeeId().getValue());
        // TODO: 权限管理
    }
    
    private void notifyRelatedSystems(EmployeeStatusChangedEvent event) {
        log.debug("通知相关系统状态变更: {}", event.getEmployeeId().getValue());
        // TODO: 系统集成
    }
    
    private void handleEmployeeConfirmation(EmployeeStatusChangedEvent event) {
        log.info("员工转正特殊处理: {}", event.getEmployeeId().getValue());
        // TODO: 转正相关业务
    }
    
    private void notifyEmployeeAndManager(ProbationExtendedEvent event) {
        log.debug("通知员工和经理试用期延长");
        // TODO: 通知逻辑
    }
    
    private void updateHRSystem(ProbationExtendedEvent event) {
        log.debug("更新HR系统试用期信息");
        // TODO: 系统更新
    }
    
    private void notifyOldDepartment(EmployeeTransferredEvent event) {
        log.debug("通知原部门: {}", event.getOldDepartmentId().getValue());
        // TODO: 部门通知
    }
    
    private void notifyNewDepartment(EmployeeTransferredEvent event) {
        log.debug("通知新部门: {}", event.getNewDepartmentId().getValue());
        // TODO: 部门通知
    }
    
    private void updateWorkstation(EmployeeTransferredEvent event) {
        log.debug("更新工位信息");
        // TODO: 工位管理
    }
    
    private void transferPermissions(EmployeeTransferredEvent event) {
        log.debug("转移权限");
        // TODO: 权限转移
    }
    
    private void sendCongratulations(EmployeePromotedEvent event) {
        log.debug("发送晋升祝贺");
        // TODO: 发送通知
    }
    
    private void updatePositionLevel(EmployeePromotedEvent event) {
        log.debug("更新职级信息");
        // TODO: 职级更新
    }
    
    private void notifyHRForSalaryAdjustment(EmployeePromotedEvent event) {
        log.debug("通知HR调整薪资");
        // TODO: 薪资调整流程
    }
    
    private void initiateResignationProcess(EmployeeResignedEvent event) {
        log.debug("启动离职流程");
        // TODO: 离职流程
    }
    
    private void disableSystemAccount(EmployeeResignedEvent event) {
        log.debug("禁用系统账号: {}", event.getEmployeeNumber());
        // TODO: 账号禁用
    }
    
    private void notifyTeamMembers(EmployeeResignedEvent event) {
        log.debug("通知团队成员");
        // TODO: 团队通知
    }
    
    private void scheduleExitInterview(EmployeeResignedEvent event) {
        log.debug("安排离职面谈");
        // TODO: 面谈安排
    }
    
    private void handleTermination(EmployeeResignedEvent event) {
        log.info("处理辞退相关事务");
        // TODO: 辞退特殊处理
    }
    
    private void handleResignation(EmployeeResignedEvent event) {
        log.info("处理辞职相关事务");
        // TODO: 辞职特殊处理
    }
}

