package com.company.hr.shared.domain;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 领域事件基类
 * 用于领域事件的发布和订阅
 */
public abstract class DomainEvent {
    
    private final String eventId;
    private final LocalDateTime occurredOn;
    
    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}

