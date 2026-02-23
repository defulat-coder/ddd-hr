package com.company.hr.culture.domain.model;

import com.company.hr.shared.domain.DomainEvent;
import lombok.Getter;

/**
 * 活动完成事件
 */
@Getter
public class ActivityCompletedEvent extends DomainEvent {
    private final ActivityId activityId;
    private final long attendeeCount;
    
    public ActivityCompletedEvent(ActivityId activityId, long attendeeCount) {
        super();
        this.activityId = activityId;
        this.attendeeCount = attendeeCount;
    }
}

