package com.company.hr.employee.acl.external;

import com.company.hr.employee.domain.model.*;
import com.company.hr.organization.domain.model.DepartmentId;
import com.company.hr.organization.domain.model.PositionId;
import com.company.hr.shared.acl.ExternalSystemAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 招聘系统适配器
 * 防腐层：将外部招聘系统的候选人数据转换为内部数据
 * 注意：适配器负责数据转换，工厂负责对象创建
 */
@Component
@Slf4j
public class RecruitmentSystemAdapter implements 
    ExternalSystemAdapter<ConvertedData, RecruitmentSystemClient.CandidateData> {
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public ConvertedData toDomainModel(RecruitmentSystemClient.CandidateData externalModel) {
        log.info("将外部招聘系统候选人数据转换为内部数据: {}", externalModel.getId());
        
        // 转换性别：外部系统使用 M/F，内部使用枚举
        Gender gender = convertGender(externalModel.getSex());
        
        // 转换日期：外部系统使用字符串，内部使用LocalDate
        LocalDate birthDate = LocalDate.parse(externalModel.getBirthDateStr(), DATE_FORMATTER);
        LocalDate hireDate = LocalDate.parse(externalModel.getHiringDate(), DATE_FORMATTER);
        
        // 创建值对象
        PersonalInfo personalInfo = new PersonalInfo(
            externalModel.getFirstName(),
            externalModel.getLastName(),
            externalModel.getIdNumber(),
            birthDate,
            gender
        );
        
        ContactInfo contactInfo = new ContactInfo(
            externalModel.getEmailAddress(),
            externalModel.getMobile(),
            externalModel.getHomeAddress(),
            externalModel.getEmergencyContactName(),
            externalModel.getEmergencyContactPhone()
        );
        
        // 转换部门和职位：外部系统使用code，内部使用ID
        DepartmentId departmentId = convertDepartmentCode(externalModel.getDepartmentCode());
        PositionId positionId = convertPositionCode(externalModel.getPositionCode());
        
        log.info("候选人数据转换完成");
        
        // 适配器返回转换后的数据，由工厂负责创建完整的Employee对象
        return new ConvertedData(
            personalInfo,
            contactInfo,
            departmentId,
            positionId,
            hireDate
        );
    }
    
    @Override
    public RecruitmentSystemClient.CandidateData toExternalModel(ConvertedData domainModel) {
        log.info("将内部数据转换为外部招聘系统数据");
        
        RecruitmentSystemClient.CandidateData candidateData = 
            new RecruitmentSystemClient.CandidateData();
        
        // 转换基本信息
        candidateData.setFirstName(domainModel.getPersonalInfo().getFirstName());
        candidateData.setLastName(domainModel.getPersonalInfo().getLastName());
        candidateData.setFullName(domainModel.getPersonalInfo().getFullName());
        candidateData.setIdNumber(domainModel.getPersonalInfo().getIdCardNumber());
        
        // 转换日期为字符串
        candidateData.setBirthDateStr(
            domainModel.getPersonalInfo().getBirthDate().format(DATE_FORMATTER)
        );
        
        // 转换性别为 M/F
        candidateData.setSex(convertGenderToExternal(domainModel.getPersonalInfo().getGender()));
        
        // 转换联系信息
        candidateData.setEmailAddress(domainModel.getContactInfo().getEmail());
        candidateData.setMobile(domainModel.getContactInfo().getPhoneNumber());
        candidateData.setHomeAddress(domainModel.getContactInfo().getAddress());
        candidateData.setEmergencyContactName(domainModel.getContactInfo().getEmergencyContact());
        candidateData.setEmergencyContactPhone(domainModel.getContactInfo().getEmergencyPhone());
        
        // 转换部门和职位ID为code
        candidateData.setDepartmentCode(
            convertDepartmentIdToCode(domainModel.getDepartmentId())
        );
        candidateData.setPositionCode(
            convertPositionIdToCode(domainModel.getPositionId())
        );
        
        candidateData.setHiringDate(domainModel.getHireDate().format(DATE_FORMATTER));
        
        log.info("内部数据转换完成");
        return candidateData;
    }
    
    /**
     * 转换性别：M/F -> Gender枚举
     */
    private Gender convertGender(String sex) {
        return switch (sex.toUpperCase()) {
            case "M" -> Gender.MALE;
            case "F" -> Gender.FEMALE;
            default -> Gender.OTHER;
        };
    }
    
    /**
     * 转换性别：Gender枚举 -> M/F
     */
    private String convertGenderToExternal(Gender gender) {
        return switch (gender) {
            case MALE -> "M";
            case FEMALE -> "F";
            case OTHER -> "O";
        };
    }
    
    /**
     * 将部门代码转换为部门ID
     * 实际实现中应该查询部门仓储
     */
    private DepartmentId convertDepartmentCode(String departmentCode) {
        // 简化实现：这里应该通过DepartmentRepository查询
        // 示例：根据code查找对应的ID
        log.debug("转换部门代码: {} -> 部门ID", departmentCode);
        return DepartmentId.of("dept-" + departmentCode);
    }
    
    /**
     * 将职位代码转换为职位ID
     * 实际实现中应该查询职位仓储
     */
    private PositionId convertPositionCode(String positionCode) {
        // 简化实现：这里应该通过PositionRepository查询
        log.debug("转换职位代码: {} -> 职位ID", positionCode);
        return PositionId.of("pos-" + positionCode);
    }
    
    /**
     * 将部门ID转换为部门代码
     */
    private String convertDepartmentIdToCode(DepartmentId departmentId) {
        // 简化实现：实际应该查询部门信息
        return departmentId.getValue().replace("dept-", "");
    }
    
    /**
     * 将职位ID转换为职位代码
     */
    private String convertPositionIdToCode(PositionId positionId) {
        // 简化实现：实际应该查询职位信息
        return positionId.getValue().replace("pos-", "");
    }
    
}


