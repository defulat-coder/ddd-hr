# 领域事件发布订阅使用示例

## 快速开始

### 示例1：员工入职，自动配置福利

#### 业务场景
当新员工入职时，系统应该：
1. 发送欢迎邮件
2. 创建系统账号
3. 自动配置基础福利（五险一金、餐补）
4. 发送福利说明
5. 邀请参加入职培训

#### 实现方式

**步骤1：员工入职（主业务流程）**
```java
@RestController
public class EmployeeController {
    
    @PostMapping("/employees")
    public ApiResponse<EmployeeDTO> createEmployee(@RequestBody CreateEmployeeCommand command) {
        // 调用应用服务
        EmployeeDTO employee = employeeService.createEmployee(command);
        return ApiResponse.success(employee);
    }
}
```

**步骤2：应用服务执行业务并发布事件**
```java
@Service
public class EmployeeApplicationService {
    
    @Transactional
    public EmployeeDTO createEmployee(CreateEmployeeCommand command) {
        // 1. 创建员工聚合根
        Employee employee = new Employee(...);  // 内部注册EmployeeHiredEvent
        
        // 2. 保存
        employee = employeeRepository.save(employee);
        
        // 3. 发布事件
        eventPublisher.publishAll(employee.getDomainEvents());
        employee.clearDomainEvents();
        
        return EmployeeDTO.fromDomain(employee);
    }
}
```

**步骤3：员工上下文处理自己的事件**
```java
@Component
public class EmployeeEventHandler {
    
    @EventListener
    @Async
    public void handleEmployeeHired(EmployeeHiredEvent event) {
        log.info("【员工上下文】处理入职: {}", event.getEmployeeNumber());
        
        // 发送欢迎邮件
        sendWelcomeEmail(event);
        
        // 创建系统账号
        createSystemAccount(event);
    }
}
```

**步骤4：福利上下文自动响应**
```java
@Component
public class BenefitEventHandler {
    
    @EventListener
    @Async
    public void handleEmployeeHired(EmployeeHiredEvent event) {
        log.info("【福利上下文】处理入职: {}", event.getEmployeeNumber());
        
        // 自动配置基础福利
        enrollBasicBenefits(event.getEmployeeId());
        
        // 发送福利说明
        sendBenefitIntroduction(event.getEmployeeId());
    }
}
```

**步骤5：文化上下文自动响应**
```java
@Component
public class CultureEventHandler {
    
    @EventListener
    @Async
    public void handleEmployeeHired(EmployeeHiredEvent event) {
        log.info("【文化上下文】处理入职: {}", event.getEmployeeNumber());
        
        // 自动报名入职培训
        enrollOnboardingTraining(event.getEmployeeId());
        
        // 发送文化活动邀请
        sendCultureActivityInvitation(event.getEmployeeId());
    }
}
```

#### 日志输出

```
[http-nio-8080-exec-1] EmployeeApplicationService - 创建员工: 张三
[http-nio-8080-exec-1] DomainEventPublisher - 发布领域事件: EmployeeHiredEvent
[task-1] EmployeeEventHandler - 【员工上下文】处理入职: 2024000001
[task-2] BenefitEventHandler - 【福利上下文】处理入职: 2024000001
[task-3] CultureEventHandler - 【文化上下文】处理入职: 2024000001
[task-1] EmployeeEventHandler - 发送欢迎邮件: 2024000001
[task-2] BenefitEventHandler - 自动配置基础福利: emp-001
[task-3] CultureEventHandler - 自动报名入职培训: emp-001
```

---

## 示例2：员工离职，自动清理资源

#### 业务场景
当员工离职时，系统应该：
1. 禁用系统账号
2. 取消所有活跃的绩效目标
3. 终止所有福利
4. 取消未来活动报名
5. 生成绩效和福利结算报告

#### API调用

```bash
POST /api/employees/resign
{
  "employeeId": "emp-001",
  "resignDate": "2024-12-31",
  "reason": "个人发展原因",
  "resignType": "RESIGNATION"
}
```

#### 事件流转

```
API请求
  ↓
EmployeeApplicationService.resignEmployee()
  ↓
Employee.resign() → 注册 EmployeeResignedEvent
  ↓
DomainEventPublisher.publishAll()
  ↓
┌────────────┬────────────┬────────────┬────────────┐
│            │            │            │            │
▼            ▼            ▼            ▼            ▼
员工处理器   绩效处理器   福利处理器   文化处理器
│            │            │            │
▼            ▼            ▼            ▼
禁用账号    取消目标    终止福利    取消活动
离职手续    绩效报告    福利结算    安排欢送
```

#### 处理器实现

**员工上下文**：
```java
@EventListener
@Async
public void handleEmployeeResigned(EmployeeResignedEvent event) {
    // 1. 禁用系统账号
    disableSystemAccount(event.getEmployeeNumber());
    
    // 2. 启动离职流程
    initiateResignationProcess(event);
    
    // 3. 通知团队成员
    notifyTeamMembers(event);
    
    // 4. 安排离职面谈
    scheduleExitInterview(event);
}
```

**绩效上下文**：
```java
@EventListener
@Async
public void handleEmployeeResigned(EmployeeResignedEvent event) {
    // 1. 取消所有活跃目标
    List<Goal> activeGoals = goalRepository.findActiveGoalsByEmployeeId(
        EmployeeId.of(event.getEmployeeId().getValue())
    );
    activeGoals.forEach(goal -> {
        goal.cancel();
        goalRepository.save(goal);
    });
    
    // 2. 生成最终绩效报告
    generateFinalPerformanceReport(event.getEmployeeId());
}
```

**福利上下文**：
```java
@EventListener
@Async
public void handleEmployeeResigned(EmployeeResignedEvent event) {
    // 1. 终止所有福利
    List<Benefit> benefits = benefitRepository.findByEmployeeId(
        EmployeeId.of(event.getEmployeeId().getValue())
    );
    benefits.forEach(benefit -> {
        benefit.terminate(event.getResignDate());
        benefitRepository.save(benefit);
    });
    
    // 2. 生成福利结算报告
    generateBenefitSettlementReport(event.getEmployeeId());
}
```

**文化上下文**：
```java
@EventListener
@Async
public void handleEmployeeResigned(EmployeeResignedEvent event) {
    // 1. 取消未来活动报名
    List<CultureActivity> activities = activityRepository.findByParticipantId(
        EmployeeId.of(event.getEmployeeId().getValue())
    );
    activities.forEach(activity -> {
        activity.cancelParticipation(event.getEmployeeId());
        activityRepository.save(activity);
    });
    
    // 2. 如果是正常离职，安排欢送会
    if ("RESIGNATION".equals(event.getResignType())) {
        scheduleFarewellEvent(event.getEmployeeId());
    }
}
```

---

## 示例3：员工调动，多上下文协作

#### 业务场景
员工从技术一部调动到技术二部时：
- 通知原部门经理和新部门经理
- 原部门经理进行调动前绩效评估
- 新部门经理设置新的工作目标
- 更新工位和权限
- 调整部门福利配置

#### API调用

```bash
POST /api/employees/transfer
{
  "employeeId": "emp-001",
  "newDepartmentId": "dept-002",
  "newPositionId": "pos-002",
  "reason": "组织架构调整"
}
```

#### 完整流程

**1. 员工上下文处理**：
```java
@EventListener
@Async
public void handleEmployeeTransferred(EmployeeTransferredEvent event) {
    // 通知原部门
    notifyDepartment(event.getOldDepartmentId(), 
        "员工 " + event.getEmployeeId() + " 已调离本部门");
    
    // 通知新部门
    notifyDepartment(event.getNewDepartmentId(), 
        "欢迎新员工 " + event.getEmployeeId() + " 加入");
    
    // 更新工位
    updateWorkstation(event.getEmployeeId(), event.getNewDepartmentId());
    
    // 转移权限
    transferPermissions(event.getEmployeeId(), 
        event.getOldDepartmentId(), event.getNewDepartmentId());
}
```

**2. 绩效上下文处理**：
```java
@EventListener
@Async
public void handleEmployeeTransferred(EmployeeTransferredEvent event) {
    // 通知原经理进行调动前评估
    Employee oldManager = getManagerByDepartment(event.getOldDepartmentId());
    sendNotification(oldManager, 
        "请对调动员工 " + event.getEmployeeId() + " 进行评估");
    
    // 通知新经理设置新目标
    Employee newManager = getManagerByDepartment(event.getNewDepartmentId());
    sendNotification(newManager, 
        "请为新员工 " + event.getEmployeeId() + " 设置工作目标");
}
```

**3. 福利上下文处理**：
```java
@EventListener
@Async
public void handleEmployeeTransferred(EmployeeTransferredEvent event) {
    // 检查新部门是否有特殊福利
    List<Benefit> deptBenefits = benefitRepository
        .findByDepartmentId(event.getNewDepartmentId());
    
    if (!deptBenefits.isEmpty()) {
        // 自动参加新部门的特殊福利
        deptBenefits.forEach(benefit -> {
            benefit.addEnrollment(createEnrollment(event.getEmployeeId()));
            benefitRepository.save(benefit);
        });
    }
}
```

---

## 示例4：员工晋升，触发庆祝流程

#### API调用

```bash
POST /api/employees/promote
{
  "employeeId": "emp-001",
  "newPositionId": "pos-senior-001",
  "reason": "工作表现优秀，晋升为高级工程师"
}
```

#### 事件处理

**员工上下文**：
```java
@EventListener
@Async
public void handleEmployeePromoted(EmployeePromotedEvent event) {
    // 发送晋升祝贺邮件
    sendPromotionEmail(event.getEmployeeId(), 
        event.getOldPositionId(), event.getNewPositionId());
    
    // 更新职级信息
    updatePositionLevel(event.getEmployeeId());
}
```

**绩效上下文**：
```java
@EventListener
@Async
public void handleEmployeePromoted(EmployeePromotedEvent event) {
    // 通知HR调整薪资
    notifyHRForSalaryAdjustment(event.getEmployeeId(), 
        event.getNewPositionId());
    
    // 设置新职级的绩效目标模板
    applyPerformanceTemplate(event.getEmployeeId(), 
        event.getNewPositionId());
}
```

**文化上下文**：
```java
@EventListener
@Async
public void handleEmployeePromoted(EmployeePromotedEvent event) {
    // 全公司发送晋升通知
    broadcastPromotion(event.getEmployeeId(), 
        "祝贺 " + getEmployeeName(event.getEmployeeId()) + " 晋升！");
    
    // 添加到公司荣誉榜
    addToHonorBoard(event.getEmployeeId(), "晋升", 
        event.getReason());
}
```

---

## 完整的事件处理器列表

### 员工事件处理器（EmployeeEventHandler）

| 事件 | 处理逻辑 |
|------|---------|
| EmployeeHiredEvent | 发送欢迎邮件、创建账号、通知HR |
| EmployeeStatusChangedEvent | 更新权限、通知系统 |
| ProbationExtendedEvent | 通知员工和经理、更新HR系统 |
| EmployeeTransferredEvent | 通知新旧部门、更新工位、转移权限 |
| EmployeePromotedEvent | 发送祝贺、更新职级、通知HR调薪 |
| EmployeeResignedEvent | 禁用账号、离职手续、通知团队 |

### 绩效事件处理器（PerformanceEventHandler）

| 事件 | 处理逻辑 |
|------|---------|
| EmployeeResignedEvent | 取消活跃目标、生成绩效报告 |
| EmployeeTransferredEvent | 通知经理评估、设置新目标 |
| EmployeeStatusChangedEvent | 初始化正式员工绩效体系 |

### 福利事件处理器（BenefitEventHandler）

| 事件 | 处理逻辑 |
|------|---------|
| EmployeeHiredEvent | 配置基础福利、发送福利说明 |
| EmployeeStatusChangedEvent | 开放正式员工福利 |
| EmployeeResignedEvent | 终止所有福利、生成结算报告 |

### 文化事件处理器（CultureEventHandler）

| 事件 | 处理逻辑 |
|------|---------|
| EmployeeHiredEvent | 报名入职培训、发送活动邀请 |
| EmployeePromotedEvent | 发送祝贺、添加到荣誉榜 |
| EmployeeResignedEvent | 取消活动报名、安排欢送会 |

---

## 测试事件发布订阅

### 方式1：通过API测试

```bash
# 1. 创建员工（触发EmployeeHiredEvent）
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{...}'

# 2. 观察日志
# 会看到多个处理器异步处理事件

# 3. 员工调动（触发EmployeeTransferredEvent）
curl -X POST http://localhost:8080/api/employees/transfer \
  -H "Content-Type: application/json" \
  -d '{...}'
```

### 方式2：单元测试

```java
@SpringBootTest
class EventPublishTest {
    
    @Autowired
    private EmployeeApplicationService employeeService;
    
    @Test
    void should_publish_hired_event_when_create_employee() {
        // Given
        CreateEmployeeCommand command = new CreateEmployeeCommand();
        // ...设置参数
        
        // When
        EmployeeDTO employee = employeeService.createEmployee(command);
        
        // Then
        // 事件会被异步处理
        // 可以通过日志验证或使用测试事件监听器
    }
}
```

---

## 相关文档

- [DOMAIN_EVENT_DESIGN.md](DOMAIN_EVENT_DESIGN.md) - 详细设计文档
- [EMPLOYEE_LIFECYCLE.md](EMPLOYEE_LIFECYCLE.md) - 员工业务流程
- [ARCHITECTURE.md](ARCHITECTURE.md) - 整体架构

---

**最后更新**: 2024-12-01

