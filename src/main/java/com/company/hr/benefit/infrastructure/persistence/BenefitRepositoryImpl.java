package com.company.hr.benefit.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.hr.benefit.domain.model.Benefit;
import com.company.hr.benefit.domain.model.BenefitId;
import com.company.hr.benefit.domain.model.BenefitType;
import com.company.hr.benefit.domain.repository.BenefitRepository;
import com.company.hr.employee.domain.model.EmployeeId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BenefitRepositoryImpl implements BenefitRepository {

    private final BenefitMapper benefitMapper;

    @Override
    public Benefit save(Benefit aggregate) {
        BenefitEntity entity = BenefitEntity.fromDomain(aggregate);
        if (benefitMapper.selectById(entity.getId()) == null) {
            benefitMapper.insert(entity);
        } else {
            benefitMapper.updateById(entity);
        }
        return benefitMapper.selectById(entity.getId()).toDomain();
    }

    @Override
    public Optional<Benefit> findById(BenefitId id) {
        BenefitEntity entity = benefitMapper.selectById(id.getValue());
        return Optional.ofNullable(entity).map(BenefitEntity::toDomain);
    }

    @Override
    public void delete(Benefit aggregate) {
        benefitMapper.deleteById(aggregate.getId().getValue());
    }

    @Override
    public void deleteById(BenefitId id) {
        benefitMapper.deleteById(id.getValue());
    }

    @Override
    public boolean existsById(BenefitId id) {
        return benefitMapper.selectById(id.getValue()) != null;
    }

    @Override
    public List<Benefit> findByType(BenefitType type) {
        return benefitMapper.selectList(new LambdaQueryWrapper<BenefitEntity>().eq(BenefitEntity::getType, type.name()))
            .stream().map(BenefitEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Benefit> findActiveBenefits() {
        return benefitMapper.selectList(new LambdaQueryWrapper<BenefitEntity>().eq(BenefitEntity::getActive, true))
            .stream().map(BenefitEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Benefit> findByEmployeeId(EmployeeId employeeId) {
        return benefitMapper.selectList(null).stream()
            .map(BenefitEntity::toDomain)
            .filter(benefit -> benefit.getEnrollments().stream().anyMatch(e -> e.getEmployeeId().equals(employeeId)))
            .collect(Collectors.toList());
    }

    @Override
    public List<Benefit> findAll() {
        return benefitMapper.selectList(null).stream().map(BenefitEntity::toDomain).collect(Collectors.toList());
    }
}
