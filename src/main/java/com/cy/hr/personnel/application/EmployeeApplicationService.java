package com.cy.hr.personnel.application;

/**
 * 文件说明：EmployeeApplicationService
 */
import com.cy.hr.organization.domain.DepartmentRepository;
import com.cy.hr.personnel.domain.Employee;
import com.cy.hr.personnel.domain.EmployeeNoGenerator;
import com.cy.hr.personnel.domain.EmployeeRepository;
import com.cy.hr.personnel.domain.EmploymentContract;
import com.cy.hr.shared.domain.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeApplicationService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeNoGenerator employeeNoGenerator;
    private final DepartmentRepository departmentRepository;

    /**
     * 办理员工入职。
     */
    public Employee onboard(OnboardEmployeeCommand command) {
        // 业务规则：身份证号全局唯一
        employeeRepository.findByIdCardNo(command.idCardNo()).ifPresent(e -> {
            throw new DomainException("身份证号已存在");
        });

        // 业务规则：入职员工必须绑定有效部门
        departmentRepository.findById(command.departmentId())
                .orElseThrow(() -> new DomainException("部门不存在"));

        EmploymentContract contract = EmploymentContract.create(
                command.contractType(),
                command.contractStartDate(),
                command.contractEndDate(),
                command.probationMonths(),
                command.contractSignedDate()
        );

        Employee employee = Employee.onboard(
                employeeNoGenerator.next(),
                command.name(),
                command.gender(),
                command.birthDate(),
                command.idCardNo(),
                command.phone(),
                command.departmentId(),
                command.position(),
                command.onboardingDate(),
                contract
        );
        return employeeRepository.save(employee);
    }

    /**
     * 员工转正。
     */
    public Employee becomeRegular(ChangeEmployeeStatusCommand command) {
        Employee employee = employeeRepository.findById(command.employeeId())
                .orElseThrow(() -> new DomainException("员工不存在"));
        employee.becomeRegular();
        return employeeRepository.save(employee);
    }

    /**
     * 员工调岗。
     */
    public Employee transfer(ChangeEmployeeStatusCommand command) {
        Employee employee = employeeRepository.findById(command.employeeId())
                .orElseThrow(() -> new DomainException("员工不存在"));

        // 业务规则：调岗必须指定目标部门且部门有效
        if (command.targetDepartmentId() == null || command.targetDepartmentId().isBlank()) {
            throw new DomainException("目标部门不能为空");
        }
        departmentRepository.findById(command.targetDepartmentId())
                .orElseThrow(() -> new DomainException("目标部门不存在"));

        employee.transfer(command.targetDepartmentId(), command.targetPosition());
        return employeeRepository.save(employee);
    }

    /**
     * 员工离职。
     */
    public Employee resign(ChangeEmployeeStatusCommand command) {
        Employee employee = employeeRepository.findById(command.employeeId())
                .orElseThrow(() -> new DomainException("员工不存在"));
        employee.resign();
        return employeeRepository.save(employee);
    }

    /**
     * 查询员工列表。
     */
    public List<Employee> list() {
        return employeeRepository.findAll();
    }
}
