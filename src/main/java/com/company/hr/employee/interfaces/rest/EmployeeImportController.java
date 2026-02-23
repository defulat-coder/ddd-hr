package com.company.hr.employee.interfaces.rest;

import com.company.hr.employee.acl.external.RecruitmentSystemService;
import com.company.hr.employee.application.dto.EmployeeDTO;
import com.company.hr.employee.domain.model.Employee;
import com.company.hr.infrastructure.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 员工导入控制器
 * 展示防腐层的使用：从外部招聘系统导入员工
 */
@RestController
@RequestMapping("/employees/import")
@RequiredArgsConstructor
public class EmployeeImportController {
    
    private final RecruitmentSystemService recruitmentSystemService;
    
    /**
     * 从招聘系统导入候选人为员工
     * 通过防腐层处理外部系统集成
     */
    @PostMapping("/from-recruitment/{candidateId}")
    public ApiResponse<EmployeeDTO> importFromRecruitment(
            @PathVariable String candidateId) {
        
        // 通过防腐层服务导入，领域模型不会被外部系统污染
        Employee employee = recruitmentSystemService
            .importCandidateAsEmployee(candidateId);
        
        EmployeeDTO dto = EmployeeDTO.fromDomain(employee);
        return ApiResponse.success(dto, "从招聘系统导入员工成功");
    }
}

