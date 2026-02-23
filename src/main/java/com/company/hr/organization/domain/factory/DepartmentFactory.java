package com.company.hr.organization.domain.factory;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.organization.domain.model.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门工厂
 * 负责创建部门聚合根和职位实体
 */
@Component
public class DepartmentFactory {
    
    /**
     * 创建标准部门
     * 
     * @param name 部门名称
     * @param code 部门编码
     * @param type 部门类型
     * @param parentId 父部门ID（可为null）
     * @param managerId 部门经理ID
     * @param description 描述
     * @return 新创建的部门
     */
    public Department createDepartment(
            String name,
            String code,
            DepartmentType type,
            DepartmentId parentId,
            EmployeeId managerId,
            String description) {
        
        // 生成部门ID
        DepartmentId departmentId = DepartmentId.generate();
        
        // 创建部门聚合根
        Department department = new Department(
            departmentId,
            name,
            code,
            type,
            parentId,
            managerId,
            description
        );
        
        return department;
    }
    
    /**
     * 创建带有初始职位的部门
     * 
     * @param name 部门名称
     * @param code 部门编码
     * @param type 部门类型
     * @param parentId 父部门ID
     * @param managerId 部门经理ID
     * @param description 描述
     * @param initialPositions 初始职位列表
     * @return 带有职位的部门
     */
    public Department createDepartmentWithPositions(
            String name,
            String code,
            DepartmentType type,
            DepartmentId parentId,
            EmployeeId managerId,
            String description,
            List<PositionCreationData> initialPositions) {
        
        // 创建部门
        Department department = createDepartment(
            name, code, type, parentId, managerId, description
        );
        
        // 添加初始职位
        if (initialPositions != null) {
            initialPositions.forEach(data -> {
                Position position = createPosition(
                    data.getTitle(),
                    data.getCode(),
                    data.getLevel(),
                    data.getMinSalary(),
                    data.getMaxSalary(),
                    data.getDescription(),
                    data.getMaxHeadcount()
                );
                department.addPosition(position);
            });
        }
        
        return department;
    }
    
    /**
     * 创建职位
     * 
     * @param title 职位名称
     * @param code 职位编码
     * @param level 职位级别
     * @param minSalary 最低薪资
     * @param maxSalary 最高薪资
     * @param description 描述
     * @param maxHeadcount 最大编制
     * @return 新创建的职位
     */
    public Position createPosition(
            String title,
            String code,
            PositionLevel level,
            BigDecimal minSalary,
            BigDecimal maxSalary,
            String description,
            int maxHeadcount) {
        
        // 生成职位ID
        PositionId positionId = PositionId.generate();
        
        // 创建职位实体
        Position position = new Position(
            positionId,
            title,
            code,
            level,
            minSalary,
            maxSalary,
            description,
            maxHeadcount
        );
        
        return position;
    }
    
    /**
     * 创建标准的技术部门（包含常见职位）
     * 
     * @param name 部门名称
     * @param code 部门编码
     * @param managerId 部门经理ID
     * @return 带有标准技术职位的部门
     */
    public Department createTechnicalDepartment(
            String name,
            String code,
            EmployeeId managerId) {
        
        Department department = createDepartment(
            name, code, DepartmentType.DEPARTMENT, null, managerId, "技术部门"
        );
        
        // 添加标准技术职位
        List<PositionCreationData> positions = new ArrayList<>();
        
        // 初级工程师
        positions.add(new PositionCreationData(
            "初级工程师", code + "-JUNIOR", PositionLevel.JUNIOR,
            new BigDecimal("8000"), new BigDecimal("12000"),
            "初级技术岗位", 5
        ));
        
        // 中级工程师
        positions.add(new PositionCreationData(
            "中级工程师", code + "-MID", PositionLevel.INTERMEDIATE,
            new BigDecimal("12000"), new BigDecimal("18000"),
            "中级技术岗位", 3
        ));
        
        // 高级工程师
        positions.add(new PositionCreationData(
            "高级工程师", code + "-SENIOR", PositionLevel.SENIOR,
            new BigDecimal("18000"), new BigDecimal("30000"),
            "高级技术岗位", 2
        ));
        
        // 技术专家
        positions.add(new PositionCreationData(
            "技术专家", code + "-EXPERT", PositionLevel.EXPERT,
            new BigDecimal("30000"), new BigDecimal("50000"),
            "专家级技术岗位", 1
        ));
        
        positions.forEach(data -> {
            Position position = createPosition(
                data.getTitle(), data.getCode(), data.getLevel(),
                data.getMinSalary(), data.getMaxSalary(),
                data.getDescription(), data.getMaxHeadcount()
            );
            department.addPosition(position);
        });
        
        return department;
    }
    
    /**
     * 职位创建数据（用于批量创建职位）
     */
    public static class PositionCreationData {
        private final String title;
        private final String code;
        private final PositionLevel level;
        private final BigDecimal minSalary;
        private final BigDecimal maxSalary;
        private final String description;
        private final int maxHeadcount;
        
        public PositionCreationData(String title, String code, PositionLevel level,
                                   BigDecimal minSalary, BigDecimal maxSalary,
                                   String description, int maxHeadcount) {
            this.title = title;
            this.code = code;
            this.level = level;
            this.minSalary = minSalary;
            this.maxSalary = maxSalary;
            this.description = description;
            this.maxHeadcount = maxHeadcount;
        }
        
        public String getTitle() { return title; }
        public String getCode() { return code; }
        public PositionLevel getLevel() { return level; }
        public BigDecimal getMinSalary() { return minSalary; }
        public BigDecimal getMaxSalary() { return maxSalary; }
        public String getDescription() { return description; }
        public int getMaxHeadcount() { return maxHeadcount; }
    }
}


