package com.company.hr.employee.domain.factory;

import com.company.hr.employee.domain.model.*;
import com.company.hr.employee.domain.repository.EmployeeRepository;
import com.company.hr.organization.domain.model.DepartmentId;
import com.company.hr.organization.domain.model.PositionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 员工工厂
 * 负责创建员工聚合根，封装复杂的创建逻辑
 */
@Component
@RequiredArgsConstructor
public class EmployeeFactory {
    
    private final EmployeeRepository employeeRepository;
    
    /**
     * 创建新员工（标准入职流程）
     * 
     * @param personalInfo 个人信息
     * @param contactInfo 联系信息
     * @param departmentId 部门ID
     * @param positionId 职位ID
     * @param hireDate 入职日期
     * @return 新创建的员工
     */
    public Employee createEmployee(
            PersonalInfo personalInfo,
            ContactInfo contactInfo,
            DepartmentId departmentId,
            PositionId positionId,
            LocalDate hireDate) {
        
        // 验证联系信息
        contactInfo.validate();
        
        // 生成员工ID
        EmployeeId employeeId = EmployeeId.generate();
        
        // 生成员工号
        String employeeNumber = employeeRepository.generateEmployeeNumber();
        
        // 创建员工聚合根
        Employee employee = new Employee(
            employeeId,
            employeeNumber,
            personalInfo,
            contactInfo,
            departmentId,
            positionId,
            hireDate
        );
        
        return employee;
    }
    
    /**
     * 创建特殊人才（无试用期，直接转正）
     * 
     * @param personalInfo 个人信息
     * @param contactInfo 联系信息
     * @param departmentId 部门ID
     * @param positionId 职位ID
     * @param hireDate 入职日期
     * @param reason 特殊原因
     * @return 已转正的员工
     */
    public Employee createSpecialTalentEmployee(
            PersonalInfo personalInfo,
            ContactInfo contactInfo,
            DepartmentId departmentId,
            PositionId positionId,
            LocalDate hireDate,
            String reason) {
        
        // 创建标准员工
        Employee employee = createEmployee(
            personalInfo, contactInfo, departmentId, positionId, hireDate
        );
        
        // 立即转正
        employee.confirmEmploymentEarly(reason);
        
        return employee;
    }
    
    /**
     * 从外部招聘系统创建员工
     * 使用防腐层适配器转换数据，工厂负责创建
     * 
     * @param candidateData 外部候选人数据
     * @param adapter 招聘系统适配器
     * @return 新创建的员工
     */
    public Employee createFromRecruitmentSystem(
            com.company.hr.employee.acl.external.RecruitmentSystemClient.CandidateData candidateData,
            com.company.hr.employee.acl.external.RecruitmentSystemAdapter adapter) {
        
        // 1. 通过适配器转换为内部数据格式
        com.company.hr.employee.acl.external.ConvertedData convertedData = 
            adapter.toDomainModel(candidateData);
        
        // 2. 使用工厂创建员工
        Employee employee = createEmployee(
            convertedData.getPersonalInfo(),
            convertedData.getContactInfo(),
            convertedData.getDepartmentId(),
            convertedData.getPositionId(),
            convertedData.getHireDate()
        );
        
        return employee;
    }
    
    /**
     * 重建员工聚合根（从持久化恢复）
     * 
     * @param id 员工ID
     * @param employeeNumber 员工号
     * @param personalInfo 个人信息
     * @param contactInfo 联系信息
     * @param departmentId 部门ID
     * @param positionId 职位ID
     * @param status 员工状态
     * @param hireDate 入职日期
     * @param probationEndDate 试用期结束日期
     * @param resignDate 离职日期
     * @return 重建的员工聚合根
     */
    public Employee reconstitute(
            EmployeeId id,
            String employeeNumber,
            PersonalInfo personalInfo,
            ContactInfo contactInfo,
            DepartmentId departmentId,
            PositionId positionId,
            EmployeeStatus status,
            LocalDate hireDate,
            LocalDate probationEndDate,
            LocalDate resignDate) {
        
        return Employee.reconstitute(
            id, employeeNumber, personalInfo, contactInfo,
            departmentId, positionId, status,
            hireDate, probationEndDate, resignDate
        );
    }
}

