package com.company.hr.shared.domain;

import java.util.Objects;

/**
 * 实体基类
 * DDD中的实体，拥有唯一标识
 */
public abstract class Entity<ID> {
    
    private final ID id;
    
    protected Entity(ID id) {
        this.id = Objects.requireNonNull(id, "Entity ID cannot be null");
    }
    
    public ID getId() {
        return id;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

