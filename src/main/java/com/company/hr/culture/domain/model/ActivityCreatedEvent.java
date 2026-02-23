package com.company.hr.culture.domain.model;

import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 活动创建事件
 */
@Getter
public class ActivityCreatedEvent extends DomainEvent {
    private final ActivityId activityId;
    private final String title;
    private final ActivityType type;
    private final LocalDateTime startTime;
    
    public ActivityCreatedEvent(ActivityId activityId, String title, 
                               ActivityType type, LocalDateTime startTime) {
        super();
        this.activityId = activityId;
        this.title = title;
        this.type = type;
        this.startTime = startTime;
    }
}

