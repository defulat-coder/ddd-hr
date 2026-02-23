package com.company.hr.organization.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.hr.organization.domain.model.Department;
import com.company.hr.organization.domain.model.DepartmentId;
import com.company.hr.organization.domain.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements DepartmentRepository {

    private final DepartmentMapper departmentMapper;

    @Override
    public Department save(Department aggregate) {
        DepartmentEntity entity = DepartmentEntity.fromDomain(aggregate);
        if (departmentMapper.selectById(entity.getId()) == null) {
            departmentMapper.insert(entity);
        } else {
            departmentMapper.updateById(entity);
        }
        return departmentMapper.selectById(entity.getId()).toDomain();
    }

    @Override
    public Optional<Department> findById(DepartmentId id) {
        DepartmentEntity entity = departmentMapper.selectById(id.getValue());
        return Optional.ofNullable(entity).map(DepartmentEntity::toDomain);
    }

    @Override
    public void delete(Department aggregate) {
        departmentMapper.deleteById(aggregate.getId().getValue());
    }

    @Override
    public void deleteById(DepartmentId id) {
        departmentMapper.deleteById(id.getValue());
    }

    @Override
    public boolean existsById(DepartmentId id) {
        return departmentMapper.selectById(id.getValue()) != null;
    }

    @Override
    public Optional<Department> findByCode(String code) {
        DepartmentEntity entity = departmentMapper.selectOne(
            new LambdaQueryWrapper<DepartmentEntity>().eq(DepartmentEntity::getCode, code).last("LIMIT 1")
        );
        return Optional.ofNullable(entity).map(DepartmentEntity::toDomain);
    }

    @Override
    public List<Department> findByParentId(DepartmentId parentId) {
        return departmentMapper.selectList(
                new LambdaQueryWrapper<DepartmentEntity>().eq(DepartmentEntity::getParentId, parentId.getValue())
            )
            .stream().map(DepartmentEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Department> findTopLevelDepartments() {
        return departmentMapper.selectList(
                new LambdaQueryWrapper<DepartmentEntity>().isNull(DepartmentEntity::getParentId)
            )
            .stream().map(DepartmentEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Department> findActiveDepartments() {
        return departmentMapper.selectList(
                new LambdaQueryWrapper<DepartmentEntity>().eq(DepartmentEntity::getActive, true)
            )
            .stream().map(DepartmentEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Department> findAll() {
        return departmentMapper.selectList(null).stream().map(DepartmentEntity::toDomain).collect(Collectors.toList());
    }
}
