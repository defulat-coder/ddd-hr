package com.company.hr.benefit.domain.model;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.domain.Entity;
import com.company.hr.shared.exception.DomainException;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 福利参加实体
 */
@Getter
public class BenefitEnrollment extends Entity<EnrollmentId> {
    
    private EmployeeId employeeId;
    private LocalDate enrollmentDate;
    private LocalDate effectiveDate;
    private LocalDate expirationDate;
    private EnrollmentStatus status;
    private String notes;
    
    public BenefitEnrollment(EnrollmentId id, EmployeeId employeeId, 
                            LocalDate enrollmentDate, LocalDate effectiveDate, 
                            LocalDate expirationDate) {
        super(id);
        this.employeeId = employeeId;
        this.enrollmentDate = enrollmentDate;
        this.effectiveDate = effectiveDate;
        this.expirationDate = expirationDate;
        this.status = EnrollmentStatus.PENDING;
        
        validateDates();
    }
    
    private void validateDates() {
        if (effectiveDate.isBefore(enrollmentDate)) {
            throw new DomainException("生效日期不能早于申请日期");
        }
        if (expirationDate != null && expirationDate.isBefore(effectiveDate)) {
            throw new DomainException("过期日期不能早于生效日期");
        }
    }
    
    /**
     * 批准参加
     */
    public void approve() {
        if (status != EnrollmentStatus.PENDING) {
            throw new DomainException("只有待审批状态的福利才能批准");
        }
        this.status = EnrollmentStatus.APPROVED;
    }
    
    /**
     * 激活
     */
    public void activate() {
        if (status != EnrollmentStatus.APPROVED) {
            throw new DomainException("只有已批准状态的福利才能激活");
        }
        if (LocalDate.now().isBefore(effectiveDate)) {
            throw new DomainException("还未到生效日期");
        }
        this.status = EnrollmentStatus.ACTIVE;
    }
    
    /**
     * 暂停
     */
    public void suspend(String reason) {
        if (status != EnrollmentStatus.ACTIVE) {
            throw new DomainException("只有生效中的福利才能暂停");
        }
        this.status = EnrollmentStatus.SUSPENDED;
        this.notes = reason;
    }
    
    /**
     * 取消
     */
    public void cancel(String reason) {
        if (status == EnrollmentStatus.CANCELLED || status == EnrollmentStatus.EXPIRED) {
            throw new DomainException("福利已经结束");
        }
        this.status = EnrollmentStatus.CANCELLED;
        this.notes = reason;
    }
    
    /**
     * 检查是否过期
     */
    public void checkExpiration() {
        if (expirationDate != null && LocalDate.now().isAfter(expirationDate)) {
            if (status == EnrollmentStatus.ACTIVE) {
                this.status = EnrollmentStatus.EXPIRED;
            }
        }
    }
    
    /**
     * 是否有效
     */
    public boolean isActive() {
        return status == EnrollmentStatus.ACTIVE;
    }
}

