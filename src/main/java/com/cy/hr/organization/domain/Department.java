package com.cy.hr.organization.domain;

/**
 * 文件说明：Department
 */
import com.cy.hr.shared.domain.DomainException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Department {

    /** 部门ID */
    private final String id;
    /** 部门名称 */
    private final String name;
    /** 上级部门ID */
    private final String parentDepartmentId;
    /** 部门层级 */
    private final int level;
    /** 部门负责人 */
    private final String leader;
    /** 编制数 */
    private final int staffingQuota;
    /** 生效日期 */
    private final LocalDate effectiveDate;

    /**
     * 创建部门聚合。
     */
    public static Department create(String name,
                                    String parentDepartmentId,
                                    int level,
                                    String leader,
                                    int staffingQuota,
                                    LocalDate effectiveDate) {
        // 业务规则：部门层级不超过5级
        if (level < 1 || level > 5) {
            throw new DomainException("部门层级必须在1到5之间");
        }
        // 业务规则：编制不可为负数
        if (staffingQuota < 0) {
            throw new DomainException("编制数不能小于0");
        }
        if (Objects.isNull(effectiveDate)) {
            throw new DomainException("生效日期不能为空");
        }
        return new Department(UUID.randomUUID().toString(), name, parentDepartmentId, level, leader, staffingQuota, effectiveDate);
    }

    /**
     * 从持久化数据恢复部门聚合。
     */
    public static Department restore(String id,
                                     String name,
                                     String parentDepartmentId,
                                     int level,
                                     String leader,
                                     int staffingQuota,
                                     LocalDate effectiveDate) {
        return new Department(id, name, parentDepartmentId, level, leader, staffingQuota, effectiveDate);
    }

}
