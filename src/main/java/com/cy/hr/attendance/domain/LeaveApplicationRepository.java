package com.cy.hr.attendance.domain;

/**
 * 文件说明：LeaveApplicationRepository
 */
import java.util.List;
import java.util.Optional;

public interface LeaveApplicationRepository {

    LeaveApplication save(LeaveApplication application);

    Optional<LeaveApplication> findById(String id);

    List<LeaveApplication> findAll();
}
