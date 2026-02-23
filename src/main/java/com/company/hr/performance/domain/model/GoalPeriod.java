package com.company.hr.performance.domain.model;

import com.company.hr.shared.domain.ValueObject;
import lombok.Value;

import java.time.LocalDate;

/**
 * 目标周期值对象
 */
@Value
public class GoalPeriod implements ValueObject {
    LocalDate startDate;
    LocalDate endDate;
    LocalDate registrationDeadline; // 目标设置截止日期
    
    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }
    
    public boolean isCompleted() {
        return LocalDate.now().isAfter(endDate);
    }
    
    public boolean canRegister() {
        return LocalDate.now().isBefore(registrationDeadline);
    }
    
    public void validate() {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }
        if (registrationDeadline.isAfter(endDate)) {
            throw new IllegalArgumentException("目标设置截止日期不能晚于结束日期");
        }
    }
}

