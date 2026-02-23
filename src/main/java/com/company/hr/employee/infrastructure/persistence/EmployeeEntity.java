package com.company.hr.employee.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hr.employee.domain.model.*;
import com.company.hr.infrastructure.persistence.BaseEntity;
import com.company.hr.organization.domain.model.DepartmentId;
import com.company.hr.organization.domain.model.PositionId;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 员工持久化实体
 */
@TableName("employees")
@Getter
@Setter
public class EmployeeEntity extends BaseEntity {
    
    @TableId(type = IdType.INPUT)
    private String id;
    
    private String employeeNumber;
    
    private String firstName;
    private String lastName;
    private String idCardNumber;
    private LocalDate birthDate;
    
    private Gender gender;
    
    private String email;
    private String phoneNumber;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    
    private String departmentId;
    private String positionId;
    
    private EmployeeStatus status;
    
    private LocalDate hireDate;
    private LocalDate probationEndDate;
    private LocalDate resignDate;
    
    /**
     * 从领域模型转换
     */
    public static EmployeeEntity fromDomain(Employee employee) {
        EmployeeEntity entity = new EmployeeEntity();
        entity.setId(employee.getId().getValue());
        entity.setEmployeeNumber(employee.getEmployeeNumber());
        
        PersonalInfo personalInfo = employee.getPersonalInfo();
        entity.setFirstName(personalInfo.getFirstName());
        entity.setLastName(personalInfo.getLastName());
        entity.setIdCardNumber(personalInfo.getIdCardNumber());
        entity.setBirthDate(personalInfo.getBirthDate());
        entity.setGender(personalInfo.getGender());
        
        ContactInfo contactInfo = employee.getContactInfo();
        entity.setEmail(contactInfo.getEmail());
        entity.setPhoneNumber(contactInfo.getPhoneNumber());
        entity.setAddress(contactInfo.getAddress());
        entity.setEmergencyContact(contactInfo.getEmergencyContact());
        entity.setEmergencyPhone(contactInfo.getEmergencyPhone());
        
        entity.setDepartmentId(employee.getDepartmentId().getValue());
        entity.setPositionId(employee.getPositionId().getValue());
        entity.setStatus(employee.getStatus());
        entity.setHireDate(employee.getHireDate());
        entity.setProbationEndDate(employee.getProbationEndDate());
        entity.setResignDate(employee.getResignDate());
        
        return entity;
    }
    
    /**
     * 转换为领域模型（使用工厂重建）
     */
    public Employee toDomain(com.company.hr.employee.domain.factory.EmployeeFactory factory) {
        PersonalInfo personalInfo = new PersonalInfo(
            firstName, lastName, idCardNumber, birthDate, gender
        );
        
        ContactInfo contactInfo = new ContactInfo(
            email, phoneNumber, address, emergencyContact, emergencyPhone
        );
        
        // 使用工厂的reconstitute方法重建完整状态的员工聚合根
        return factory.reconstitute(
            EmployeeId.of(id),
            employeeNumber,
            personalInfo,
            contactInfo,
            DepartmentId.of(departmentId),
            PositionId.of(positionId),
            status,
            hireDate,
            probationEndDate,
            resignDate
        );
    }
}
