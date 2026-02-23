package com.company.hr.culture.domain.model;

import com.company.hr.shared.domain.ValueObject;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * 活动时间表值对象
 */
@Value
public class ActivitySchedule implements ValueObject {
    LocalDateTime startTime;
    LocalDateTime endTime;
    LocalDateTime registrationDeadline;
    
    public boolean isRegistrationOpen() {
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(registrationDeadline) && now.isBefore(startTime);
    }
    
    public boolean isOngoing() {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }
    
    public boolean isCompleted() {
        return LocalDateTime.now().isAfter(endTime);
    }
    
    public void validate() {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        if (registrationDeadline.isAfter(startTime)) {
            throw new IllegalArgumentException("报名截止时间不能晚于活动开始时间");
        }
    }
}

