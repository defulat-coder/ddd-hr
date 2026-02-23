package com.company.hr.performance.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.performance.domain.model.Goal;
import com.company.hr.performance.domain.model.GoalId;
import com.company.hr.performance.domain.model.GoalStatus;
import com.company.hr.performance.domain.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GoalRepositoryImpl implements GoalRepository {

    private final GoalMapper goalMapper;

    @Override
    public Goal save(Goal aggregate) {
        GoalEntity entity = GoalEntity.fromDomain(aggregate);
        if (goalMapper.selectById(entity.getId()) == null) {
            goalMapper.insert(entity);
        } else {
            goalMapper.updateById(entity);
        }
        return goalMapper.selectById(entity.getId()).toDomain();
    }

    @Override
    public Optional<Goal> findById(GoalId id) {
        GoalEntity entity = goalMapper.selectById(id.getValue());
        return Optional.ofNullable(entity).map(GoalEntity::toDomain);
    }

    @Override
    public void delete(Goal aggregate) {
        goalMapper.deleteById(aggregate.getId().getValue());
    }

    @Override
    public void deleteById(GoalId id) {
        goalMapper.deleteById(id.getValue());
    }

    @Override
    public boolean existsById(GoalId id) {
        return goalMapper.selectById(id.getValue()) != null;
    }

    @Override
    public List<Goal> findByEmployeeId(EmployeeId employeeId) {
        return goalMapper.selectList(
                new LambdaQueryWrapper<GoalEntity>().eq(GoalEntity::getEmployeeId, employeeId.getValue())
            )
            .stream().map(GoalEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Goal> findByEmployeeIdAndStatus(EmployeeId employeeId, GoalStatus status) {
        return goalMapper.selectList(
                new LambdaQueryWrapper<GoalEntity>()
                    .eq(GoalEntity::getEmployeeId, employeeId.getValue())
                    .eq(GoalEntity::getStatus, status.name())
            )
            .stream().map(GoalEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Goal> findActiveGoalsByEmployeeId(EmployeeId employeeId) {
        return findByEmployeeIdAndStatus(employeeId, GoalStatus.ACTIVE);
    }
}
