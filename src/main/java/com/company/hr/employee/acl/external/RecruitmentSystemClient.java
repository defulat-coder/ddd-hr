package com.company.hr.employee.acl.external;

import lombok.Data;

/**
 * 外部招聘系统客户端
 * 模拟第三方招聘系统的API调用
 */
public interface RecruitmentSystemClient {
    
    /**
     * 从招聘系统获取候选人信息
     */
    CandidateData getCandidateById(String candidateId);
    
    /**
     * 更新候选人状态为已录用
     */
    void updateCandidateStatus(String candidateId, String status);
    
    /**
     * 外部招聘系统的候选人数据模型
     */
    @Data
    class CandidateData {
        private String id;
        private String fullName;
        private String firstName;
        private String lastName;
        private String idNumber;
        private String birthDateStr; // 外部系统使用字符串格式
        private String sex; // 外部系统使用 M/F
        private String emailAddress;
        private String mobile;
        private String homeAddress;
        private String emergencyContactName;
        private String emergencyContactPhone;
        private String departmentCode;
        private String positionCode;
        private String hiringDate;
    }
}

