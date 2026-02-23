package com.company.hr.employee.domain.model;

import com.company.hr.organization.domain.model.DepartmentId;
import com.company.hr.organization.domain.model.PositionId;
import com.company.hr.shared.domain.AggregateRoot;
import com.company.hr.shared.exception.DomainException;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 员工聚合根
 * 员工中心的核心聚合
 */
@Getter
public class Employee extends AggregateRoot<EmployeeId> {
    
    private String employeeNumber;
    private PersonalInfo personalInfo;
    private ContactInfo contactInfo;
    private DepartmentId departmentId;
    private PositionId positionId;
    private EmployeeStatus status;
    private LocalDate hireDate;
    private LocalDate probationEndDate;
    private LocalDate resignDate;
    
    /**
     * 创建新员工（入职）
     */
    public Employee(EmployeeId id, String employeeNumber, PersonalInfo personalInfo, 
                   ContactInfo contactInfo, DepartmentId departmentId, 
                   PositionId positionId, LocalDate hireDate) {
        super(id);
        this.employeeNumber = employeeNumber;
        this.personalInfo = personalInfo;
        this.contactInfo = contactInfo;
        this.departmentId = departmentId;
        this.positionId = positionId;
        this.hireDate = hireDate;
        this.status = EmployeeStatus.PROBATION;
        this.probationEndDate = hireDate.plusMonths(3); // 默认3个月试用期
        
        // 验证联系信息
        contactInfo.validate();
        
        // 发布员工入职事件
        registerEvent(new EmployeeHiredEvent(id, employeeNumber, hireDate));
    }
    
    /**
     * 重建员工聚合根（用于从持久化恢复）
     */
    public static Employee reconstitute(
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
        
        Employee employee = new Employee(
            id, employeeNumber, personalInfo, contactInfo, 
            departmentId, positionId, hireDate
        );
        
        // 清除构造函数中产生的事件（重建时不应该发布事件）
        employee.clearDomainEvents();
        
        // 恢复状态
        employee.status = status;
        employee.probationEndDate = probationEndDate;
        employee.resignDate = resignDate;
        
        return employee;
    }
    
    /**
     * 转正
     */
    public void confirmEmployment() {
        if (status != EmployeeStatus.PROBATION) {
            throw new DomainException("只有试用期员工才能转正");
        }
        if (LocalDate.now().isBefore(probationEndDate)) {
            throw new DomainException("试用期未结束，不能转正");
        }
        this.status = EmployeeStatus.ACTIVE;
        registerEvent(new EmployeeStatusChangedEvent(getId(), EmployeeStatus.PROBATION, EmployeeStatus.ACTIVE));
    }
    
    /**
     * 提前转正
     */
    public void confirmEmploymentEarly(String reason) {
        if (status != EmployeeStatus.PROBATION) {
            throw new DomainException("只有试用期员工才能转正");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new DomainException("提前转正必须提供理由");
        }
        this.status = EmployeeStatus.ACTIVE;
        registerEvent(new EmployeeStatusChangedEvent(getId(), EmployeeStatus.PROBATION, EmployeeStatus.ACTIVE));
    }
    
    /**
     * 延长试用期
     */
    public void extendProbation(int months, String reason) {
        if (status != EmployeeStatus.PROBATION) {
            throw new DomainException("只有试用期员工才能延长试用期");
        }
        if (months <= 0 || months > 6) {
            throw new DomainException("试用期延长时间必须在1-6个月之间");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new DomainException("延长试用期必须提供理由");
        }
        
        LocalDate oldProbationEndDate = this.probationEndDate;
        this.probationEndDate = this.probationEndDate.plusMonths(months);
        
        // 检查总试用期不超过6个月
        long totalMonths = java.time.temporal.ChronoUnit.MONTHS.between(hireDate, probationEndDate);
        if (totalMonths > 6) {
            throw new DomainException("试用期总长度不能超过6个月");
        }
        
        registerEvent(new ProbationExtendedEvent(getId(), oldProbationEndDate, probationEndDate, reason));
    }
    
    /**
     * 变更状态
     */
    public void changeStatus(EmployeeStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new DomainException(String.format("不能从%s状态转换到%s状态", 
                status.getDescription(), newStatus.getDescription()));
        }
        EmployeeStatus oldStatus = this.status;
        this.status = newStatus;
        registerEvent(new EmployeeStatusChangedEvent(getId(), oldStatus, newStatus));
    }
    
    /**
     * 离职（主动辞职）
     */
    public void resign(LocalDate resignDate, String reason) {
        if (this.status == EmployeeStatus.RESIGNED) {
            throw new DomainException("员工已经离职");
        }
        if (resignDate.isBefore(LocalDate.now())) {
            throw new DomainException("离职日期不能早于当前日期");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new DomainException("离职必须提供原因");
        }
        
        EmployeeStatus oldStatus = this.status;
        this.status = EmployeeStatus.RESIGNED;
        this.resignDate = resignDate;
        registerEvent(new EmployeeResignedEvent(getId(), employeeNumber, resignDate, reason, "RESIGNATION"));
    }
    
    /**
     * 辞退
     */
    public void terminate(LocalDate terminateDate, String reason) {
        if (this.status == EmployeeStatus.RESIGNED) {
            throw new DomainException("员工已经离职");
        }
        if (terminateDate.isBefore(LocalDate.now())) {
            throw new DomainException("辞退日期不能早于当前日期");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new DomainException("辞退必须提供详细原因");
        }
        
        this.status = EmployeeStatus.RESIGNED;
        this.resignDate = terminateDate;
        registerEvent(new EmployeeResignedEvent(getId(), employeeNumber, terminateDate, reason, "TERMINATION"));
    }
    
    /**
     * 获取在职天数
     */
    public long getWorkingDays() {
        if (resignDate != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(hireDate, resignDate);
        }
        return java.time.temporal.ChronoUnit.DAYS.between(hireDate, LocalDate.now());
    }
    
    /**
     * 是否在试用期
     */
    public boolean isProbation() {
        return status == EmployeeStatus.PROBATION;
    }
    
    /**
     * 调动（部门和职位）
     */
    public void transfer(DepartmentId newDepartmentId, PositionId newPositionId, String reason) {
        if (this.status != EmployeeStatus.ACTIVE) {
            throw new DomainException("只有在职员工才能调动");
        }
        if (newDepartmentId.equals(this.departmentId) && newPositionId.equals(this.positionId)) {
            throw new DomainException("新部门和职位与当前相同，无需调动");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new DomainException("调动必须提供原因");
        }
        
        DepartmentId oldDepartmentId = this.departmentId;
        PositionId oldPositionId = this.positionId;
        this.departmentId = newDepartmentId;
        this.positionId = newPositionId;
        registerEvent(new EmployeeTransferredEvent(getId(), oldDepartmentId, newDepartmentId, 
            oldPositionId, newPositionId, reason));
    }
    
    /**
     * 晋升（仅变更职位）
     */
    public void promote(PositionId newPositionId, String reason) {
        if (this.status != EmployeeStatus.ACTIVE) {
            throw new DomainException("只有在职员工才能晋升");
        }
        if (newPositionId.equals(this.positionId)) {
            throw new DomainException("新职位与当前相同，无需晋升");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new DomainException("晋升必须提供原因");
        }
        
        PositionId oldPositionId = this.positionId;
        this.positionId = newPositionId;
        registerEvent(new EmployeePromotedEvent(getId(), oldPositionId, newPositionId, reason));
    }
    
    /**
     * 停职
     */
    public void suspend(String reason) {
        if (this.status != EmployeeStatus.ACTIVE) {
            throw new DomainException("只有在职员工才能停职");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new DomainException("停职必须提供原因");
        }
        changeStatus(EmployeeStatus.SUSPENDED);
    }
    
    /**
     * 复职
     */
    public void reinstate() {
        if (this.status != EmployeeStatus.SUSPENDED) {
            throw new DomainException("只有停职员工才能复职");
        }
        changeStatus(EmployeeStatus.ACTIVE);
    }
    
    /**
     * 更新联系信息
     */
    public void updateContactInfo(ContactInfo newContactInfo) {
        newContactInfo.validate();
        this.contactInfo = newContactInfo;
    }
    
    /**
     * 是否在职
     */
    public boolean isActive() {
        return status == EmployeeStatus.ACTIVE;
    }
}

