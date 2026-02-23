package com.company.hr.employee.acl.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 招聘系统客户端实现
 * 模拟外部招聘系统的API调用
 */
@Component
@Slf4j
public class RecruitmentSystemClientImpl implements RecruitmentSystemClient {
    
    @Override
    public CandidateData getCandidateById(String candidateId) {
        log.info("调用外部招聘系统API获取候选人: {}", candidateId);
        
        // 模拟外部API调用，返回模拟数据
        CandidateData candidateData = new CandidateData();
        candidateData.setId(candidateId);
        candidateData.setFirstName("李");
        candidateData.setLastName("四");
        candidateData.setFullName("李四");
        candidateData.setIdNumber("110101199101011234");
        candidateData.setBirthDateStr("1991-01-01");
        candidateData.setSex("M");
        candidateData.setEmailAddress("lisi@example.com");
        candidateData.setMobile("13900139000");
        candidateData.setHomeAddress("北京市海淀区");
        candidateData.setEmergencyContactName("李五");
        candidateData.setEmergencyContactPhone("13900139001");
        candidateData.setDepartmentCode("TECH");
        candidateData.setPositionCode("DEV");
        candidateData.setHiringDate("2024-12-01");
        
        log.info("成功获取候选人数据: {}", candidateData.getFullName());
        return candidateData;
    }
    
    @Override
    public void updateCandidateStatus(String candidateId, String status) {
        log.info("更新候选人状态: {} -> {}", candidateId, status);
        
        // 模拟外部API调用
        // 实际实现中这里会调用HTTP API或RPC
        
        log.info("候选人状态更新成功");
    }
}

