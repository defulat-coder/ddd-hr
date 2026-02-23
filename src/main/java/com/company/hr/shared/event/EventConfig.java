package com.company.hr.shared.event;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 事件配置
 * 启用异步事件处理
 */
@Configuration
@EnableAsync
public class EventConfig {
    // 事件配置类
    // Spring会自动处理@EventListener和@Async注解
}

