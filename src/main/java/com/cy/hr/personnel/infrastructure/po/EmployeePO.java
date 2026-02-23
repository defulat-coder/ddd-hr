package com.cy.hr.personnel.infrastructure.po;

/**
 * 文件说明：EmployeePO
 */
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("employees")
public class EmployeePO {

    @TableId
    /** 员工ID */
    private String id;
    /** 工号 */
    private String employeeNo;
    /** 姓名 */
    private String name;
    /** 性别 */
    private String gender;
    /** 出生日期 */
    private LocalDate birthDate;
    /** 身份证号 */
    private String idCardNo;
    /** 手机号 */
    private String phone;
    /** 部门ID */
    private String departmentId;
    /** 岗位 */
    private String position;
    /** 入职日期 */
    private LocalDate onboardingDate;
    /** 员工状态 */
    private String status;
    /** 合同类型 */
    private String contractType;
    /** 合同开始日期 */
    private LocalDate contractStartDate;
    /** 合同结束日期 */
    private LocalDate contractEndDate;
    /** 试用期（月） */
    private Integer probationMonths;
    /** 合同签订日期 */
    private LocalDate contractSignedDate;
}
