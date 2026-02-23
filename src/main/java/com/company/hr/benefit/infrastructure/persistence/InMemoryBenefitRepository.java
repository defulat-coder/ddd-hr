package com.company.hr.benefit.infrastructure.persistence;

import com.company.hr.benefit.domain.model.Benefit;
import com.company.hr.benefit.domain.model.BenefitId;
import com.company.hr.benefit.domain.model.BenefitType;
import com.company.hr.benefit.domain.repository.BenefitRepository;
import com.company.hr.employee.domain.model.EmployeeId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryBenefitRepository implements BenefitRepository {

    private final ConcurrentMap<String, Benefit> store = new ConcurrentHashMap<>();

    @Override
    public Benefit save(Benefit aggregate) {
        store.put(aggregate.getId().getValue(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<Benefit> findById(BenefitId id) {
        return Optional.ofNullable(store.get(id.getValue()));
    }

    @Override
    public void delete(Benefit aggregate) {
        store.remove(aggregate.getId().getValue());
    }

    @Override
    public void deleteById(BenefitId id) {
        store.remove(id.getValue());
    }

    @Override
    public boolean existsById(BenefitId id) {
        return store.containsKey(id.getValue());
    }

    @Override
    public List<Benefit> findByType(BenefitType type) {
        List<Benefit> result = new ArrayList<>();
        for (Benefit benefit : store.values()) {
            if (benefit.getType() == type) {
                result.add(benefit);
            }
        }
        return result;
    }

    @Override
    public List<Benefit> findActiveBenefits() {
        List<Benefit> result = new ArrayList<>();
        for (Benefit benefit : store.values()) {
            if (benefit.isActive()) {
                result.add(benefit);
            }
        }
        return result;
    }

    @Override
    public List<Benefit> findByEmployeeId(EmployeeId employeeId) {
        List<Benefit> result = new ArrayList<>();
        for (Benefit benefit : store.values()) {
            boolean matched = benefit.getEnrollments().stream()
                .anyMatch(e -> e.getEmployeeId().equals(employeeId));
            if (matched) {
                result.add(benefit);
            }
        }
        return result;
    }

    @Override
    public List<Benefit> findAll() {
        return new ArrayList<>(store.values());
    }
}
