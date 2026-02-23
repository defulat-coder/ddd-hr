package com.company.hr.organization.infrastructure.persistence;

import com.company.hr.organization.domain.model.Department;
import com.company.hr.organization.domain.model.DepartmentId;
import com.company.hr.organization.domain.repository.DepartmentRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryDepartmentRepository implements DepartmentRepository {

    private final ConcurrentMap<String, Department> store = new ConcurrentHashMap<>();

    @Override
    public Department save(Department aggregate) {
        store.put(aggregate.getId().getValue(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<Department> findById(DepartmentId id) {
        return Optional.ofNullable(store.get(id.getValue()));
    }

    @Override
    public void delete(Department aggregate) {
        store.remove(aggregate.getId().getValue());
    }

    @Override
    public void deleteById(DepartmentId id) {
        store.remove(id.getValue());
    }

    @Override
    public boolean existsById(DepartmentId id) {
        return store.containsKey(id.getValue());
    }

    @Override
    public Optional<Department> findByCode(String code) {
        return store.values().stream()
            .filter(d -> d.getCode().equals(code))
            .findFirst();
    }

    @Override
    public List<Department> findByParentId(DepartmentId parentId) {
        List<Department> result = new ArrayList<>();
        for (Department department : store.values()) {
            if (department.getParentId() != null && department.getParentId().equals(parentId)) {
                result.add(department);
            }
        }
        return result;
    }

    @Override
    public List<Department> findTopLevelDepartments() {
        List<Department> result = new ArrayList<>();
        for (Department department : store.values()) {
            if (department.getParentId() == null) {
                result.add(department);
            }
        }
        return result;
    }

    @Override
    public List<Department> findActiveDepartments() {
        List<Department> result = new ArrayList<>();
        for (Department department : store.values()) {
            if (department.isActive()) {
                result.add(department);
            }
        }
        return result;
    }

    @Override
    public List<Department> findAll() {
        return new ArrayList<>(store.values());
    }
}
