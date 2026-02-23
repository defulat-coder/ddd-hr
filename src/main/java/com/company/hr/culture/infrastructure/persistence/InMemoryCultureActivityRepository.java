package com.company.hr.culture.infrastructure.persistence;

import com.company.hr.culture.domain.model.ActivityId;
import com.company.hr.culture.domain.model.ActivityStatus;
import com.company.hr.culture.domain.model.ActivityType;
import com.company.hr.culture.domain.model.CultureActivity;
import com.company.hr.culture.domain.repository.CultureActivityRepository;
import com.company.hr.employee.domain.model.EmployeeId;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryCultureActivityRepository implements CultureActivityRepository {

    private final ConcurrentMap<String, CultureActivity> store = new ConcurrentHashMap<>();

    @Override
    public CultureActivity save(CultureActivity aggregate) {
        store.put(aggregate.getId().getValue(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<CultureActivity> findById(ActivityId id) {
        return Optional.ofNullable(store.get(id.getValue()));
    }

    @Override
    public void delete(CultureActivity aggregate) {
        store.remove(aggregate.getId().getValue());
    }

    @Override
    public void deleteById(ActivityId id) {
        store.remove(id.getValue());
    }

    @Override
    public boolean existsById(ActivityId id) {
        return store.containsKey(id.getValue());
    }

    @Override
    public List<CultureActivity> findByType(ActivityType type) {
        List<CultureActivity> result = new ArrayList<>();
        for (CultureActivity activity : store.values()) {
            if (activity.getType() == type) {
                result.add(activity);
            }
        }
        return result;
    }

    @Override
    public List<CultureActivity> findByStatus(ActivityStatus status) {
        List<CultureActivity> result = new ArrayList<>();
        for (CultureActivity activity : store.values()) {
            if (activity.getStatus() == status) {
                result.add(activity);
            }
        }
        return result;
    }

    @Override
    public List<CultureActivity> findByOrganizerId(EmployeeId organizerId) {
        List<CultureActivity> result = new ArrayList<>();
        for (CultureActivity activity : store.values()) {
            if (activity.getOrganizerId().equals(organizerId)) {
                result.add(activity);
            }
        }
        return result;
    }

    @Override
    public List<CultureActivity> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<CultureActivity> result = new ArrayList<>();
        for (CultureActivity activity : store.values()) {
            if (!activity.getSchedule().getStartTime().isBefore(startDate)
                && !activity.getSchedule().getEndTime().isAfter(endDate)) {
                result.add(activity);
            }
        }
        return result;
    }

    @Override
    public List<CultureActivity> findByParticipantId(EmployeeId employeeId) {
        List<CultureActivity> result = new ArrayList<>();
        for (CultureActivity activity : store.values()) {
            boolean matched = activity.getParticipations().stream()
                .anyMatch(p -> p.getEmployeeId().equals(employeeId));
            if (matched) {
                result.add(activity);
            }
        }
        return result;
    }

    @Override
    public List<CultureActivity> findAll() {
        return new ArrayList<>(store.values());
    }
}
