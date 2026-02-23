package com.cy.hr.organization.infrastructure.po;

/**
 * 文件说明：DepartmentPO
 */
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("departments")
public class DepartmentPO {

    @TableId
    /** 部门ID */
    private String id;
    /** 部门名称 */
    private String name;
    /** 上级部门ID */
    private String parentDepartmentId;
    /** 层级 */
    private Integer level;
    /** 负责人 */
    private String leader;
    /** 编制数 */
    private Integer staffingQuota;
    /** 生效日期 */
    private LocalDate effectiveDate;
}
