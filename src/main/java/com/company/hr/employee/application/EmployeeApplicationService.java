package com.company.hr.employee.application;

import com.company.hr.employee.application.dto.*;
import com.company.hr.employee.domain.factory.EmployeeFactory;
import com.company.hr.employee.domain.model.*;
import com.company.hr.employee.domain.repository.EmployeeRepository;
import com.company.hr.organization.domain.model.DepartmentId;
import com.company.hr.organization.domain.model.PositionId;
import com.company.hr.shared.event.DomainEventPublisher;
import com.company.hr.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 员工应用服务
 * 负责协调领域对象完成业务用例
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmployeeApplicationService {
    
    private final EmployeeRepository employeeRepository;
    private final EmployeeFactory employeeFactory;
    private final DomainEventPublisher eventPublisher;
    
    /**
     * 创建员工（入职）
     */
    public EmployeeDTO createEmployee(CreateEmployeeCommand command) {
        log.info("创建员工: {}", command);
        
        // 创建值对象
        PersonalInfo personalInfo = new PersonalInfo(
            command.getFirstName(),
            command.getLastName(),
            command.getIdCardNumber(),
            command.getBirthDate(),
            command.getGender()
        );
        
        ContactInfo contactInfo = new ContactInfo(
            command.getEmail(),
            command.getPhoneNumber(),
            command.getAddress(),
            command.getEmergencyContact(),
            command.getEmergencyPhone()
        );
        
        // 使用工厂创建员工聚合根
        Employee employee = employeeFactory.createEmployee(
            personalInfo,
            contactInfo,
            DepartmentId.of(command.getDepartmentId()),
            PositionId.of(command.getPositionId()),
            LocalDate.now()
        );
        
        // 保存
        employee = employeeRepository.save(employee);
        
        // 发布领域事件
        eventPublisher.publishAll(employee.getDomainEvents());
        employee.clearDomainEvents();
        
        log.info("员工创建成功: {}", employee.getId());
        return EmployeeDTO.fromDomain(employee);
    }
    
    /**
     * 创建特殊人才（无试用期）
     */
    public EmployeeDTO createSpecialTalentEmployee(CreateEmployeeCommand command, String reason) {
        log.info("创建特殊人才员工: {}", command);
        
        PersonalInfo personalInfo = new PersonalInfo(
            command.getFirstName(),
            command.getLastName(),
            command.getIdCardNumber(),
            command.getBirthDate(),
            command.getGender()
        );
        
        ContactInfo contactInfo = new ContactInfo(
            command.getEmail(),
            command.getPhoneNumber(),
            command.getAddress(),
            command.getEmergencyContact(),
            command.getEmergencyPhone()
        );
        
        // 使用工厂创建特殊人才（直接转正）
        Employee employee = employeeFactory.createSpecialTalentEmployee(
            personalInfo,
            contactInfo,
            DepartmentId.of(command.getDepartmentId()),
            PositionId.of(command.getPositionId()),
            LocalDate.now(),
            reason
        );
        
        employee = employeeRepository.save(employee);
        
        // 发布领域事件
        eventPublisher.publishAll(employee.getDomainEvents());
        employee.clearDomainEvents();
        
        log.info("特殊人才员工创建成功: {}", employee.getId());
        return EmployeeDTO.fromDomain(employee);
    }
    
    /**
     * 员工转正
     */
    public void confirmEmployee(String employeeId) {
        log.info("员工转正: {}", employeeId);
        
        Employee employee = employeeRepository.findById(EmployeeId.of(employeeId))
            .orElseThrow(() -> new BusinessException("EMP001", "员工不存在"));
        
        employee.confirmEmployment();
        employeeRepository.save(employee);
        
        // 发布领域事件
        eventPublisher.publishAll(employee.getDomainEvents());
        employee.clearDomainEvents();
        
        log.info("员工转正成功: {}", employeeId);
    }
    
    /**
     * 员工调动
     */
    public void transferEmployee(TransferEmployeeCommand command) {
        log.info("员工调动: {}", command);
        
        Employee employee = employeeRepository.findById(EmployeeId.of(command.getEmployeeId()))
            .orElseThrow(() -> new BusinessException("EMP001", "员工不存在"));
        
        employee.transfer(
            DepartmentId.of(command.getNewDepartmentId()),
            PositionId.of(command.getNewPositionId()),
            command.getReason()
        );
        
        employeeRepository.save(employee);
        
        // 发布领域事件
        eventPublisher.publishAll(employee.getDomainEvents());
        employee.clearDomainEvents();
        
        log.info("员工调动成功: {}", command.getEmployeeId());
    }
    
    /**
     * 员工晋升
     */
    public void promoteEmployee(PromoteEmployeeCommand command) {
        log.info("员工晋升: {}", command);
        
        Employee employee = employeeRepository.findById(EmployeeId.of(command.getEmployeeId()))
            .orElseThrow(() -> new BusinessException("EMP001", "员工不存在"));
        
        employee.promote(
            PositionId.of(command.getNewPositionId()),
            command.getReason()
        );
        
        employeeRepository.save(employee);
        
        // 发布领域事件
        eventPublisher.publishAll(employee.getDomainEvents());
        employee.clearDomainEvents();
        
        log.info("员工晋升成功: {}", command.getEmployeeId());
    }
    
    /**
     * 提前转正
     */
    public void confirmEmploymentEarly(String employeeId, String reason) {
        log.info("提前转正: {}, 原因: {}", employeeId, reason);
        
        Employee employee = employeeRepository.findById(EmployeeId.of(employeeId))
            .orElseThrow(() -> new BusinessException("EMP001", "员工不存在"));
        
        employee.confirmEmploymentEarly(reason);
        employeeRepository.save(employee);
        
        // 发布领域事件
        eventPublisher.publishAll(employee.getDomainEvents());
        employee.clearDomainEvents();
        
        log.info("提前转正成功: {}", employeeId);
    }
    
    /**
     * 延长试用期
     */
    public void extendProbation(ExtendProbationCommand command) {
        log.info("延长试用期: {}", command);
        
        Employee employee = employeeRepository.findById(EmployeeId.of(command.getEmployeeId()))
            .orElseThrow(() -> new BusinessException("EMP001", "员工不存在"));
        
        employee.extendProbation(command.getMonths(), command.getReason());
        employeeRepository.save(employee);
        
        // 发布领域事件
        eventPublisher.publishAll(employee.getDomainEvents());
        employee.clearDomainEvents();
        
        log.info("延长试用期成功: {}", command.getEmployeeId());
    }
    
    /**
     * 更新联系信息
     */
    public void updateContactInfo(UpdateContactCommand command) {
        log.info("更新联系信息: {}", command);
        
        Employee employee = employeeRepository.findById(EmployeeId.of(command.getEmployeeId()))
            .orElseThrow(() -> new BusinessException("EMP001", "员工不存在"));
        
        ContactInfo newContactInfo = new ContactInfo(
            command.getEmail(),
            command.getPhoneNumber(),
            command.getAddress(),
            command.getEmergencyContact(),
            command.getEmergencyPhone()
        );
        
        employee.updateContactInfo(newContactInfo);
        employeeRepository.save(employee);
        
        log.info("联系信息更新成功: {}", command.getEmployeeId());
    }
    
    /**
     * 员工离职/辞退
     */
    public void resignEmployee(ResignEmployeeCommand command) {
        log.info("员工离职: {}", command);
        
        Employee employee = employeeRepository.findById(EmployeeId.of(command.getEmployeeId()))
            .orElseThrow(() -> new BusinessException("EMP001", "员工不存在"));
        
        if ("TERMINATION".equals(command.getResignType())) {
            // 辞退
            employee.terminate(command.getResignDate(), command.getReason());
        } else {
            // 主动辞职
            employee.resign(command.getResignDate(), command.getReason());
        }
        
        employeeRepository.save(employee);
        
        // 发布领域事件
        eventPublisher.publishAll(employee.getDomainEvents());
        employee.clearDomainEvents();
        
        log.info("员工离职成功: {}", command.getEmployeeId());
    }
    
    /**
     * 停职
     */
    public void suspendEmployee(String employeeId, String reason) {
        log.info("员工停职: {}, 原因: {}", employeeId, reason);
        
        Employee employee = employeeRepository.findById(EmployeeId.of(employeeId))
            .orElseThrow(() -> new BusinessException("EMP001", "员工不存在"));
        
        employee.suspend(reason);
        employeeRepository.save(employee);
        
        // 发布领域事件
        eventPublisher.publishAll(employee.getDomainEvents());
        employee.clearDomainEvents();
        
        log.info("员工停职成功: {}", employeeId);
    }
    
    /**
     * 复职
     */
    public void reinstateEmployee(String employeeId) {
        log.info("员工复职: {}", employeeId);
        
        Employee employee = employeeRepository.findById(EmployeeId.of(employeeId))
            .orElseThrow(() -> new BusinessException("EMP001", "员工不存在"));
        
        employee.reinstate();
        employeeRepository.save(employee);
        
        // 发布领域事件
        eventPublisher.publishAll(employee.getDomainEvents());
        employee.clearDomainEvents();
        
        log.info("员工复职成功: {}", employeeId);
    }
    
    /**
     * 根据ID查询员工
     */
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeById(String employeeId) {
        return employeeRepository.findById(EmployeeId.of(employeeId))
            .map(EmployeeDTO::fromDomain)
            .orElseThrow(() -> new BusinessException("EMP001", "员工不存在"));
    }
    
    /**
     * 根据工号查询员工
     */
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeByNumber(String employeeNumber) {
        return employeeRepository.findByEmployeeNumber(employeeNumber)
            .map(EmployeeDTO::fromDomain)
            .orElseThrow(() -> new BusinessException("EMP001", "员工不存在"));
    }
    
    /**
     * 查询部门员工列表
     */
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getEmployeesByDepartment(String departmentId) {
        return employeeRepository.findByDepartmentId(DepartmentId.of(departmentId))
            .stream()
            .map(EmployeeDTO::fromDomain)
            .collect(Collectors.toList());
    }
    
    /**
     * 查询所有员工
     */
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
            .stream()
            .map(EmployeeDTO::fromDomain)
            .collect(Collectors.toList());
    }
}

