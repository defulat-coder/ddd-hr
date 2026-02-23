package com.company.hr.culture.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.hr.culture.domain.model.*;
import com.company.hr.culture.domain.repository.CultureActivityRepository;
import com.company.hr.employee.domain.model.EmployeeId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CultureActivityRepositoryImpl implements CultureActivityRepository {

    private final CultureActivityMapper activityMapper;

    @Override
    public CultureActivity save(CultureActivity aggregate) {
        CultureActivityEntity entity = CultureActivityEntity.fromDomain(aggregate);
        if (activityMapper.selectById(entity.getId()) == null) {
            activityMapper.insert(entity);
        } else {
            activityMapper.updateById(entity);
        }
        return activityMapper.selectById(entity.getId()).toDomain();
    }

    @Override
    public Optional<CultureActivity> findById(ActivityId id) {
        CultureActivityEntity entity = activityMapper.selectById(id.getValue());
        return Optional.ofNullable(entity).map(CultureActivityEntity::toDomain);
    }

    @Override
    public void delete(CultureActivity aggregate) {
        activityMapper.deleteById(aggregate.getId().getValue());
    }

    @Override
    public void deleteById(ActivityId id) {
        activityMapper.deleteById(id.getValue());
    }

    @Override
    public boolean existsById(ActivityId id) {
        return activityMapper.selectById(id.getValue()) != null;
    }

    @Override
    public List<CultureActivity> findByType(ActivityType type) {
        return activityMapper.selectList(new LambdaQueryWrapper<CultureActivityEntity>().eq(CultureActivityEntity::getType, type.name()))
            .stream().map(CultureActivityEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<CultureActivity> findByStatus(ActivityStatus status) {
        return activityMapper.selectList(new LambdaQueryWrapper<CultureActivityEntity>().eq(CultureActivityEntity::getStatus, status.name()))
            .stream().map(CultureActivityEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<CultureActivity> findByOrganizerId(EmployeeId organizerId) {
        return activityMapper.selectList(new LambdaQueryWrapper<CultureActivityEntity>().eq(CultureActivityEntity::getOrganizerId, organizerId.getValue()))
            .stream().map(CultureActivityEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<CultureActivity> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return activityMapper.selectList(new LambdaQueryWrapper<CultureActivityEntity>()
                .ge(CultureActivityEntity::getStartTime, startDate)
                .le(CultureActivityEntity::getEndTime, endDate)
            )
            .stream().map(CultureActivityEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<CultureActivity> findByParticipantId(EmployeeId employeeId) {
        return activityMapper.selectList(null).stream()
            .map(CultureActivityEntity::toDomain)
            .filter(activity -> activity.getParticipations().stream().anyMatch(p -> p.getEmployeeId().equals(employeeId)))
            .collect(Collectors.toList());
    }

    @Override
    public List<CultureActivity> findAll() {
        return activityMapper.selectList(null).stream().map(CultureActivityEntity::toDomain).collect(Collectors.toList());
    }
}
