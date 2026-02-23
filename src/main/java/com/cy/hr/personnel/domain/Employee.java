package com.cy.hr.personnel.domain;

/**
 * 文件说明：Employee
 */
import com.cy.hr.shared.domain.DomainException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Employee {

    /** 员工ID */
    private final String id;
    /** 员工工号 */
    private final String employeeNo;
    /** 姓名 */
    private final String name;
    /** 性别 */
    private final String gender;
    /** 出生日期 */
    private final LocalDate birthDate;
    /** 身份证号 */
    private final String idCardNo;
    /** 联系电话 */
    private final String phone;
    /** 部门ID */
    private String departmentId;
    /** 岗位 */
    private String position;
    /** 入职日期 */
    private final LocalDate onboardingDate;
    /** 劳动合同 */
    private final EmploymentContract contract;
    /** 员工状态 */
    private EmployeeStatus status;

    /**
     * 办理员工入职并创建员工聚合。
     */
    public static Employee onboard(String employeeNo,
                                   String name,
                                   String gender,
                                   LocalDate birthDate,
                                   String idCardNo,
                                   String phone,
                                   String departmentId,
                                   String position,
                                   LocalDate onboardingDate,
                                   EmploymentContract contract) {
        // 业务规则：入职基础信息必须完整
        if (name == null || name.isBlank()) {
            throw new DomainException("姓名不能为空");
        }
        if (idCardNo == null || idCardNo.isBlank()) {
            throw new DomainException("身份证号不能为空");
        }
        if (departmentId == null || departmentId.isBlank()) {
            throw new DomainException("部门不能为空");
        }
        if (position == null || position.isBlank()) {
            throw new DomainException("岗位不能为空");
        }
        if (onboardingDate == null) {
            throw new DomainException("入职日期不能为空");
        }
        // 业务规则：合同信息必填
        if (contract == null) {
            throw new DomainException("合同信息必填");
        }
        return new Employee(
                UUID.randomUUID().toString(),
                employeeNo,
                name,
                gender,
                birthDate,
                idCardNo,
                phone,
                departmentId,
                position,
                onboardingDate,
                contract,
                EmployeeStatus.PROBATION
        );
    }

    /**
     * 从持久化数据恢复员工聚合。
     */
    public static Employee restore(String id,
                                   String employeeNo,
                                   String name,
                                   String gender,
                                   LocalDate birthDate,
                                   String idCardNo,
                                   String phone,
                                   String departmentId,
                                   String position,
                                   LocalDate onboardingDate,
                                   EmploymentContract contract,
                                   EmployeeStatus status) {
        return new Employee(id, employeeNo, name, gender, birthDate, idCardNo, phone, departmentId, position, onboardingDate, contract, status);
    }

    /**
     * 员工转正。
     */
    public void becomeRegular() {
        // 业务规则：离职员工不能转正
        if (status == EmployeeStatus.RESIGNED) {
            throw new DomainException("离职员工不能转正");
        }
        this.status = EmployeeStatus.ACTIVE;
    }

    /**
     * 员工调岗。
     */
    public void transfer(String departmentId, String position) {
        // 业务规则：离职员工不能调岗
        if (status == EmployeeStatus.RESIGNED) {
            throw new DomainException("离职员工不能调岗");
        }
        if (departmentId == null || departmentId.isBlank() || position == null || position.isBlank()) {
            throw new DomainException("调岗参数不完整");
        }
        this.departmentId = departmentId;
        this.position = position;
    }

    /**
     * 员工离职。
     */
    public void resign() {
        this.status = EmployeeStatus.RESIGNED;
    }

}
