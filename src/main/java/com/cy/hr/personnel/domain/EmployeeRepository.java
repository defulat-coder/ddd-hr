package com.cy.hr.personnel.domain;

/**
 * 文件说明：EmployeeRepository
 */
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {

    Employee save(Employee employee);

    Optional<Employee> findById(String id);

    Optional<Employee> findByIdCardNo(String idCardNo);

    List<Employee> findAll();
}
