package com.company.hr.employee.infrastructure.persistence;

import com.company.hr.employee.domain.factory.EmployeeFactory;
import com.company.hr.employee.domain.model.Employee;
import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.employee.domain.model.EmployeeStatus;
import com.company.hr.employee.domain.repository.EmployeeRepository;
import com.company.hr.organization.domain.model.DepartmentId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 员工仓储实现
 */
@Component
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {
    
    private final EmployeeJpaRepository jpaRepository;
    private final EmployeeFactory employeeFactory;
    
    @Override
    public Employee save(Employee aggregate) {
        EmployeeJpaEntity entity = EmployeeJpaEntity.fromDomain(aggregate);
        EmployeeJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain(employeeFactory);
    }
    
    @Override
    public Optional<Employee> findById(EmployeeId id) {
        return jpaRepository.findById(id.getValue())
            .map(entity -> entity.toDomain(employeeFactory));
    }
    
    @Override
    public void delete(Employee aggregate) {
        jpaRepository.deleteById(aggregate.getId().getValue());
    }
    
    @Override
    public void deleteById(EmployeeId id) {
        jpaRepository.deleteById(id.getValue());
    }
    
    @Override
    public boolean existsById(EmployeeId id) {
        return jpaRepository.existsById(id.getValue());
    }
    
    @Override
    public Optional<Employee> findByEmployeeNumber(String employeeNumber) {
        return jpaRepository.findByEmployeeNumber(employeeNumber)
            .map(entity -> entity.toDomain(employeeFactory));
    }
    
    @Override
    public List<Employee> findByDepartmentId(DepartmentId departmentId) {
        return jpaRepository.findByDepartmentId(departmentId.getValue())
            .stream()
            .map(entity -> entity.toDomain(employeeFactory))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Employee> findByStatus(EmployeeStatus status) {
        return jpaRepository.findByStatus(status)
            .stream()
            .map(entity -> entity.toDomain(employeeFactory))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Employee> findAll() {
        return jpaRepository.findAll()
            .stream()
            .map(entity -> entity.toDomain(employeeFactory))
            .collect(Collectors.toList());
    }
    
    @Override
    public String generateEmployeeNumber() {
        // 简单实现：使用年份+序号
        String year = String.valueOf(LocalDate.now().getYear());
        long count = jpaRepository.count() + 1;
        return year + String.format("%06d", count);
    }
}

