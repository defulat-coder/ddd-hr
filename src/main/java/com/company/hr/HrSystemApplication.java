package com.company.hr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * HR系统主应用程序
 * 基于DDD架构的人力资源管理系统
 */
@SpringBootApplication
public class HrSystemApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(HrSystemApplication.class, args);
    }
}

