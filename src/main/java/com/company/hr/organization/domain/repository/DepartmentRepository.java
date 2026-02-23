package com.company.hr.organization.domain.repository;

import com.company.hr.organization.domain.model.Department;
import com.company.hr.organization.domain.model.DepartmentId;
import com.company.hr.shared.domain.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 部门仓储接口
 */
public interface DepartmentRepository extends Repository<Department, DepartmentId> {
    
    /**
     * 根据编码查找部门
     */
    Optional<Department> findByCode(String code);
    
    /**
     * 根据父部门ID查找子部门列表
     */
    List<Department> findByParentId(DepartmentId parentId);
    
    /**
     * 查找所有顶级部门
     */
    List<Department> findTopLevelDepartments();
    
    /**
     * 查找所有激活的部门
     */
    List<Department> findActiveDepartments();
    
    /**
     * 查找所有部门
     */
    List<Department> findAll();
}

