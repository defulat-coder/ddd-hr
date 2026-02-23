package com.company.hr.employee.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    
    private final EmployeeMapper employeeMapper;
    private final EmployeeFactory employeeFactory;
    
    @Override
    public Employee save(Employee aggregate) {
        EmployeeEntity entity = EmployeeEntity.fromDomain(aggregate);
        EmployeeEntity existing = employeeMapper.selectById(entity.getId());
        if (existing == null) {
            employeeMapper.insert(entity);
        } else {
            employeeMapper.updateById(entity);
        }
        EmployeeEntity saved = employeeMapper.selectById(entity.getId());
        return saved.toDomain(employeeFactory);
    }
    
    @Override
    public Optional<Employee> findById(EmployeeId id) {
        EmployeeEntity entity = employeeMapper.selectById(id.getValue());
        return Optional.ofNullable(entity).map(e -> e.toDomain(employeeFactory));
    }
    
    @Override
    public void delete(Employee aggregate) {
        employeeMapper.deleteById(aggregate.getId().getValue());
    }
    
    @Override
    public void deleteById(EmployeeId id) {
        employeeMapper.deleteById(id.getValue());
    }
    
    @Override
    public boolean existsById(EmployeeId id) {
        return employeeMapper.selectById(id.getValue()) != null;
    }
    
    @Override
    public Optional<Employee> findByEmployeeNumber(String employeeNumber) {
        EmployeeEntity entity = employeeMapper.selectOne(
            new LambdaQueryWrapper<EmployeeEntity>()
                .eq(EmployeeEntity::getEmployeeNumber, employeeNumber)
                .last("LIMIT 1")
        );
        return Optional.ofNullable(entity).map(e -> e.toDomain(employeeFactory));
    }
    
    @Override
    public List<Employee> findByDepartmentId(DepartmentId departmentId) {
        return employeeMapper.selectList(
                new LambdaQueryWrapper<EmployeeEntity>()
                    .eq(EmployeeEntity::getDepartmentId, departmentId.getValue())
            )
            .stream()
            .map(entity -> entity.toDomain(employeeFactory))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Employee> findByStatus(EmployeeStatus status) {
        return employeeMapper.selectList(
                new LambdaQueryWrapper<EmployeeEntity>()
                    .eq(EmployeeEntity::getStatus, status)
            )
            .stream()
            .map(entity -> entity.toDomain(employeeFactory))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Employee> findAll() {
        return employeeMapper.selectList(null)
            .stream()
            .map(entity -> entity.toDomain(employeeFactory))
            .collect(Collectors.toList());
    }
    
    @Override
    public String generateEmployeeNumber() {
        // 简单实现：使用年份+序号
        String year = String.valueOf(LocalDate.now().getYear());
        long count = employeeMapper.selectCount(null) + 1;
        return year + String.format("%06d", count);
    }
}
