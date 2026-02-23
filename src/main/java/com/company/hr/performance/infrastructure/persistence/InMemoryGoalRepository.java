package com.company.hr.performance.infrastructure.persistence;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.performance.domain.model.Goal;
import com.company.hr.performance.domain.model.GoalId;
import com.company.hr.performance.domain.model.GoalStatus;
import com.company.hr.performance.domain.repository.GoalRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryGoalRepository implements GoalRepository {

    private final ConcurrentMap<String, Goal> store = new ConcurrentHashMap<>();

    @Override
    public Goal save(Goal aggregate) {
        store.put(aggregate.getId().getValue(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<Goal> findById(GoalId id) {
        return Optional.ofNullable(store.get(id.getValue()));
    }

    @Override
    public void delete(Goal aggregate) {
        store.remove(aggregate.getId().getValue());
    }

    @Override
    public void deleteById(GoalId id) {
        store.remove(id.getValue());
    }

    @Override
    public boolean existsById(GoalId id) {
        return store.containsKey(id.getValue());
    }

    @Override
    public List<Goal> findByEmployeeId(EmployeeId employeeId) {
        List<Goal> result = new ArrayList<>();
        for (Goal goal : store.values()) {
            if (goal.getEmployeeId().equals(employeeId)) {
                result.add(goal);
            }
        }
        return result;
    }

    @Override
    public List<Goal> findByEmployeeIdAndStatus(EmployeeId employeeId, GoalStatus status) {
        List<Goal> result = new ArrayList<>();
        for (Goal goal : store.values()) {
            if (goal.getEmployeeId().equals(employeeId) && goal.getStatus() == status) {
                result.add(goal);
            }
        }
        return result;
    }

    @Override
    public List<Goal> findActiveGoalsByEmployeeId(EmployeeId employeeId) {
        return findByEmployeeIdAndStatus(employeeId, GoalStatus.ACTIVE);
    }
}
