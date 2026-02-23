package com.company.hr.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI hrOpenApi() {
        return new OpenAPI().info(new Info()
            .title("HR System API")
            .description("DDD HR 系统接口文档")
            .version("1.0.0")
            .contact(new Contact().name("HR Team").email("hr@example.com"))
            .license(new License().name("Apache 2.0")));
    }
}

