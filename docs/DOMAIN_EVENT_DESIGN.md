# 领域事件发布订阅设计文档

## 概述

本系统实现了完整的**领域事件发布订阅机制**，用于实现限界上下文间的解耦和异步通信。

## 🎯 核心目标

1. **解耦限界上下文**：通过事件实现上下文间的松耦合
2. **异步处理**：事件处理不阻塞主业务流程
3. **可扩展性**：易于添加新的事件处理器
4. **可追溯性**：所有事件都有唯一ID和发生时间

## 📐 架构设计

### 整体架构图

```
┌─────────────────────────────────────────────────────────────┐
│                     应用服务层                                │
│              EmployeeApplicationService                      │
│                       │                                       │
│                       │ 1. 执行业务逻辑                        │
│                       ▼                                       │
│                  Employee.transfer()                         │
│                       │                                       │
│                       │ 2. 注册领域事件                        │
│                       ▼                                       │
│            employee.registerEvent(event)                     │
│                       │                                       │
│                       │ 3. 发布事件                           │
│                       ▼                                       │
│         DomainEventPublisher.publishAll()                    │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        │ Spring ApplicationEventPublisher
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                   Spring 事件总线                             │
└────┬──────────┬──────────┬──────────┬───────────────────────┘
     │          │          │          │
     │          │          │          │ @EventListener
     ▼          ▼          ▼          ▼
┌─────────┐ ┌────────┐ ┌────────┐ ┌─────────┐
│员工事件  │ │绩效事件│ │福利事件│ │文化事件  │
│处理器    │ │处理器  │ │处理器  │ │处理器    │
└─────────┘ └────────┘ └────────┘ └─────────┘
    │            │          │          │
    │ @Async     │ @Async   │ @Async   │ @Async
    ▼            ▼          ▼          ▼
  异步处理    异步处理    异步处理    异步处理
```

## 📦 核心组件

### 1. DomainEvent（领域事件基类）

```java
public abstract class DomainEvent {
    private final String eventId;           // 事件唯一ID
    private final LocalDateTime occurredOn; // 发生时间
    
    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
    }
}
```

**职责**：
- 提供事件唯一标识
- 记录事件发生时间
- 作为所有领域事件的基类

---

### 2. DomainEventPublisher（事件发布器）

```java
@Component
public class DomainEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;
    
    public void publish(DomainEvent event) {
        log.info("发布领域事件: {}", event.getClass().getSimpleName());
        applicationEventPublisher.publishEvent(event);
    }
    
    public void publishAll(List<DomainEvent> events) {
        events.forEach(this::publish);
    }
}
```

**职责**：
- 封装Spring的ApplicationEventPublisher
- 提供统一的事件发布接口
- 记录事件发布日志

**使用方式**：
```java
// 在应用服务中
employee.transfer(...);
employeeRepository.save(employee);

// 发布聚合根产生的所有事件
eventPublisher.publishAll(employee.getDomainEvents());
employee.clearDomainEvents();
```

---

### 3. EventHandler（事件处理器）

使用Spring的`@EventListener`注解监听事件：

```java
@Component
@DomainEventHandler("EmployeeEventHandler")
@Slf4j
public class EmployeeEventHandler {
    
    @EventListener
    @Async
    public void handleEmployeeHired(EmployeeHiredEvent event) {
        log.info("处理员工入职事件: {}", event.getEmployeeNumber());
        // 处理逻辑
    }
}
```

**关键注解**：
- `@Component`：Spring组件
- `@DomainEventHandler`：自定义注解，标记为领域事件处理器
- `@EventListener`：监听特定类型的事件
- `@Async`：异步处理事件

---

## 🔄 事件流转过程

### 完整流程示例：员工调动

#### 1. 用户调用API

```bash
POST /api/employees/transfer
{
  "employeeId": "emp-001",
  "newDepartmentId": "dept-002",
  "newPositionId": "pos-002",
  "reason": "组织架构调整"
}
```

#### 2. 应用服务执行业务逻辑

```java
@Transactional
public void transferEmployee(TransferEmployeeCommand command) {
    // 1. 加载聚合根
    Employee employee = employeeRepository.findById(...).get();
    
    // 2. 执行领域逻辑（会注册事件）
    employee.transfer(...);  // 内部调用 registerEvent()
    
    // 3. 保存聚合根
    employeeRepository.save(employee);
    
    // 4. 发布事件
    eventPublisher.publishAll(employee.getDomainEvents());
    employee.clearDomainEvents();
}
```

#### 3. 领域对象注册事件

```java
public void transfer(...) {
    // 业务逻辑
    this.departmentId = newDepartmentId;
    this.positionId = newPositionId;
    
    // 注册领域事件
    registerEvent(new EmployeeTransferredEvent(
        getId(), oldDepartmentId, newDepartmentId, ...
    ));
}
```

#### 4. 多个上下文响应事件

**员工上下文**：
```java
@EventListener
@Async
public void handleEmployeeTransferred(EmployeeTransferredEvent event) {
    log.info("通知新旧部门");
    notifyOldDepartment(event);
    notifyNewDepartment(event);
}
```

**绩效上下文**：
```java
@EventListener
@Async
public void handleEmployeeTransferred(EmployeeTransferredEvent event) {
    log.info("通知经理进行绩效评估");
    notifyManagerForEvaluation(event);
}
```

**福利上下文**：
```java
@EventListener
@Async
public void handleEmployeeTransferred(EmployeeTransferredEvent event) {
    log.info("更新福利配置");
    updateBenefitConfiguration(event);
}
```

---

## 📋 事件目录

### 员工中心事件

| 事件 | 触发时机 | 订阅者 |
|------|---------|-------|
| `EmployeeHiredEvent` | 员工入职 | 员工、福利、文化 |
| `EmployeeStatusChangedEvent` | 状态变更（转正、停职等） | 员工、绩效、福利 |
| `ProbationExtendedEvent` | 延长试用期 | 员工、绩效 |
| `EmployeeTransferredEvent` | 部门调动 | 员工、绩效、福利 |
| `EmployeePromotedEvent` | 职位晋升 | 员工、绩效、文化 |
| `EmployeeResignedEvent` | 员工离职 | 员工、绩效、福利、文化 |

---

## 🎯 事件处理器详解

### 1. EmployeeEventHandler（员工上下文内）

**职责**：处理员工相关的通用业务

**处理的事件**：
- 入职：发送欢迎邮件、创建账号
- 状态变更：更新权限
- 调动：通知新旧部门
- 晋升：发送祝贺
- 离职：禁用账号、离职手续

**代码位置**：`employee/application/event/EmployeeEventHandler.java`

---

### 2. PerformanceEventHandler（绩效上下文）

**职责**：响应员工事件，调整绩效管理

**业务逻辑**：
```java
@EventListener
@Async
public void handleEmployeeResigned(EmployeeResignedEvent event) {
    // 1. 取消该员工的所有活跃目标
    cancelActiveGoals(event.getEmployeeId());
    
    // 2. 生成最终绩效报告
    generateFinalPerformanceReport(event.getEmployeeId());
}
```

**代码位置**：`performance/application/event/PerformanceEventHandler.java`

---

### 3. BenefitEventHandler（福利上下文）

**职责**：自动化福利管理

**业务逻辑**：
```java
@EventListener
@Async
public void handleEmployeeHired(EmployeeHiredEvent event) {
    // 1. 自动配置基础福利（五险一金、餐补）
    enrollBasicBenefits(event.getEmployeeId());
    
    // 2. 发送福利说明
    sendBenefitIntroduction(event.getEmployeeId());
}

@EventListener
@Async
public void handleEmployeeResigned(EmployeeResignedEvent event) {
    // 终止所有福利
    terminateAllBenefits(event.getEmployeeId(), event.getResignDate());
}
```

**代码位置**：`benefit/application/event/BenefitEventHandler.java`

---

### 4. CultureEventHandler（文化上下文）

**职责**：响应员工事件，管理文化活动

**业务逻辑**：
```java
@EventListener
@Async
public void handleEmployeePromoted(EmployeePromotedEvent event) {
    // 1. 发送晋升祝贺
    sendPromotionCongratulations(event.getEmployeeId());
    
    // 2. 添加到荣誉榜
    addToHonorBoard(event.getEmployeeId(), "晋升");
}
```

**代码位置**：`culture/application/event/CultureEventHandler.java`

---

## 💡 设计亮点

### 1. 异步处理（@Async）

所有事件处理器都使用`@Async`注解，实现异步处理：

**优势**：
- ✅ 不阻塞主业务流程
- ✅ 提高系统响应速度
- ✅ 处理失败不影响主流程

**配置**：
```java
@Configuration
@EnableAsync
public class EventConfig {
    // Spring自动配置异步线程池
}
```

---

### 2. 事务边界清晰

```java
@Transactional
public void transferEmployee(TransferEmployeeCommand command) {
    // 1. 业务逻辑在事务内
    employee.transfer(...);
    employeeRepository.save(employee);
    
    // 2. 事件发布在事务提交后（通过@Async）
    eventPublisher.publishAll(employee.getDomainEvents());
    // 事件处理器在独立的线程中执行
}
```

**注意**：Spring会在事务提交后才真正发布事件。

---

### 3. 解耦限界上下文

通过事件实现上下文间的解耦：

```
员工上下文                绩效上下文
    │                        │
    │ EmployeeResignedEvent  │
    ├────────────────────────>│
    │                        │
    │                   取消活跃目标
    │                   生成绩效报告
    
不直接依赖，通过事件通信
```

---

### 4. 可扩展性

添加新的事件处理器非常简单：

```java
@Component
@Slf4j
public class NewContextEventHandler {
    
    @EventListener
    @Async
    public void handleEmployeeHired(EmployeeHiredEvent event) {
        // 新的业务逻辑
    }
}
```

无需修改现有代码！

---

## 🔍 事件追踪

### 日志输出示例

```
2024-12-01 10:00:00.123 INFO  [http-nio-8080-exec-1] 
    EmployeeApplicationService - 员工调动: emp-001

2024-12-01 10:00:00.234 INFO  [http-nio-8080-exec-1] 
    DomainEventPublisher - 发布领域事件: EmployeeTransferredEvent - event-uuid-123

2024-12-01 10:00:00.345 INFO  [task-1] 
    EmployeeEventHandler - 【事件处理】员工调动: 员工ID=emp-001

2024-12-01 10:00:00.456 INFO  [task-2] 
    PerformanceEventHandler - 【绩效上下文】收到员工调动事件: 员工ID=emp-001

2024-12-01 10:00:00.567 INFO  [task-3] 
    BenefitEventHandler - 【福利上下文】收到员工调动事件: 员工ID=emp-001

2024-12-01 10:00:01.123 INFO  [task-1] 
    EmployeeEventHandler - 【事件完成】员工调动事件处理完成
```

**观察要点**：
- 主线程（http-nio）：执行业务逻辑和发布事件
- 异步线程（task-1/2/3）：并行处理事件
- 完整的事件追踪链路

---

## 📊 项目结构

```
src/main/java/com/company/hr/
│
├── shared/event/                          # 共享事件基础
│   ├── DomainEventPublisher.java         # 事件发布器
│   ├── DomainEventHandler.java           # 事件处理器注解
│   └── EventConfig.java                  # 事件配置
│
├── employee/
│   └── application/event/
│       └── EmployeeEventHandler.java     # 员工事件处理器
│
├── performance/
│   └── application/event/
│       └── PerformanceEventHandler.java  # 绩效事件处理器
│
├── benefit/
│   └── application/event/
│       └── BenefitEventHandler.java      # 福利事件处理器
│
└── culture/
    └── application/event/
        └── CultureEventHandler.java      # 文化事件处理器
```

---

## 🚀 使用指南

### 1. 在领域对象中注册事件

```java
public class Employee extends AggregateRoot<EmployeeId> {
    
    public void transfer(...) {
        // 业务逻辑
        this.departmentId = newDepartmentId;
        
        // 注册事件
        registerEvent(new EmployeeTransferredEvent(...));
    }
}
```

### 2. 在应用服务中发布事件

```java
@Service
@RequiredArgsConstructor
public class EmployeeApplicationService {
    
    private final DomainEventPublisher eventPublisher;
    
    @Transactional
    public void transferEmployee(TransferEmployeeCommand command) {
        employee.transfer(...);
        employeeRepository.save(employee);
        
        // 发布事件
        eventPublisher.publishAll(employee.getDomainEvents());
        employee.clearDomainEvents();
    }
}
```

### 3. 创建事件处理器

```java
@Component
@Slf4j
public class MyEventHandler {
    
    @EventListener
    @Async
    public void handleEvent(EmployeeTransferredEvent event) {
        log.info("处理事件: {}", event.getEmployeeId());
        // 处理逻辑
    }
}
```

---

## ⚠️ 注意事项

### 1. 事务一致性

事件处理器是异步的，不在同一事务中：
- ✅ 主业务失败，事件不会发布
- ⚠️ 事件处理失败，不影响主业务
- 💡 需要考虑补偿机制或重试策略

### 2. 事件顺序

Spring的事件机制不保证处理顺序：
- 多个处理器并发执行
- 如需顺序，考虑使用消息队列

### 3. 性能考虑

- 事件处理器应该快速返回
- 耗时操作考虑分离到后台任务
- 可配置异步线程池大小

---

## 🔧 扩展建议

### 1. 添加消息队列

```java
@Component
public class DomainEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void publish(DomainEvent event) {
        // 发布到消息队列
        rabbitTemplate.convertAndSend("hr.events", event);
    }
}
```

### 2. 添加事件存储

```java
@EventListener
public void storeEvent(DomainEvent event) {
    EventStore store = new EventStore(
        event.getEventId(),
        event.getClass().getSimpleName(),
        event.getOccurredOn(),
        toJson(event)
    );
    eventStoreRepository.save(store);
}
```

### 3. 添加重试机制

```java
@EventListener
@Retryable(maxAttempts = 3)
public void handleEvent(EmployeeHiredEvent event) {
    // 失败自动重试
}
```

---

## 📚 相关文档

- [ARCHITECTURE.md](ARCHITECTURE.md) - 整体架构设计
- [EMPLOYEE_LIFECYCLE.md](EMPLOYEE_LIFECYCLE.md) - 员工业务流程
- [ACL_DESIGN.md](ACL_DESIGN.md) - 防腐层设计

---

**版本**: 1.0.0  
**最后更新**: 2024-12-01  
**实现状态**: ✅ 完成

