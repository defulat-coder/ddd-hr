package com.company.hr.employee.domain.repository;

import com.company.hr.employee.domain.model.Employee;
import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.employee.domain.model.EmployeeStatus;
import com.company.hr.organization.domain.model.DepartmentId;
import com.company.hr.shared.domain.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 员工仓储接口
 */
public interface EmployeeRepository extends Repository<Employee, EmployeeId> {
    
    /**
     * 根据工号查找员工
     */
    Optional<Employee> findByEmployeeNumber(String employeeNumber);
    
    /**
     * 根据部门ID查找员工列表
     */
    List<Employee> findByDepartmentId(DepartmentId departmentId);
    
    /**
     * 根据状态查找员工列表
     */
    List<Employee> findByStatus(EmployeeStatus status);
    
    /**
     * 查找所有员工
     */
    List<Employee> findAll();
    
    /**
     * 生成下一个工号
     */
    String generateEmployeeNumber();
}

