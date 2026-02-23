package com.company.hr.employee.acl.external;

import com.company.hr.employee.domain.factory.EmployeeFactory;
import com.company.hr.employee.domain.model.Employee;
import com.company.hr.employee.domain.repository.EmployeeRepository;
import com.company.hr.shared.acl.AntiCorruptionService;
import com.company.hr.shared.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 招聘系统防腐服务
 * 封装与外部招聘系统的交互逻辑
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecruitmentSystemService implements AntiCorruptionService {
    
    private final RecruitmentSystemClient recruitmentClient;
    private final RecruitmentSystemAdapter adapter;
    private final EmployeeRepository employeeRepository;
    private final EmployeeFactory employeeFactory;
    private final DomainEventPublisher eventPublisher;
    
    /**
     * 从招聘系统导入候选人为员工
     * 这是防腐层的核心方法，保护内部领域模型不受外部系统影响
     */
    public Employee importCandidateAsEmployee(String candidateId) {
        log.info("从招聘系统导入候选人: {}", candidateId);
        
        try {
            // 1. 从外部系统获取候选人数据
            RecruitmentSystemClient.CandidateData candidateData = 
                recruitmentClient.getCandidateById(candidateId);
            
            // 2. 通过工厂和适配器创建员工
            Employee employee = employeeFactory.createFromRecruitmentSystem(
                candidateData, 
                adapter
            );
            
            // 3. 保存到领域仓储
            employee = employeeRepository.save(employee);
            
            // 4. 发布领域事件
            eventPublisher.publishAll(employee.getDomainEvents());
            employee.clearDomainEvents();
            
            // 5. 更新外部系统状态
            recruitmentClient.updateCandidateStatus(candidateId, "HIRED");
            
            log.info("候选人导入成功，员工ID: {}", employee.getId().getValue());
            return employee;
            
        } catch (Exception e) {
            log.error("导入候选人失败: {}", candidateId, e);
            throw new RuntimeException("导入候选人失败", e);
        }
    }
    
    /**
     * 将员工信息同步回招聘系统
     */
    public void syncEmployeeToRecruitmentSystem(Employee employee) {
        log.info("同步员工信息到招聘系统: {}", employee.getId());
        
        try {
            // TODO: 适配器已改为使用ConvertedData，需要实现Employee到ConvertedData的转换
            // 或者重新设计适配器接口以支持双向转换
            // 暂时注释掉这部分代码
            
            /*
            RecruitmentSystemClient.CandidateData candidateData = 
                adapter.toExternalModel(employee);
            */
            
            // 同步到外部系统（这里简化处理）
            log.warn("该方法需要重新实现");
            
        } catch (Exception e) {
            log.error("同步员工信息失败: {}", employee.getId(), e);
            // 注意：这里可能需要补偿机制或重试逻辑
        }
    }
}

