package com.company.hr.benefit.domain.repository;

import com.company.hr.benefit.domain.model.Benefit;
import com.company.hr.benefit.domain.model.BenefitId;
import com.company.hr.benefit.domain.model.BenefitType;
import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.domain.Repository;

import java.util.List;

/**
 * 福利仓储接口
 */
public interface BenefitRepository extends Repository<Benefit, BenefitId> {
    
    /**
     * 根据类型查找福利列表
     */
    List<Benefit> findByType(BenefitType type);
    
    /**
     * 查找所有激活的福利
     */
    List<Benefit> findActiveBenefits();
    
    /**
     * 根据员工ID查找其参加的福利列表
     */
    List<Benefit> findByEmployeeId(EmployeeId employeeId);
    
    /**
     * 查找所有福利
     */
    List<Benefit> findAll();
}

