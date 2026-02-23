package com.company.hr.employee.acl.external;

import com.company.hr.employee.domain.model.ContactInfo;
import com.company.hr.employee.domain.model.PersonalInfo;
import com.company.hr.organization.domain.model.DepartmentId;
import com.company.hr.organization.domain.model.PositionId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 转换后的候选人数据
 * 适配器返回的中间数据，由工厂使用
 */
@Getter
@AllArgsConstructor
public class ConvertedData {
    private final PersonalInfo personalInfo;
    private final ContactInfo contactInfo;
    private final DepartmentId departmentId;
    private final PositionId positionId;
    private final LocalDate hireDate;
}


