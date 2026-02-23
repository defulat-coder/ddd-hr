package com.cy.hr.shared.interfaces.config;

/**
 * 文件说明：OpenApiConfig
 */
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * OpenAPI 基础信息。
     */
    @Bean
    public OpenAPI hrOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("DDD HR API")
                        .description("HR系统接口文档")
                        .version("v1.0.0")
                        .contact(new Contact().name("HR Team")));
    }

    /**
     * 按 /api 路径分组展示接口。
     */
    @Bean
    public GroupedOpenApi hrApiGroup() {
        return GroupedOpenApi.builder()
                .group("hr-api")
                .pathsToMatch("/api/**")
                .build();
    }
}
