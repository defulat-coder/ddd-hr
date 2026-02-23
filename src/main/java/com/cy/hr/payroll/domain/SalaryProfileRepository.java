package com.cy.hr.payroll.domain;

/**
 * 文件说明：SalaryProfileRepository
 */
import java.util.Optional;

public interface SalaryProfileRepository {

    SalaryProfile save(SalaryProfile salaryProfile);

    Optional<SalaryProfile> findByEmployeeId(String employeeId);
}
