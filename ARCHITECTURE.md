# HR系统DDD架构设计文档

## 1. 限界上下文划分

### 1.1 上下文映射图

```
┌──────────────────────────────────────────────────────────────┐
│                     HR 人力资源系统                            │
└──────────────────────────────────────────────────────────────┘

┌─────────────────┐        ┌─────────────────┐
│   员工中心       │◄──────►│   组织管理       │
│  (Employee)     │  依赖   │ (Organization)  │
│   核心域        │        │   支撑域         │
└────────┬────────┘        └─────────────────┘
         │
         │ 发布事件
         ▼
┌─────────────────┐        ┌─────────────────┐
│  目标与绩效      │        │   福利管理       │
│ (Performance)   │        │   (Benefit)     │
│   核心域        │        │   支撑域         │
└─────────────────┘        └─────────────────┘
         │
         │ 发布事件
         ▼
┌─────────────────┐
│   企业文化       │
│   (Culture)     │
│   通用域        │
└─────────────────┘
```

### 1.2 上下文关系

- **员工中心 → 组织管理**: 员工需要引用部门和职位
- **员工中心 → 目标与绩效**: 通过领域事件通知员工变更
- **员工中心 → 福利管理**: 通过领域事件通知员工入职/离职
- **员工中心 → 企业文化**: 通过领域事件通知员工状态

## 2. 领域模型设计

### 2.1 员工中心 (Employee Context)

#### 聚合根: Employee

```java
Employee (聚合根)
├── EmployeeId (值对象)
├── PersonalInfo (值对象)
│   ├── firstName
│   ├── lastName
│   ├── idCardNumber
│   ├── birthDate
│   └── gender
├── ContactInfo (值对象)
│   ├── email
│   ├── phoneNumber
│   ├── address
│   ├── emergencyContact
│   └── emergencyPhone
├── DepartmentId (外部引用)
├── PositionId (外部引用)
├── EmployeeStatus (枚举)
└── 关键方法:
    ├── confirmEmployment() - 转正
    ├── transfer() - 调动
    ├── resign() - 离职
    └── updateContactInfo() - 更新联系信息
```

#### 领域事件

- `EmployeeHiredEvent` - 员工入职
- `EmployeeStatusChangedEvent` - 状态变更
- `EmployeeTransferredEvent` - 员工调动
- `EmployeeResignedEvent` - 员工离职

### 2.2 组织管理 (Organization Context)

#### 聚合根: Department

```java
Department (聚合根)
├── DepartmentId (值对象)
├── name
├── code
├── type (DepartmentType枚举)
├── parentId (自引用)
├── managerId (EmployeeId引用)
├── List<Position> (实体集合)
│   └── Position (实体)
│       ├── PositionId
│       ├── title
│       ├── level (PositionLevel枚举)
│       ├── salaryRange
│       └── headcount
└── 关键方法:
    ├── addPosition() - 添加职位
    ├── changeManager() - 更换负责人
    ├── deactivate() - 停用部门
    └── activate() - 启用部门
```

### 2.3 目标与绩效 (Performance Context)

#### 聚合根: Goal (OKR)

```java
Goal (聚合根)
├── GoalId (值对象)
├── EmployeeId (外部引用)
├── title
├── description
├── GoalPeriod (值对象)
│   ├── startDate
│   ├── endDate
│   └── registrationDeadline
├── GoalStatus (枚举)
├── List<Objective> (实体集合)
│   └── Objective (实体)
│       ├── ObjectiveId
│       ├── description
│       ├── keyResult (关键结果)
│       ├── weight (权重)
│       ├── targetValue (目标值)
│       ├── actualValue (实际值)
│       └── status
└── 关键方法:
    ├── addObjective() - 添加目标项
    ├── activate() - 激活目标
    ├── complete() - 完成目标
    └── calculateTotalScore() - 计算总分
```

### 2.4 福利管理 (Benefit Context)

#### 聚合根: Benefit

```java
Benefit (聚合根)
├── BenefitId (值对象)
├── name
├── description
├── BenefitType (枚举)
├── BenefitCost (值对象)
│   ├── employerCost
│   └── employeeCost
├── eligibilityCriteria (资格标准)
├── List<BenefitEnrollment> (实体集合)
│   └── BenefitEnrollment (实体)
│       ├── EnrollmentId
│       ├── EmployeeId
│       ├── enrollmentDate
│       ├── effectiveDate
│       ├── expirationDate
│       └── status
└── 关键方法:
    ├── addEnrollment() - 添加参加记录
    ├── updateInfo() - 更新福利信息
    └── deactivate() - 停用福利
```

### 2.5 企业文化 (Culture Context)

#### 聚合根: CultureActivity

```java
CultureActivity (聚合根)
├── ActivityId (值对象)
├── title
├── description
├── ActivityType (枚举)
├── ActivitySchedule (值对象)
│   ├── startTime
│   ├── endTime
│   └── registrationDeadline
├── location
├── organizerId (EmployeeId引用)
├── maxParticipants
├── budget
├── ActivityStatus (枚举)
├── List<ActivityParticipation> (实体集合)
│   └── ActivityParticipation (实体)
│       ├── ParticipationId
│       ├── EmployeeId
│       ├── registrationTime
│       ├── status
│       ├── feedback
│       └── rating
└── 关键方法:
    ├── openRegistration() - 开放报名
    ├── registerParticipant() - 员工报名
    ├── start() - 开始活动
    ├── complete() - 完成活动
    └── getAverageRating() - 获取平均评分
```

## 3. 分层架构

### 3.1 五层架构（含防腐层）

```
┌─────────────────────────────────────────┐
│         接口层 (Interfaces)              │
│   EmployeeController                     │
│   (REST API, DTO转换)                    │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│        应用层 (Application)              │
│   EmployeeApplicationService             │
│   (用例编排, 事务管理)                    │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│       防腐层 (Anti-Corruption)           │
│   Adapters, Translators, Services        │
│   (外部系统适配, 上下文翻译)              │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│          领域层 (Domain)                 │
│   Employee, Department, Goal...          │
│   (业务逻辑, 领域规则)                    │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│      基础设施层 (Infrastructure)          │
│   EmployeeRepositoryImpl, JPA Entity     │
│   (技术实现, 持久化)                      │
└─────────────────────────────────────────┘
```

### 3.2 各层职责

#### 防腐层 (Anti-Corruption Layer)

**职责**：
- 适配外部系统
- 上下文间翻译
- 保护领域模型
- 模型转换

**示例**：
```java
@Component
public class RecruitmentSystemAdapter 
    implements ExternalSystemAdapter<Employee, CandidateData> {
    
    @Override
    public Employee toDomainModel(CandidateData external) {
        // 将外部招聘系统数据转换为Employee领域模型
        // 处理数据格式差异，保护领域模型不受外部影响
    }
}
```

详细设计请查看：[ACL_DESIGN.md](ACL_DESIGN.md)

#### 接口层 (Interfaces Layer)

**职责**：
- 接收用户请求
- 参数校验
- DTO与领域对象转换
- 返回响应

**示例**：
```java
@RestController
@RequestMapping("/employees")
public class EmployeeController {
    @PostMapping
    public ApiResponse<EmployeeDTO> createEmployee(
        @RequestBody CreateEmployeeCommand command) {
        // 委托给应用服务
    }
}
```

#### 应用层 (Application Layer)

**职责**：
- 用例编排
- 事务边界
- 领域事件发布
- 调用领域服务

**示例**：
```java
@Service
@Transactional
public class EmployeeApplicationService {
    public EmployeeDTO createEmployee(CreateEmployeeCommand command) {
        // 1. 创建领域对象
        // 2. 调用领域方法
        // 3. 保存聚合根
        // 4. 发布领域事件
    }
}
```

#### 领域层 (Domain Layer)

**职责**：
- 业务规则
- 状态管理
- 不变性维护
- 领域事件生成

**示例**：
```java
public class Employee extends AggregateRoot<EmployeeId> {
    public void confirmEmployment() {
        // 业务规则验证
        if (status != EmployeeStatus.PROBATION) {
            throw new DomainException("只有试用期员工才能转正");
        }
        // 状态变更
        this.status = EmployeeStatus.ACTIVE;
        // 发布事件
        registerEvent(new EmployeeStatusChangedEvent(...));
    }
}
```

#### 基础设施层 (Infrastructure Layer)

**职责**：
- 持久化实现
- 外部服务集成
- 消息队列
- 缓存

**示例**：
```java
@Component
public class EmployeeRepositoryImpl implements EmployeeRepository {
    public Employee save(Employee aggregate) {
        // JPA持久化实现
    }
}
```

## 4. 关键设计模式

### 4.1 防腐层模式 (Anti-Corruption Layer Pattern)

**目的**：保护领域模型不受外部系统的"腐蚀"

**实现**：
```java
// 1. 定义适配器接口
public interface ExternalSystemAdapter<T, R> {
    T toDomainModel(R externalModel);
    R toExternalModel(T domainModel);
}

// 2. 实现具体适配器
@Component
public class RecruitmentSystemAdapter 
    implements ExternalSystemAdapter<Employee, CandidateData> {
    // 转换逻辑
}

// 3. 创建防腐服务
@Service
public class RecruitmentSystemService 
    implements AntiCorruptionService {
    public Employee importCandidateAsEmployee(String candidateId) {
        // 1. 获取外部数据
        // 2. 适配器转换
        // 3. 保存到领域
        // 4. 更新外部系统
    }
}
```

**应用场景**：
- 外部系统集成（招聘系统、OA系统）
- 限界上下文通信（员工→福利、员工→绩效）
- 遗留系统迁移

### 4.2 聚合模式 (Aggregate Pattern)

**目的**：定义对象所有权和边界，保证一致性

**实现**：
- 每个聚合有一个聚合根
- 外部只能通过聚合根访问聚合内部
- 聚合内的修改在事务边界内完成

### 4.3 仓储模式 (Repository Pattern)

**目的**：抽象数据访问，使领域层不依赖持久化技术

**实现**：
```java
// 领域层定义接口
public interface EmployeeRepository extends Repository<Employee, EmployeeId> {
    Optional<Employee> findByEmployeeNumber(String number);
}

// 基础设施层实现
@Component
public class EmployeeRepositoryImpl implements EmployeeRepository {
    // JPA实现
}
```

### 4.4 领域事件模式 (Domain Event Pattern)

**目的**：实现限界上下文之间的解耦

**实现**：
```java
// 聚合根中注册事件
public class Employee extends AggregateRoot<EmployeeId> {
    public void resign(LocalDate resignDate) {
        this.status = EmployeeStatus.RESIGNED;
        registerEvent(new EmployeeResignedEvent(getId(), ...));
    }
}

// 应用服务发布事件
employeeRepository.save(employee);
eventPublisher.publish(employee.getDomainEvents());
```

### 4.5 值对象模式 (Value Object Pattern)

**目的**：用不可变对象表达领域概念

**实现**：
```java
@Value  // Lombok生成不可变类
public class PersonalInfo implements ValueObject {
    String firstName;
    String lastName;
    String idCardNumber;
    LocalDate birthDate;
    Gender gender;
    
    // 业务方法
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
```

## 5. 业务规则示例

### 5.1 员工状态转换规则

```
    [试用期] ──转正──> [在职] ──停职──> [停职] ──恢复──> [在职]
       │                │                            │
       │                │                            │
       └────离职────────┴───────离职─────────────────┴──> [离职]
```

**实现**：
```java
public enum EmployeeStatus {
    PROBATION, ACTIVE, SUSPENDED, RESIGNED;
    
    public boolean canTransitionTo(EmployeeStatus newStatus) {
        return switch (this) {
            case PROBATION -> newStatus == ACTIVE || newStatus == RESIGNED;
            case ACTIVE -> newStatus == SUSPENDED || newStatus == RESIGNED;
            case SUSPENDED -> newStatus == ACTIVE || newStatus == RESIGNED;
            case RESIGNED -> false;
        };
    }
}
```

### 5.2 OKR权重规则

- 目标必须包含至少一个目标项
- 所有目标项的权重总和必须等于100%
- 每个目标项的完成率 = 实际值 / 目标值
- 目标项得分 = 权重 × 完成率
- 目标总分 = 所有目标项得分之和

**实现**：
```java
public class Goal extends AggregateRoot<GoalId> {
    public void activate() {
        BigDecimal totalWeight = objectives.stream()
            .map(Objective::getWeight)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalWeight.compareTo(new BigDecimal("100")) != 0) {
            throw new DomainException("目标项权重总和必须等于100%");
        }
        this.status = GoalStatus.ACTIVE;
    }
}
```

### 5.3 活动报名规则

- 报名必须在截止时间前
- 报名人数不能超过最大限制
- 同一员工不能重复报名
- 活动状态必须是"开放报名"

**实现**：
```java
public class CultureActivity extends AggregateRoot<ActivityId> {
    public void registerParticipant(EmployeeId employeeId) {
        if (status != ActivityStatus.REGISTRATION_OPEN) {
            throw new DomainException("活动未开放报名");
        }
        if (!schedule.isRegistrationOpen()) {
            throw new DomainException("报名时间已截止");
        }
        if (getRegistrationCount() >= maxParticipants) {
            throw new DomainException("活动人数已满");
        }
        // ... 添加参与记录
    }
}
```

## 6. 扩展性设计

### 6.1 领域事件发布订阅

```java
// 事件发布者
@Component
public class DomainEventPublisher {
    public void publish(List<DomainEvent> events) {
        events.forEach(this::publishEvent);
    }
}

// 事件订阅者
@Component
public class PerformanceEventHandler {
    @EventListener
    public void handle(EmployeeResignedEvent event) {
        // 取消该员工的所有活跃目标
    }
}
```

### 6.2 CQRS支持

可以为查询创建独立的读模型：

```java
// 命令模型（写）
@Service
public class EmployeeApplicationService {
    public void createEmployee(CreateEmployeeCommand cmd) { }
}

// 查询模型（读）
@Service
public class EmployeeQueryService {
    public EmployeeDetailDTO getEmployeeDetail(String id) { }
    public List<EmployeeSummaryDTO> searchEmployees(SearchCriteria criteria) { }
}
```

## 7. 测试策略

### 7.1 单元测试 - 领域模型

```java
@Test
public void should_confirm_employment_when_probation_period_ends() {
    // Given
    Employee employee = createEmployeeInProbation();
    
    // When
    employee.confirmEmployment();
    
    // Then
    assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
    assertThat(employee.getDomainEvents()).hasSize(1);
}
```

### 7.2 集成测试 - 应用服务

```java
@SpringBootTest
@Transactional
public class EmployeeApplicationServiceTest {
    @Test
    public void should_create_employee_successfully() {
        // Given
        CreateEmployeeCommand command = new CreateEmployeeCommand();
        
        // When
        EmployeeDTO result = employeeService.createEmployee(command);
        
        // Then
        assertThat(result.getEmployeeNumber()).isNotNull();
    }
}
```

## 8. 总结

本系统采用DDD设计，具有以下优势：

1. **业务逻辑集中**：核心业务逻辑在领域模型中，易于理解和维护
2. **高内聚低耦合**：限界上下文明确，通过领域事件解耦
3. **可测试性强**：领域模型独立于基础设施，易于单元测试
4. **扩展性好**：分层架构和DDD模式支持业务演进
5. **代码质量高**：充血模型避免贫血模型的问题

通过严格遵循DDD的战略设计和战术设计原则，我们构建了一个清晰、可维护、可扩展的HR系统。

