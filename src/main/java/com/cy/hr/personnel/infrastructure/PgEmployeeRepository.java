package com.cy.hr.personnel.infrastructure;

/**
 * 文件说明：PgEmployeeRepository
 */
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.hr.personnel.domain.Employee;
import com.cy.hr.personnel.domain.EmployeeRepository;
import com.cy.hr.personnel.domain.EmployeeStatus;
import com.cy.hr.personnel.domain.EmploymentContract;
import com.cy.hr.personnel.infrastructure.mapper.EmployeeMapper;
import com.cy.hr.personnel.infrastructure.po.EmployeePO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PgEmployeeRepository implements EmployeeRepository {

    private final EmployeeMapper mapper;

    @Override
    public Employee save(Employee employee) {
        EmployeePO po = toPo(employee);
        if (mapper.selectById(po.getId()) == null) {
            mapper.insert(po);
        } else {
            mapper.updateById(po);
        }
        return employee;
    }

    @Override
    public Optional<Employee> findById(String id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public Optional<Employee> findByIdCardNo(String idCardNo) {
        return Optional.ofNullable(mapper.selectOne(new LambdaQueryWrapper<EmployeePO>()
                        .eq(EmployeePO::getIdCardNo, idCardNo)
                        .last("LIMIT 1")))
                .map(this::toDomain);
    }

    @Override
    public List<Employee> findAll() {
        return mapper.selectList(new LambdaQueryWrapper<EmployeePO>()
                        .orderByDesc(EmployeePO::getOnboardingDate)
                        .orderByAsc(EmployeePO::getEmployeeNo))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private EmployeePO toPo(Employee employee) {
        EmployeePO po = new EmployeePO();
        po.setId(employee.getId());
        po.setEmployeeNo(employee.getEmployeeNo());
        po.setName(employee.getName());
        po.setGender(employee.getGender());
        po.setBirthDate(employee.getBirthDate());
        po.setIdCardNo(employee.getIdCardNo());
        po.setPhone(employee.getPhone());
        po.setDepartmentId(employee.getDepartmentId());
        po.setPosition(employee.getPosition());
        po.setOnboardingDate(employee.getOnboardingDate());
        po.setStatus(employee.getStatus().name());
        po.setContractType(employee.getContract().getType());
        po.setContractStartDate(employee.getContract().getStartDate());
        po.setContractEndDate(employee.getContract().getEndDate());
        po.setProbationMonths(employee.getContract().getProbationMonths());
        po.setContractSignedDate(employee.getContract().getSignedDate());
        return po;
    }

    private Employee toDomain(EmployeePO po) {
        EmploymentContract contract = EmploymentContract.create(
                po.getContractType(),
                po.getContractStartDate(),
                po.getContractEndDate(),
                po.getProbationMonths(),
                po.getContractSignedDate());
        return Employee.restore(
                po.getId(),
                po.getEmployeeNo(),
                po.getName(),
                po.getGender(),
                po.getBirthDate(),
                po.getIdCardNo(),
                po.getPhone(),
                po.getDepartmentId(),
                po.getPosition(),
                po.getOnboardingDate(),
                contract,
                EmployeeStatus.valueOf(po.getStatus()));
    }
}
