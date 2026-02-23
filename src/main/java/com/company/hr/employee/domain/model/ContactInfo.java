package com.company.hr.employee.domain.model;

import com.company.hr.shared.domain.ValueObject;
import lombok.Value;

/**
 * 联系信息值对象
 */
@Value
public class ContactInfo implements ValueObject {
    String email;
    String phoneNumber;
    String address;
    String emergencyContact;
    String emergencyPhone;
    
    public void validate() {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("无效的邮箱地址");
        }
        if (phoneNumber == null || !phoneNumber.matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("无效的手机号码");
        }
    }
}

