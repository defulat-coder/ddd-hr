package com.company.hr.shared.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 聚合根基类
 * DDD中的聚合根标识，管理领域事件
 */
public abstract class AggregateRoot<ID> extends Entity<ID> {
    
    private final transient List<DomainEvent> domainEvents = new ArrayList<>();
    
    protected AggregateRoot(ID id) {
        super(id);
    }
    
    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }
    
    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }
    
    public void clearDomainEvents() {
        domainEvents.clear();
    }
}

