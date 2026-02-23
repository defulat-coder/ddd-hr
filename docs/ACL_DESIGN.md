# 防腐层(Anti-Corruption Layer)设计文档

## 1. 什么是防腐层？

防腐层（ACL）是领域驱动设计中的一个重要模式，用于**保护领域模型不受外部系统的"腐蚀"**。

### 1.1 核心目的

- 🛡️ **保护领域模型**：避免外部系统的数据结构和业务规则侵入内部领域模型
- 🔄 **模型转换**：在不同的限界上下文或外部系统之间进行模型翻译
- 🔌 **解耦**：降低系统间的耦合度，便于独立演化
- 🎭 **适配**：适配不同系统的接口和数据格式

### 1.2 为什么需要防腐层？

```
❌ 没有防腐层的问题：

External System          Your Domain
┌──────────────┐        ┌──────────────┐
│ CandidateData│───────►│   Employee   │
│  - sex: "M"  │        │ - gender: ?  │
│  - fullname  │        │ - firstName? │
└──────────────┘        └──────────────┘
        ↓
  领域模型被外部结构污染！


✅ 使用防腐层：

External System    ACL              Your Domain
┌──────────────┐  ┌──────┐  ┌──────────────┐
│CandidateData │→│Adapter│→│   Employee   │
└──────────────┘  └──────┘  └──────────────┘
                     ↑
                 转换&保护
```

## 2. HR系统中的防腐层架构

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                    HR 领域模型                           │
│  (Employee, Department, Goal, Benefit, CultureActivity)│
└────────────────────┬────────────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │    Anti-Corruption Layer│
        │     (防腐层中间件)       │
        └────────────┬────────────┘
                     │
    ┌────────────────┼────────────────┐
    │                │                │
┌───▼───┐      ┌────▼────┐     ┌────▼────┐
│外部招聘│      │遗留OA   │     │上下文间 │
│系统    │      │系统     │     │通信     │
└───────┘      └─────────┘     └─────────┘
```

### 2.2 防腐层分类

我们的HR系统实现了三种类型的防腐层：

#### 1️⃣ 外部系统防腐层 (External System ACL)

**场景**：集成第三方系统或遗留系统

**示例**：
- `RecruitmentSystemAdapter` - 适配外部招聘系统
- `LegacyOrgSystemAdapter` - 适配遗留组织系统

#### 2️⃣ 上下文间防腐层 (Context-to-Context ACL)

**场景**：不同限界上下文之间的通信

**示例**：
- `EmployeeContextTranslator` - 员工上下文翻译器（福利上下文使用）
- `EmployeeContextAdapter` - 员工上下文适配器（绩效上下文使用）
- `EmployeeContextFacade` - 员工上下文门面（文化上下文使用）

#### 3️⃣ 遗留系统防腐层 (Legacy System ACL)

**场景**：迁移或集成旧系统

**示例**：
- `LegacyOrgSystemAdapter` - 适配旧的组织架构系统

## 3. 防腐层核心组件

### 3.1 基础接口

#### ExternalSystemAdapter（外部系统适配器）

```java
public interface ExternalSystemAdapter<T, R> {
    // 外部模型 -> 领域模型
    T toDomainModel(R externalModel);
    
    // 领域模型 -> 外部模型
    R toExternalModel(T domainModel);
}
```

#### Translator（翻译器）

```java
public interface Translator<SOURCE, TARGET> {
    // 源模型 -> 目标模型
    TARGET translate(SOURCE source);
}
```

#### AntiCorruptionService（防腐服务标记）

```java
public interface AntiCorruptionService {
    // 标记接口，用于识别防腐层服务
}
```

### 3.2 组件职责

| 组件 | 职责 | 使用场景 |
|------|------|---------|
| **Adapter** | 双向转换，适配不同系统 | 外部系统集成 |
| **Translator** | 单向翻译，上下文间转换 | 限界上下文通信 |
| **Service** | 封装调用逻辑，提供门面 | 业务流程编排 |

## 4. 实现示例

### 4.1 外部系统集成：招聘系统

#### 场景
从外部招聘系统导入候选人为员工

#### 外部系统的数据结构
```java
class CandidateData {
    String sex;              // "M" / "F"
    String birthDateStr;     // "1990-01-01"
    String emailAddress;     // 字段名不同
    String departmentCode;   // 使用code而非ID
}
```

#### 我们的领域模型
```java
class Employee {
    Gender gender;           // 枚举
    LocalDate birthDate;     // 日期类型
    String email;            // 字段名不同
    DepartmentId departmentId; // 使用ID而非code
}
```

#### 防腐层实现

**步骤1：创建适配器**
```java
@Component
public class RecruitmentSystemAdapter 
    implements ExternalSystemAdapter<Employee, CandidateData> {
    
    @Override
    public Employee toDomainModel(CandidateData external) {
        // 转换性别
        Gender gender = convertGender(external.getSex());
        
        // 转换日期
        LocalDate birthDate = LocalDate.parse(
            external.getBirthDateStr(), 
            DATE_FORMATTER
        );
        
        // 转换部门code为ID
        DepartmentId deptId = convertDepartmentCode(
            external.getDepartmentCode()
        );
        
        // 创建领域对象
        return new Employee(...);
    }
}
```

**步骤2：创建防腐服务**
```java
@Service
public class RecruitmentSystemService 
    implements AntiCorruptionService {
    
    public Employee importCandidateAsEmployee(String candidateId) {
        // 1. 从外部系统获取数据
        CandidateData data = recruitmentClient
            .getCandidateById(candidateId);
        
        // 2. 适配器转换
        Employee employee = adapter.toDomainModel(data);
        
        // 3. 保存到领域仓储
        employee = employeeRepository.save(employee);
        
        // 4. 更新外部系统状态
        recruitmentClient.updateCandidateStatus(
            candidateId, "HIRED"
        );
        
        return employee;
    }
}
```

**步骤3：在API中使用**
```java
@RestController
public class EmployeeImportController {
    
    @PostMapping("/employees/import/from-recruitment/{candidateId}")
    public ApiResponse<EmployeeDTO> importFromRecruitment(
            @PathVariable String candidateId) {
        
        // 通过防腐层导入，领域模型不受影响
        Employee employee = recruitmentSystemService
            .importCandidateAsEmployee(candidateId);
        
        return ApiResponse.success(
            EmployeeDTO.fromDomain(employee)
        );
    }
}
```

### 4.2 上下文间通信：福利查询员工

#### 场景
福利上下文需要验证员工是否有资格参加福利

#### 问题
如果直接依赖Employee聚合根：
```java
❌ 不好的做法：
@Service
public class BenefitApplicationService {
    @Autowired
    private EmployeeRepository employeeRepository; // 直接依赖
    
    public void enrollBenefit(BenefitId benefitId, EmployeeId employeeId) {
        Employee employee = employeeRepository.findById(employeeId).get();
        // 福利上下文直接访问员工聚合根 - 造成耦合！
    }
}
```

#### 防腐层解决方案

**步骤1：创建翻译器**
```java
@Component
public class EmployeeContextTranslator 
    implements Translator<EmployeeId, EmployeeInfo> {
    
    @Override
    public EmployeeInfo translate(EmployeeId source) {
        // 只返回福利上下文需要的信息
        return new EmployeeInfo(
            source.getValue(),
            "employeeNumber",
            true // isActive
        );
    }
    
    // 简化的DTO，不暴露完整的Employee
    public static class EmployeeInfo {
        private String employeeId;
        private String employeeNumber;
        private boolean active;
    }
}
```

**步骤2：创建防腐服务**
```java
@Service
public class EmployeeContextService 
    implements AntiCorruptionService {
    
    private final EmployeeContextTranslator translator;
    
    public boolean isEmployeeEligibleForBenefit(EmployeeId employeeId) {
        // 通过翻译器获取信息
        EmployeeInfo info = translator.translate(employeeId);
        
        // 福利领域的业务规则
        return info.isActive();
    }
}
```

**步骤3：在福利领域使用**
```java
✅ 好的做法：
@Service
public class BenefitApplicationService {
    // 依赖防腐层服务，而非直接依赖员工仓储
    private final EmployeeContextService employeeContextService;
    
    public void enrollBenefit(BenefitId benefitId, EmployeeId employeeId) {
        // 通过防腐层检查资格
        if (!employeeContextService.isEmployeeEligibleForBenefit(employeeId)) {
            throw new BusinessException("员工不符合福利资格");
        }
        // ... 福利领域的业务逻辑
    }
}
```

### 4.3 遗留系统集成：旧OA系统

#### 场景
迁移旧的组织架构系统数据到新系统

#### 旧系统数据结构
```java
class LegacyDepartmentData {
    Long deptId;
    Integer deptType;      // 1,2,3,4 数字代码
    String createTime;     // 字符串时间
    Integer status;        // 0/1
}
```

#### 新系统领域模型
```java
class Department {
    DepartmentId id;
    DepartmentType type;   // 枚举
    LocalDateTime createdAt;
    boolean active;
}
```

#### 防腐层实现
```java
@Component
public class LegacyOrgSystemAdapter 
    implements ExternalSystemAdapter<Department, LegacyDepartmentData> {
    
    @Override
    public Department toDomainModel(LegacyDepartmentData legacy) {
        // 转换类型代码
        DepartmentType type = convertDepartmentType(
            legacy.getDeptType()
        );
        
        // 转换状态
        boolean active = legacy.getStatus() == 1;
        
        // 创建领域对象
        return new Department(...);
    }
    
    private DepartmentType convertDepartmentType(Integer code) {
        return switch (code) {
            case 1 -> DepartmentType.HEADQUARTERS;
            case 2 -> DepartmentType.BRANCH;
            case 3 -> DepartmentType.DEPARTMENT;
            case 4 -> DepartmentType.TEAM;
            default -> DepartmentType.DEPARTMENT;
        };
    }
}
```

## 5. 防腐层模式对比

### 5.1 Adapter vs Translator vs Facade

| 特性 | Adapter | Translator | Facade |
|------|---------|-----------|--------|
| **转换方向** | 双向 | 单向 | 单向 |
| **复杂度** | 高 | 中 | 低 |
| **使用场景** | 外部系统集成 | 上下文翻译 | 简单查询 |
| **示例** | RecruitmentSystemAdapter | EmployeeContextTranslator | EmployeeContextFacade |

### 5.2 选择建议

```
┌─────────────────────────────────────────────┐
│  何时使用哪种模式？                          │
├─────────────────────────────────────────────┤
│                                             │
│  外部系统集成（复杂转换）                    │
│       → 使用 Adapter                        │
│                                             │
│  上下文间通信（单向查询）                    │
│       → 使用 Translator                     │
│                                             │
│  简单的跨上下文查询                          │
│       → 使用 Facade                         │
│                                             │
│  遗留系统迁移                                │
│       → 使用 Adapter                        │
│                                             │
└─────────────────────────────────────────────┘
```

## 6. 最佳实践

### 6.1 设计原则

✅ **DO（应该做）**：

1. **单向依赖**：防腐层依赖领域模型，领域模型不依赖防腐层
2. **独立演化**：外部系统变化时，只需修改防腐层
3. **明确职责**：每个适配器只负责一个外部系统
4. **DTO隔离**：使用专门的DTO，不暴露完整的聚合根
5. **异常处理**：在防腐层捕获和转换外部系统的异常

❌ **DON'T（不应该做）**：

1. **避免直接依赖**：不要在领域层直接依赖外部系统
2. **避免泄露**：不要让外部模型泄露到领域层
3. **避免过度设计**：简单场景不需要复杂的防腐层
4. **避免性能问题**：注意转换的性能开销
5. **避免双向依赖**：防腐层和领域层不应互相依赖

### 6.2 命名约定

```
外部系统适配器：
  [SystemName]Adapter
  例如：RecruitmentSystemAdapter

上下文翻译器：
  [ContextName]Translator
  例如：EmployeeContextTranslator

防腐服务：
  [SystemName]Service
  例如：RecruitmentSystemService

上下文门面：
  [ContextName]Facade
  例如：EmployeeContextFacade
```

### 6.3 目录结构

```
employee/
├── acl/                          # 防腐层目录
│   ├── external/                 # 外部系统防腐层
│   │   ├── RecruitmentSystemClient.java
│   │   ├── RecruitmentSystemAdapter.java
│   │   ├── RecruitmentSystemService.java
│   │   └── RecruitmentSystemClientImpl.java
│   └── context/                  # 上下文防腐层（如需要）
│
benefit/
├── acl/                          # 福利上下文的防腐层
│   ├── EmployeeContextTranslator.java
│   └── EmployeeContextService.java
```

## 7. 测试策略

### 7.1 适配器测试

```java
@Test
public void should_convert_candidate_to_employee() {
    // Given
    CandidateData candidate = new CandidateData();
    candidate.setSex("M");
    candidate.setBirthDateStr("1990-01-01");
    
    // When
    Employee employee = adapter.toDomainModel(candidate);
    
    // Then
    assertThat(employee.getGender()).isEqualTo(Gender.MALE);
    assertThat(employee.getBirthDate()).isEqualTo(
        LocalDate.of(1990, 1, 1)
    );
}
```

### 7.2 防腐服务测试

```java
@Test
public void should_import_candidate_successfully() {
    // Given
    when(recruitmentClient.getCandidateById("123"))
        .thenReturn(mockCandidateData);
    
    // When
    Employee employee = recruitmentSystemService
        .importCandidateAsEmployee("123");
    
    // Then
    assertThat(employee).isNotNull();
    verify(recruitmentClient).updateCandidateStatus("123", "HIRED");
}
```

## 8. 项目中的防腐层总览

### 8.1 已实现的防腐层

| 防腐层 | 类型 | 作用 |
|--------|------|------|
| `RecruitmentSystemAdapter` | 外部系统 | 适配招聘系统 |
| `LegacyOrgSystemAdapter` | 遗留系统 | 适配旧OA系统 |
| `EmployeeContextTranslator` | 上下文间 | 福利→员工 |
| `EmployeeContextAdapter` | 上下文间 | 绩效→员工 |
| `EmployeeContextFacade` | 上下文间 | 文化→员工 |

### 8.2 防腐层架构图

```
┌─────────────────────────────────────────────────────┐
│                  Shared ACL Layer                    │
│  - ExternalSystemAdapter (interface)                │
│  - Translator (interface)                           │
│  - AntiCorruptionService (interface)                │
└──────────────────────┬──────────────────────────────┘
                       │
        ┌──────────────┼──────────────┐
        │              │              │
┌───────▼──────┐ ┌────▼─────┐ ┌─────▼──────┐
│Employee ACL  │ │Benefit   │ │Performance │
│- Recruitment │ │ACL       │ │ACL         │
│  Adapter     │ │- Employee│ │- Employee  │
│              │ │  Translator│  Adapter   │
└──────────────┘ └──────────┘ └────────────┘
        │              │              │
        ▼              ▼              ▼
┌──────────────────────────────────────────┐
│          Domain Layer (Protected)         │
│  Employee, Department, Goal, Benefit...   │
└──────────────────────────────────────────┘
```

## 9. 扩展建议

### 未来可以添加的防腐层

1. **薪资系统防腐层**
   - `PayrollSystemAdapter`
   - 同步员工薪资信息

2. **考勤系统防腐层**
   - `AttendanceSystemAdapter`
   - 集成打卡数据

3. **邮件服务防腐层**
   - `EmailServiceAdapter`
   - 发送通知邮件

4. **短信服务防腐层**
   - `SmsServiceAdapter`
   - 发送短信通知

## 10. 总结

防腐层是DDD中保护领域模型纯洁性的重要模式。通过合理使用防腐层：

✅ **领域模型保持纯净**：不受外部系统影响
✅ **系统独立演化**：外部变化不影响核心业务
✅ **降低耦合度**：上下文间松耦合
✅ **便于测试**：可以mock外部依赖
✅ **易于维护**：变更隔离在防腐层

在我们的HR系统中，防腐层确保了核心业务逻辑的稳定性和可维护性！🛡️

