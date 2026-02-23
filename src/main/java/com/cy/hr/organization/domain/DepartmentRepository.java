package com.cy.hr.organization.domain;

/**
 * 文件说明：DepartmentRepository
 */
import java.util.List;
import java.util.Optional;

public interface DepartmentRepository {

    Department save(Department department);

    Optional<Department> findById(String id);

    List<Department> findAll();
}
