package com.company.hr.culture.domain.repository;

import com.company.hr.culture.domain.model.ActivityId;
import com.company.hr.culture.domain.model.ActivityStatus;
import com.company.hr.culture.domain.model.ActivityType;
import com.company.hr.culture.domain.model.CultureActivity;
import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.domain.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 企业文化活动仓储接口
 */
public interface CultureActivityRepository extends Repository<CultureActivity, ActivityId> {
    
    /**
     * 根据类型查找活动列表
     */
    List<CultureActivity> findByType(ActivityType type);
    
    /**
     * 根据状态查找活动列表
     */
    List<CultureActivity> findByStatus(ActivityStatus status);
    
    /**
     * 根据组织者查找活动列表
     */
    List<CultureActivity> findByOrganizerId(EmployeeId organizerId);
    
    /**
     * 查找指定时间范围内的活动
     */
    List<CultureActivity> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 根据员工ID查找其参加的活动列表
     */
    List<CultureActivity> findByParticipantId(EmployeeId employeeId);
    
    /**
     * 查找所有活动
     */
    List<CultureActivity> findAll();
}

