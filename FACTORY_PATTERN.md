# 工厂模式设计文档

## 概述

在DDD中，**工厂（Factory）**是一个核心的战术模式，用于封装复杂对象的创建逻辑。工厂确保创建的对象处于有效状态，并将创建逻辑与业务逻辑分离。

## 为什么需要工厂？

### 1. 封装复杂的创建逻辑
聚合根的创建往往涉及多个步骤：
- 生成唯一标识
- 验证数据有效性
- 初始化默认值
- 创建关联的值对象或实体

### 2. 确保对象有效性
工厂可以在对象创建时进行验证，确保不会创建处于无效状态的对象。

### 3. 提供多种创建方式
工厂可以提供不同的工厂方法，支持不同的创建场景：
- 标准创建
- 从外部系统导入
- 从持久化恢复（reconstitute）

### 4. 隔离创建逻辑
将创建逻辑从业务逻辑中分离，使领域模型更加专注于业务行为。

## 架构设计

```
┌─────────────────────────────────────────────────────────┐
│                  Application Service                     │
│                                                          │
│  - 协调工厂和仓储                                         │
│  - 不直接调用聚合根构造函数                                │
└──────────────┬──────────────────────────────────────────┘
               │
               ├─────────────────┐
               │                 │
               ▼                 ▼
    ┌──────────────────┐  ┌──────────────────┐
    │     Factory      │  │   Repository     │
    │                  │  │                  │
    │  创建新对象       │  │  持久化/查询     │
    │  重建对象         │  │                  │
    └──────────────────┘  └──────────────────┘
               │
               ▼
    ┌──────────────────┐
    │ Aggregate Root   │
    │                  │
    │  业务逻辑         │
    │  领域行为         │
    └──────────────────┘
```

## 工厂实现

### 1. 员工工厂（EmployeeFactory）

负责创建和重建员工聚合根。

#### 核心方法

##### 1.1 标准入职创建
```java
public Employee createEmployee(
    PersonalInfo personalInfo,
    ContactInfo contactInfo,
    DepartmentId departmentId,
    PositionId positionId,
    LocalDate hireDate)
```

**职责：**
- 生成员工ID
- 生成员工号
- 验证联系信息
- 创建员工聚合根（包含试用期）

**使用场景：** 正常员工入职流程

##### 1.2 特殊人才创建
```java
public Employee createSpecialTalentEmployee(
    PersonalInfo personalInfo,
    ContactInfo contactInfo,
    DepartmentId departmentId,
    PositionId positionId,
    LocalDate hireDate,
    String reason)
```

**职责：**
- 创建标准员工
- 立即转正（无试用期）
- 记录特殊原因

**使用场景：** 高级人才引进，无需试用期

##### 1.3 从外部系统导入
```java
public Employee createFromRecruitmentSystem(
    CandidateData candidateData,
    RecruitmentSystemAdapter adapter)
```

**职责：**
- 通过防腐层适配器转换数据
- 使用转换后的数据创建员工
- 隔离外部系统依赖

**使用场景：** 从招聘系统导入候选人

##### 1.4 重建聚合根
```java
public Employee reconstitute(
    EmployeeId id,
    String employeeNumber,
    PersonalInfo personalInfo,
    ContactInfo contactInfo,
    DepartmentId departmentId,
    PositionId positionId,
    EmployeeStatus status,
    LocalDate hireDate,
    LocalDate probationEndDate,
    LocalDate resignDate)
```

**职责：**
- 从持久化数据重建完整的员工对象
- 恢复所有状态（状态、日期等）
- 不执行业务验证

**使用场景：** 从数据库加载员工

### 2. 部门工厂（DepartmentFactory）

负责创建部门聚合根和职位实体。

#### 核心方法

##### 2.1 创建标准部门
```java
public Department createDepartment(
    String name,
    String code,
    DepartmentType type,
    DepartmentId parentId,
    EmployeeId managerId,
    String description)
```

##### 2.2 创建带职位的部门
```java
public Department createDepartmentWithPositions(
    String name,
    String code,
    DepartmentType type,
    DepartmentId parentId,
    EmployeeId managerId,
    String description,
    List<PositionCreationData> initialPositions)
```

**特点：** 支持批量创建初始职位

##### 2.3 创建标准技术部门
```java
public Department createTechnicalDepartment(
    String name,
    String code,
    EmployeeId managerId)
```

**特点：** 预设技术部门的标准职位（初级/中级/高级/专家工程师）

### 3. 目标工厂（GoalFactory）

负责创建OKR目标聚合根。

#### 核心方法

##### 3.1 创建年度目标
```java
public Goal createAnnualGoal(
    EmployeeId employeeId,
    String title,
    String description,
    int year)
```

**特点：** 自动设置年度周期（1月1日-12月31日）

##### 3.2 创建季度目标
```java
public Goal createQuarterlyGoal(
    EmployeeId employeeId,
    String title,
    String description,
    int year,
    int quarter)
```

**特点：** 自动计算季度的开始和结束日期

##### 3.3 创建标准工程师年度目标
```java
public Goal createEngineerAnnualGoal(
    EmployeeId employeeId,
    int year)
```

**特点：** 预设工程师的标准OKR模板
- O1: 技术能力提升（30%）
- O2: 项目按时交付（40%）
- O3: 团队协作与分享（20%）
- O4: 创新与改进（10%）

### 4. 福利工厂（BenefitFactory）

负责创建福利聚合根。

#### 核心方法

##### 4.1 创建标准福利
```java
public Benefit createBenefit(
    String name,
    String description,
    BenefitType type,
    BenefitCost cost,
    String eligibilityCriteria)
```

##### 4.2 创建标准福利包
```java
// 试用期员工福利
public List<Benefit> createStandardBenefitPackage()

// 正式员工福利
public List<Benefit> createFormalEmployeeBenefitPackage()
```

**特点：** 提供预设的福利组合

### 5. 文化活动工厂（CultureActivityFactory）

负责创建文化活动聚合根。

#### 核心方法

##### 5.1 创建团建活动
```java
public CultureActivity createTeamBuildingActivity(
    String title,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String location,
    EmployeeId organizerId,
    int maxParticipants,
    BigDecimal budget)
```

##### 5.2 创建培训活动
```java
public CultureActivity createTrainingActivity(
    String title,
    LocalDateTime startTime,
    int durationHours,
    String location,
    EmployeeId organizerId,
    int maxParticipants)
```

##### 5.3 创建新员工入职培训
```java
public CultureActivity createOnboardingTraining(
    LocalDateTime startTime,
    EmployeeId organizerId)
```

**特点：** 预设入职培训的标准参数

## 工厂与其他组件的协作

### 1. 工厂 + 应用服务

```java
@Service
@RequiredArgsConstructor
public class EmployeeApplicationService {
    
    private final EmployeeFactory employeeFactory;
    private final EmployeeRepository employeeRepository;
    private final DomainEventPublisher eventPublisher;
    
    @Transactional
    public EmployeeDTO createEmployee(CreateEmployeeCommand command) {
        // 1. 使用工厂创建员工
        Employee employee = employeeFactory.createEmployee(
            personalInfo,
            contactInfo,
            departmentId,
            positionId,
            hireDate
        );
        
        // 2. 保存到仓储
        employee = employeeRepository.save(employee);
        
        // 3. 发布领域事件
        eventPublisher.publishAll(employee.getDomainEvents());
        
        return EmployeeDTO.fromDomain(employee);
    }
}
```

### 2. 工厂 + 仓储（重建对象）

```java
@Component
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {
    
    private final EmployeeJpaRepository jpaRepository;
    private final EmployeeFactory employeeFactory;
    
    @Override
    public Optional<Employee> findById(EmployeeId id) {
        return jpaRepository.findById(id.getValue())
            .map(entity -> entity.toDomain(employeeFactory));
    }
}
```

```java
// JPA Entity
public Employee toDomain(EmployeeFactory factory) {
    PersonalInfo personalInfo = new PersonalInfo(...);
    ContactInfo contactInfo = new ContactInfo(...);
    
    // 使用工厂的reconstitute方法重建
    return factory.reconstitute(
        EmployeeId.of(id),
        employeeNumber,
        personalInfo,
        contactInfo,
        departmentId,
        positionId,
        status,
        hireDate,
        probationEndDate,
        resignDate
    );
}
```

### 3. 工厂 + 防腐层

```java
@Service
@RequiredArgsConstructor
public class RecruitmentSystemService {
    
    private final RecruitmentSystemClient client;
    private final RecruitmentSystemAdapter adapter;
    private final EmployeeFactory employeeFactory;
    
    public Employee importCandidate(String candidateId) {
        // 1. 从外部系统获取数据
        CandidateData data = client.getCandidateById(candidateId);
        
        // 2. 使用工厂+适配器创建员工
        Employee employee = employeeFactory.createFromRecruitmentSystem(
            data,
            adapter
        );
        
        return employee;
    }
}
```

## 设计原则

### 1. 单一职责
每个工厂只负责一个聚合根的创建逻辑。

### 2. 封装创建逻辑
应用服务不直接调用聚合根构造函数，而是通过工厂创建。

### 3. 工厂方法命名规范
- `create*()`: 创建新对象
- `reconstitute()`: 从持久化重建对象
- `build*()`: 构建复杂对象

### 4. 创建 vs 重建
- **创建（create）**: 执行业务验证，生成ID，发布事件
- **重建（reconstitute）**: 不验证，不生成ID，不发布事件，用于从数据库恢复

### 5. 工厂位置
工厂放在领域层（`domain/factory`），因为它是领域逻辑的一部分。

## 使用指南

### 何时使用工厂？

✅ **应该使用工厂的场景：**
1. 聚合根创建逻辑复杂（需要生成ID、验证等）
2. 有多种创建方式（标准创建、特殊创建、导入创建）
3. 需要从外部系统导入数据
4. 需要重建聚合根（从持久化恢复）
5. 需要创建预设模板（如标准OKR模板）

❌ **不需要工厂的场景：**
1. 简单值对象的创建（直接使用构造函数）
2. 简单实体的创建（直接使用构造函数）
3. 一次性的内部对象创建

### 最佳实践

1. **工厂不持有状态**：工厂应该是无状态的
2. **依赖注入仓储**：工厂可以依赖仓储来生成唯一编号
3. **返回完整对象**：工厂返回的对象应该处于有效状态
4. **不保存对象**：工厂只负责创建，不负责持久化
5. **清晰的命名**：工厂方法名要清晰表达创建意图

## 完整示例

### 场景：员工入职流程

```java
// 1. 控制器接收请求
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    
    private final EmployeeApplicationService applicationService;
    
    @PostMapping
    public ApiResponse<EmployeeDTO> createEmployee(
            @RequestBody CreateEmployeeCommand command) {
        EmployeeDTO dto = applicationService.createEmployee(command);
        return ApiResponse.success(dto, "员工创建成功");
    }
}

// 2. 应用服务协调
@Service
@RequiredArgsConstructor
public class EmployeeApplicationService {
    
    private final EmployeeFactory employeeFactory;
    private final EmployeeRepository employeeRepository;
    private final DomainEventPublisher eventPublisher;
    
    @Transactional
    public EmployeeDTO createEmployee(CreateEmployeeCommand command) {
        // 构建值对象
        PersonalInfo personalInfo = new PersonalInfo(...);
        ContactInfo contactInfo = new ContactInfo(...);
        
        // 使用工厂创建员工
        Employee employee = employeeFactory.createEmployee(
            personalInfo,
            contactInfo,
            DepartmentId.of(command.getDepartmentId()),
            PositionId.of(command.getPositionId()),
            LocalDate.now()
        );
        
        // 保存
        employee = employeeRepository.save(employee);
        
        // 发布事件
        eventPublisher.publishAll(employee.getDomainEvents());
        
        return EmployeeDTO.fromDomain(employee);
    }
}

// 3. 工厂负责创建
@Component
@RequiredArgsConstructor
public class EmployeeFactory {
    
    private final EmployeeRepository employeeRepository;
    
    public Employee createEmployee(
            PersonalInfo personalInfo,
            ContactInfo contactInfo,
            DepartmentId departmentId,
            PositionId positionId,
            LocalDate hireDate) {
        
        // 验证
        contactInfo.validate();
        
        // 生成ID和员工号
        EmployeeId employeeId = EmployeeId.generate();
        String employeeNumber = employeeRepository.generateEmployeeNumber();
        
        // 创建聚合根
        Employee employee = new Employee(
            employeeId,
            employeeNumber,
            personalInfo,
            contactInfo,
            departmentId,
            positionId,
            hireDate
        );
        
        return employee;
    }
}

// 4. 聚合根专注业务逻辑
public class Employee extends AggregateRoot<EmployeeId> {
    
    // 构造函数保持简洁，由工厂调用
    protected Employee(
            EmployeeId id,
            String employeeNumber,
            PersonalInfo personalInfo,
            ContactInfo contactInfo,
            DepartmentId departmentId,
            PositionId positionId,
            LocalDate hireDate) {
        super(id);
        this.employeeNumber = employeeNumber;
        this.personalInfo = personalInfo;
        this.contactInfo = contactInfo;
        this.departmentId = departmentId;
        this.positionId = positionId;
        this.hireDate = hireDate;
        this.status = EmployeeStatus.PROBATION;
        this.probationEndDate = hireDate.plusMonths(3);
        
        // 发布入职事件
        addDomainEvent(new EmployeeHiredEvent(
            this.id, this.employeeNumber, hireDate
        ));
    }
    
    // 业务方法
    public void transfer(DepartmentId newDepartmentId, ...) { ... }
    public void promote(PositionId newPositionId, ...) { ... }
    public void resign(LocalDate resignDate, ...) { ... }
}
```

## 总结

工厂模式在DDD中是一个重要的战术模式，它：

1. **封装创建逻辑**：将复杂的创建逻辑从业务逻辑中分离
2. **确保对象有效**：保证创建的对象处于有效状态
3. **支持多种创建方式**：标准创建、特殊创建、导入创建、重建
4. **与防腐层配合**：隔离外部系统的数据转换
5. **简化应用服务**：应用服务通过工厂创建对象，不直接调用构造函数

通过工厂模式，我们实现了创建逻辑的集中管理和复用，使代码更加清晰、可维护。


