package com.company.hr.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA配置
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.company.hr")
@EnableTransactionManagement
public class JpaConfig {
}

