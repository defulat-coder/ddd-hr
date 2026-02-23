package com.cy.hr.organization.infrastructure;

/**
 * 文件说明：PgDepartmentRepository
 */
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.hr.organization.domain.Department;
import com.cy.hr.organization.domain.DepartmentRepository;
import com.cy.hr.organization.infrastructure.mapper.DepartmentMapper;
import com.cy.hr.organization.infrastructure.po.DepartmentPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PgDepartmentRepository implements DepartmentRepository {

    private final DepartmentMapper mapper;

    @Override
    public Department save(Department department) {
        DepartmentPO po = toPo(department);
        if (mapper.selectById(po.getId()) == null) {
            mapper.insert(po);
        } else {
            mapper.updateById(po);
        }
        return department;
    }

    @Override
    public Optional<Department> findById(String id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<Department> findAll() {
        return mapper.selectList(new LambdaQueryWrapper<DepartmentPO>()
                        .orderByAsc(DepartmentPO::getLevel)
                        .orderByAsc(DepartmentPO::getName))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private DepartmentPO toPo(Department department) {
        DepartmentPO po = new DepartmentPO();
        po.setId(department.getId());
        po.setName(department.getName());
        po.setParentDepartmentId(department.getParentDepartmentId());
        po.setLevel(department.getLevel());
        po.setLeader(department.getLeader());
        po.setStaffingQuota(department.getStaffingQuota());
        po.setEffectiveDate(department.getEffectiveDate());
        return po;
    }

    private Department toDomain(DepartmentPO po) {
        return Department.restore(
                po.getId(),
                po.getName(),
                po.getParentDepartmentId(),
                po.getLevel(),
                po.getLeader(),
                po.getStaffingQuota(),
                po.getEffectiveDate());
    }
}
