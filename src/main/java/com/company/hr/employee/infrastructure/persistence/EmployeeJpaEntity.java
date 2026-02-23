package com.company.hr.employee.infrastructure.persistence;

import com.company.hr.employee.domain.model.*;
import com.company.hr.infrastructure.persistence.BaseJpaEntity;
import com.company.hr.organization.domain.model.DepartmentId;
import com.company.hr.organization.domain.model.PositionId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 员工JPA实体
 */
@Entity
@Table(name = "employees")
@Getter
@Setter
public class EmployeeJpaEntity extends BaseJpaEntity {
    
    @Id
    private String id;
    
    @Column(unique = true, nullable = false)
    private String employeeNumber;
    
    private String firstName;
    private String lastName;
    private String idCardNumber;
    private LocalDate birthDate;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    private String email;
    private String phoneNumber;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    
    private String departmentId;
    private String positionId;
    
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;
    
    private LocalDate hireDate;
    private LocalDate probationEndDate;
    private LocalDate resignDate;
    
    /**
     * 从领域模型转换
     */
    public static EmployeeJpaEntity fromDomain(Employee employee) {
        EmployeeJpaEntity entity = new EmployeeJpaEntity();
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

