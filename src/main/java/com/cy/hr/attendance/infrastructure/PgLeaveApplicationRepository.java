package com.cy.hr.attendance.infrastructure;

/**
 * 文件说明：PgLeaveApplicationRepository
 */
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.hr.attendance.domain.LeaveApplication;
import com.cy.hr.attendance.domain.LeaveApplicationRepository;
import com.cy.hr.attendance.domain.LeaveStatus;
import com.cy.hr.attendance.infrastructure.mapper.LeaveApplicationMapper;
import com.cy.hr.attendance.infrastructure.po.LeaveApplicationPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PgLeaveApplicationRepository implements LeaveApplicationRepository {

    private final LeaveApplicationMapper mapper;

    @Override
    public LeaveApplication save(LeaveApplication application) {
        LeaveApplicationPO po = toPo(application);
        if (mapper.selectById(po.getId()) == null) {
            mapper.insert(po);
        } else {
            mapper.updateById(po);
        }
        return application;
    }

    @Override
    public Optional<LeaveApplication> findById(String id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<LeaveApplication> findAll() {
        return mapper.selectList(new LambdaQueryWrapper<LeaveApplicationPO>()
                        .orderByDesc(LeaveApplicationPO::getStartDate)
                        .orderByAsc(LeaveApplicationPO::getId))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private LeaveApplicationPO toPo(LeaveApplication application) {
        LeaveApplicationPO po = new LeaveApplicationPO();
        po.setId(application.getId());
        po.setEmployeeId(application.getEmployeeId());
        po.setLeaveType(application.getLeaveType());
        po.setStartDate(application.getStartDate());
        po.setEndDate(application.getEndDate());
        po.setDays(application.getDays());
        po.setReason(application.getReason());
        po.setStatus(application.getStatus().name());
        po.setRejectReason(application.getRejectReason());
        return po;
    }

    private LeaveApplication toDomain(LeaveApplicationPO po) {
        return LeaveApplication.restore(
                po.getId(),
                po.getEmployeeId(),
                po.getLeaveType(),
                po.getStartDate(),
                po.getEndDate(),
                po.getDays(),
                po.getReason(),
                LeaveStatus.valueOf(po.getStatus()),
                po.getRejectReason());
    }
}
