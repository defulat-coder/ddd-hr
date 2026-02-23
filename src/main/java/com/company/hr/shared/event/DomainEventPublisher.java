package com.company.hr.shared.event;

import com.company.hr.shared.domain.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 领域事件发布器
 * 负责发布领域事件到Spring事件总线
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DomainEventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;
    
    /**
     * 发布单个领域事件
     */
    public void publish(DomainEvent event) {
        log.info("发布领域事件: {} - {}", event.getClass().getSimpleName(), event.getEventId());
        applicationEventPublisher.publishEvent(event);
    }
    
    /**
     * 批量发布领域事件
     */
    public void publishAll(List<DomainEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        
        log.info("批量发布领域事件，数量: {}", events.size());
        events.forEach(this::publish);
    }
}

