package com.company.hr.employee.infrastructure.persistence;

import com.company.hr.employee.domain.model.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 员工JPA Repository
 */
@Repository
public interface EmployeeJpaRepository extends JpaRepository<EmployeeJpaEntity, String> {
    
    Optional<EmployeeJpaEntity> findByEmployeeNumber(String employeeNumber);
    
    List<EmployeeJpaEntity> findByDepartmentId(String departmentId);
    
    List<EmployeeJpaEntity> findByStatus(EmployeeStatus status);
}

