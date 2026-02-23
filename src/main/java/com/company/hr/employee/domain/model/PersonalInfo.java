package com.company.hr.employee.domain.model;

import com.company.hr.shared.domain.ValueObject;
import lombok.Value;

import java.time.LocalDate;

/**
 * 个人信息值对象
 */
@Value
public class PersonalInfo implements ValueObject {
    String firstName;
    String lastName;
    String idCardNumber;
    LocalDate birthDate;
    Gender gender;
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public int getAge() {
        return LocalDate.now().getYear() - birthDate.getYear();
    }
}

