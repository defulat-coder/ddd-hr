package com.company.hr.culture.application.event;

import com.company.hr.employee.domain.model.EmployeeHiredEvent;
import com.company.hr.employee.domain.model.EmployeePromotedEvent;
import com.company.hr.employee.domain.model.EmployeeResignedEvent;
import com.company.hr.shared.event.DomainEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 企业文化上下文的事件处理器
 * 监听员工上下文的事件，实现文化活动的响应
 */
@Component
@DomainEventHandler(
    value = "CultureEventHandler",
    description = "处理员工事件对企业文化活动的影响"
)
@Slf4j
public class CultureEventHandler {
    
    /**
     * 处理员工入职事件
     * 邀请新员工参加入职培训和文化活动
     */
    @EventListener
    @Async
    public void handleEmployeeHired(EmployeeHiredEvent event) {
        log.info("【文化上下文】收到员工入职事件: 员工号={}", event.getEmployeeNumber());
        
        // 自动报名入职培训
        enrollOnboardingTraining(event.getEmployeeId().getValue());
        
        // 发送文化活动邀请
        sendCultureActivityInvitation(event.getEmployeeId().getValue());
        
        log.info("【文化上下文】员工入职事件处理完成");
    }
    
    /**
     * 处理员工晋升事件
     * 组织庆祝活动
     */
    @EventListener
    @Async
    public void handleEmployeePromoted(EmployeePromotedEvent event) {
        log.info("【文化上下文】收到员工晋升事件: 员工ID={}", 
            event.getEmployeeId().getValue());
        
        // 发送晋升祝贺
        sendPromotionCongratulations(event.getEmployeeId().getValue());
        
        // 记录到公司荣誉榜
        addToHonorBoard(event.getEmployeeId().getValue(), "晋升");
        
        log.info("【文化上下文】员工晋升事件处理完成");
    }
    
    /**
     * 处理员工离职事件
     * 取消活动报名，安排欢送活动
     */
    @EventListener
    @Async
    public void handleEmployeeResigned(EmployeeResignedEvent event) {
        log.info("【文化上下文】收到员工离职事件: 员工号={}", event.getEmployeeNumber());
        
        // 取消所有未来活动的报名
        cancelFutureActivityRegistrations(event.getEmployeeId().getValue());
        
        // 如果是正常离职，安排欢送活动
        if ("RESIGNATION".equals(event.getResignType())) {
            scheduleFarewellEvent(event.getEmployeeId().getValue());
        }
        
        log.info("【文化上下文】员工离职事件处理完成");
    }
    
    // ========== 私有方法 ==========
    
    private void enrollOnboardingTraining(String employeeId) {
        log.debug("自动报名入职培训: {}", employeeId);
        // TODO: 查找入职培训活动并自动报名
    }
    
    private void sendCultureActivityInvitation(String employeeId) {
        log.debug("发送文化活动邀请: {}", employeeId);
        // TODO: 发送近期文化活动信息
    }
    
    private void sendPromotionCongratulations(String employeeId) {
        log.debug("发送晋升祝贺: {}", employeeId);
        // TODO: 全公司发送晋升通知
    }
    
    private void addToHonorBoard(String employeeId, String reason) {
        log.debug("添加到荣誉榜: 员工={}, 原因={}", employeeId, reason);
        // TODO: 在公司荣誉榜上添加记录
    }
    
    private void cancelFutureActivityRegistrations(String employeeId) {
        log.debug("取消未来活动报名: {}", employeeId);
        // TODO: 查询并取消该员工的所有未来活动报名
    }
    
    private void scheduleFarewellEvent(String employeeId) {
        log.debug("安排欢送活动: {}", employeeId);
        // TODO: 创建欢送会活动
    }
}

