package com.company.hr.culture.domain.factory;

import com.company.hr.culture.domain.model.*;
import com.company.hr.employee.domain.model.EmployeeId;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 文化活动工厂
 * 负责创建文化活动聚合根
 */
@Component
public class CultureActivityFactory {
    
    /**
     * 创建文化活动
     * 
     * @param title 标题
     * @param description 描述
     * @param type 活动类型
     * @param schedule 活动时间表
     * @param location 地点
     * @param organizerId 组织者ID
     * @param maxParticipants 最大参与人数
     * @param budget 预算
     * @return 新创建的活动
     */
    public CultureActivity createActivity(
            String title,
            String description,
            ActivityType type,
            ActivitySchedule schedule,
            String location,
            EmployeeId organizerId,
            int maxParticipants,
            BigDecimal budget) {
        
        ActivityId activityId = ActivityId.generate();
        
        return new CultureActivity(
            activityId,
            title,
            description,
            type,
            schedule,
            location,
            organizerId,
            maxParticipants,
            budget
        );
    }
    
    /**
     * 创建团建活动
     * 
     * @param title 活动标题
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param location 地点
     * @param organizerId 组织者
     * @param maxParticipants 最大人数
     * @param budget 预算
     * @return 团建活动
     */
    public CultureActivity createTeamBuildingActivity(
            String title,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String location,
            EmployeeId organizerId,
            int maxParticipants,
            BigDecimal budget) {
        
        // 报名截止时间：活动开始前3天
        LocalDateTime registrationDeadline = startTime.minusDays(3);
        
        ActivitySchedule schedule = new ActivitySchedule(
            startTime,
            endTime,
            registrationDeadline
        );
        
        return createActivity(
            title,
            "团队建设活动，增强团队凝聚力",
            ActivityType.TEAM_BUILDING,
            schedule,
            location,
            organizerId,
            maxParticipants,
            budget
        );
    }
    
    /**
     * 创建培训活动
     * 
     * @param title 培训标题
     * @param startTime 开始时间
     * @param durationHours 持续时长（小时）
     * @param location 地点
     * @param organizerId 组织者
     * @param maxParticipants 最大人数
     * @return 培训活动
     */
    public CultureActivity createTrainingActivity(
            String title,
            LocalDateTime startTime,
            int durationHours,
            String location,
            EmployeeId organizerId,
            int maxParticipants) {
        
        LocalDateTime endTime = startTime.plusHours(durationHours);
        LocalDateTime registrationDeadline = startTime.minusDays(2);
        
        ActivitySchedule schedule = new ActivitySchedule(
            startTime,
            endTime,
            registrationDeadline
        );
        
        return createActivity(
            title,
            "员工技能培训",
            ActivityType.TRAINING,
            schedule,
            location,
            organizerId,
            maxParticipants,
            new BigDecimal("5000") // 培训默认预算
        );
    }
    
    /**
     * 创建新员工入职培训
     * 
     * @param startTime 开始时间
     * @param organizerId 组织者
     * @return 入职培训活动
     */
    public CultureActivity createOnboardingTraining(
            LocalDateTime startTime,
            EmployeeId organizerId) {
        
        LocalDateTime endTime = startTime.plusDays(1);
        LocalDateTime registrationDeadline = startTime.minusDays(1);
        
        ActivitySchedule schedule = new ActivitySchedule(
            startTime,
            endTime,
            registrationDeadline
        );
        
        return createActivity(
            "新员工入职培训",
            "公司文化、规章制度、办公系统使用培训",
            ActivityType.TRAINING,
            schedule,
            "总部会议室",
            organizerId,
            20,
            new BigDecimal("3000")
        );
    }
    
    /**
     * 创建年会活动
     * 
     * @param year 年度
     * @param startTime 开始时间
     * @param location 地点
     * @param organizerId 组织者
     * @param budget 预算
     * @return 年会活动
     */
    public CultureActivity createAnnualMeeting(
            int year,
            LocalDateTime startTime,
            String location,
            EmployeeId organizerId,
            BigDecimal budget) {
        
        LocalDateTime endTime = startTime.plusHours(4);
        LocalDateTime registrationDeadline = startTime.minusDays(7);
        
        ActivitySchedule schedule = new ActivitySchedule(
            startTime,
            endTime,
            registrationDeadline
        );
        
        return createActivity(
            String.format("%d年度年会", year),
            "公司年度总结表彰大会",
            ActivityType.ANNUAL_MEETING,
            schedule,
            location,
            organizerId,
            500, // 年会人数较多
            budget
        );
    }
    
    /**
     * 创建体育活动
     * 
     * @param title 活动标题
     * @param startTime 开始时间
     * @param location 地点
     * @param organizerId 组织者
     * @param maxParticipants 最大人数
     * @return 体育活动
     */
    public CultureActivity createSportsActivity(
            String title,
            LocalDateTime startTime,
            String location,
            EmployeeId organizerId,
            int maxParticipants) {
        
        LocalDateTime endTime = startTime.plusHours(3);
        LocalDateTime registrationDeadline = startTime.minusDays(2);
        
        ActivitySchedule schedule = new ActivitySchedule(
            startTime,
            endTime,
            registrationDeadline
        );
        
        return createActivity(
            title,
            "员工体育健身活动",
            ActivityType.SPORTS,
            schedule,
            location,
            organizerId,
            maxParticipants,
            new BigDecimal("2000")
        );
    }
}


