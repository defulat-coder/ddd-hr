package com.cy.hr.payroll.infrastructure;

/**
 * 文件说明：PgSalaryProfileRepository
 */
import com.cy.hr.payroll.domain.SalaryProfile;
import com.cy.hr.payroll.domain.SalaryProfileRepository;
import com.cy.hr.payroll.infrastructure.mapper.SalaryProfileMapper;
import com.cy.hr.payroll.infrastructure.po.SalaryProfilePO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PgSalaryProfileRepository implements SalaryProfileRepository {

    private final SalaryProfileMapper mapper;

    @Override
    public SalaryProfile save(SalaryProfile salaryProfile) {
        SalaryProfilePO po = new SalaryProfilePO();
        po.setEmployeeId(salaryProfile.getEmployeeId());
        po.setBaseSalary(salaryProfile.getBaseSalary());
        po.setPositionSalary(salaryProfile.getPositionSalary());
        po.setPerformanceSalary(salaryProfile.getPerformanceSalary());
        po.setAllowance(salaryProfile.getAllowance());
        if (mapper.selectById(po.getEmployeeId()) == null) {
            mapper.insert(po);
        } else {
            mapper.updateById(po);
        }
        return salaryProfile;
    }

    @Override
    public Optional<SalaryProfile> findByEmployeeId(String employeeId) {
        SalaryProfilePO po = mapper.selectById(employeeId);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(new SalaryProfile(
                po.getEmployeeId(),
                po.getBaseSalary(),
                po.getPositionSalary(),
                po.getPerformanceSalary(),
                po.getAllowance()));
    }
}
