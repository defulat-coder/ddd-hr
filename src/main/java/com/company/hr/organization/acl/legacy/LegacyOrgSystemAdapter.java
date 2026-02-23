package com.company.hr.organization.acl.legacy;

import com.company.hr.organization.domain.model.Department;
import com.company.hr.organization.domain.model.DepartmentId;
import com.company.hr.organization.domain.model.DepartmentType;
import com.company.hr.shared.acl.ExternalSystemAdapter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 遗留组织系统适配器
 * 防腐层：适配旧的组织架构系统到新的领域模型
 */
@Component
@Slf4j
public class LegacyOrgSystemAdapter implements 
    ExternalSystemAdapter<Department, LegacyOrgSystemAdapter.LegacyDepartmentData> {
    
    @Override
    public Department toDomainModel(LegacyDepartmentData externalModel) {
        log.info("将遗留系统部门数据转换为领域模型: {}", externalModel.getDeptId());
        
        // 转换部门类型：旧系统使用数字代码
        DepartmentType type = convertDepartmentType(externalModel.getDeptType());
        
        // 转换父部门ID
        DepartmentId parentId = externalModel.getParentDeptId() != null 
            ? DepartmentId.of(externalModel.getParentDeptId().toString())
            : null;
        
        // 创建部门聚合根
        Department department = new Department(
            DepartmentId.of(externalModel.getDeptId().toString()),
            externalModel.getDeptName(),
            externalModel.getDeptCode(),
            type,
            parentId,
            null, // managerId暂时为空
            externalModel.getDeptDesc()
        );
        
        log.info("遗留部门数据转换完成: {}", externalModel.getDeptName());
        return department;
    }
    
    @Override
    public LegacyDepartmentData toExternalModel(Department domainModel) {
        log.info("将部门领域模型转换为遗留系统数据: {}", domainModel.getId());
        
        LegacyDepartmentData legacyData = new LegacyDepartmentData();
        legacyData.setDeptId(Long.parseLong(domainModel.getId().getValue()));
        legacyData.setDeptName(domainModel.getName());
        legacyData.setDeptCode(domainModel.getCode());
        legacyData.setDeptType(convertDepartmentTypeToLegacy(domainModel.getType()));
        legacyData.setDeptDesc(domainModel.getDescription());
        legacyData.setStatus(domainModel.isActive() ? 1 : 0);
        
        if (domainModel.getParentId() != null) {
            legacyData.setParentDeptId(
                Long.parseLong(domainModel.getParentId().getValue())
            );
        }
        
        log.info("部门数据转换完成");
        return legacyData;
    }
    
    /**
     * 转换部门类型：数字代码 -> 枚举
     */
    private DepartmentType convertDepartmentType(Integer typeCode) {
        return switch (typeCode) {
            case 1 -> DepartmentType.HEADQUARTERS;
            case 2 -> DepartmentType.BRANCH;
            case 3 -> DepartmentType.DEPARTMENT;
            case 4 -> DepartmentType.TEAM;
            default -> DepartmentType.DEPARTMENT;
        };
    }
    
    /**
     * 转换部门类型：枚举 -> 数字代码
     */
    private Integer convertDepartmentTypeToLegacy(DepartmentType type) {
        return switch (type) {
            case HEADQUARTERS -> 1;
            case BRANCH -> 2;
            case DEPARTMENT -> 3;
            case TEAM -> 4;
        };
    }
    
    /**
     * 遗留系统的部门数据结构
     * 注意：这是外部系统的数据结构，与我们的领域模型不同
     */
    @Data
    public static class LegacyDepartmentData {
        private Long deptId;
        private String deptName;
        private String deptCode;
        private Integer deptType; // 1-总部，2-分公司，3-部门，4-团队
        private Long parentDeptId;
        private String deptDesc;
        private Integer status; // 1-启用，0-停用
        private Long managerId;
        private String createTime; // 字符串格式的时间
        private String updateTime;
    }
}

