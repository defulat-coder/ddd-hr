package com.company.hr.shared.event;

import java.lang.annotation.*;

/**
 * 领域事件处理器注解
 * 标记类为领域事件处理器
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainEventHandler {
    
    /**
     * 处理器名称
     */
    String value() default "";
    
    /**
     * 处理器描述
     */
    String description() default "";
}

