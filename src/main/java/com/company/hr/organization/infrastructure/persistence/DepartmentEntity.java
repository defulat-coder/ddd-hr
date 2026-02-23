package com.company.hr.organization.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.infrastructure.persistence.BaseEntity;
import com.company.hr.organization.domain.model.*;
import com.company.hr.shared.infrastructure.persistence.JsonUtils;
import com.company.hr.shared.infrastructure.persistence.ReflectionUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("departments")
public class DepartmentEntity extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private String id;
    private String name;
    private String code;
    private String type;
    private String parentId;
    private String managerId;
    private String description;
    private Boolean active;
    private String positionsJson;

    public static DepartmentEntity fromDomain(Department department) {
        DepartmentEntity entity = new DepartmentEntity();
        entity.setId(department.getId().getValue());
        entity.setName(department.getName());
        entity.setCode(department.getCode());
        entity.setType(department.getType().name());
        entity.setParentId(department.getParentId() == null ? null : department.getParentId().getValue());
        entity.setManagerId(department.getManagerId() == null ? null : department.getManagerId().getValue());
        entity.setDescription(department.getDescription());
        entity.setActive(department.isActive());

        List<PositionSnapshot> snapshots = new ArrayList<>();
        for (Position position : department.getPositions()) {
            PositionSnapshot snapshot = new PositionSnapshot();
            snapshot.id = position.getId().getValue();
            snapshot.title = position.getTitle();
            snapshot.code = position.getCode();
            snapshot.level = position.getLevel().name();
            snapshot.minSalary = position.getMinSalary();
            snapshot.maxSalary = position.getMaxSalary();
            snapshot.description = position.getDescription();
            snapshot.headcount = position.getHeadcount();
            snapshot.maxHeadcount = position.getMaxHeadcount();
            snapshots.add(snapshot);
        }
        entity.setPositionsJson(JsonUtils.toJson(snapshots));
        return entity;
    }

    public Department toDomain() {
        Department department = new Department(
            DepartmentId.of(id),
            name,
            code,
            DepartmentType.valueOf(type),
            parentId == null ? null : DepartmentId.of(parentId),
            managerId == null ? null : EmployeeId.of(managerId),
            description
        );
        department.clearDomainEvents();

        List<Position> positions = new ArrayList<>();
        if (positionsJson != null && !positionsJson.isBlank()) {
            List<PositionSnapshot> snapshots = JsonUtils.fromJson(positionsJson, new TypeReference<List<PositionSnapshot>>() {});
            for (PositionSnapshot snapshot : snapshots) {
                Position position = new Position(
                    PositionId.of(snapshot.id),
                    snapshot.title,
                    snapshot.code,
                    PositionLevel.valueOf(snapshot.level),
                    snapshot.minSalary,
                    snapshot.maxSalary,
                    snapshot.description,
                    snapshot.maxHeadcount
                );
                ReflectionUtils.setField(position, "headcount", snapshot.headcount);
                positions.add(position);
            }
        }

        ReflectionUtils.setField(department, "positions", positions);
        ReflectionUtils.setField(department, "active", active != null ? active : Boolean.TRUE);
        return department;
    }

    public static class PositionSnapshot {
        public String id;
        public String title;
        public String code;
        public String level;
        public BigDecimal minSalary;
        public BigDecimal maxSalary;
        public String description;
        public Integer headcount;
        public Integer maxHeadcount;
    }
}
