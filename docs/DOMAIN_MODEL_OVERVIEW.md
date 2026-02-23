# 员工中心领域模型完整结构

## 📦 领域层文件列表

### 位置：`src/main/java/com/company/hr/employee/domain/`

```
domain/
├── model/                          # 领域模型
│   ├── Employee.java              ⭐ 员工聚合根（核心）
│   ├── EmployeeId.java            # 员工ID值对象
│   ├── PersonalInfo.java          # 个人信息值对象
│   ├── ContactInfo.java           # 联系信息值对象
│   ├── Gender.java                # 性别枚举
│   ├── EmployeeStatus.java        # 员工状态枚举
│   │
│   ├── EmployeeHiredEvent.java              # 员工入职事件
│   ├── EmployeeStatusChangedEvent.java      # 状态变更事件
│   ├── ProbationExtendedEvent.java          ⭐ 试用期延长事件（新增）
│   ├── EmployeeTransferredEvent.java        ✏️ 员工调动事件（增强）
│   ├── EmployeePromotedEvent.java           ⭐ 员工晋升事件（新增）
│   └── EmployeeResignedEvent.java           ✏️ 员工离职事件（增强）
│
└── repository/                    # 仓储接口
    └── EmployeeRepository.java    # 员工仓储
```

---

## 🎯 核心聚合根：Employee

### 完整的领域方法（入转调离）

```java
public class Employee extends AggregateRoot<EmployeeId> {
    
    // ========== 构造方法 ==========
    
    /**
     * 创建新员工（入职）
     */
    public Employee(EmployeeId id, String employeeNumber, 
                   PersonalInfo personalInfo, ContactInfo contactInfo,
                   DepartmentId departmentId, PositionId positionId, 
                   LocalDate hireDate)
    
    /**
     * 重建员工聚合根（从持久化恢复）
     */
    public static Employee reconstitute(...)
    
    
    // ========== 转正相关 ==========
    
    /**
     * 正常转正（试用期满）
     */
    public void confirmEmployment()
    
    /**
     * 提前转正（表现优异）⭐新增
     */
    public void confirmEmploymentEarly(String reason)
    
    /**
     * 延长试用期（需要更多时间）⭐新增
     */
    public void extendProbation(int months, String reason)
    
    
    // ========== 调动相关 ==========
    
    /**
     * 部门调动（跨部门横向调动）
     */
    public void transfer(DepartmentId newDepartmentId, 
                        PositionId newPositionId, 
                        String reason)
    
    /**
     * 职位晋升（纵向发展）⭐新增
     */
    public void promote(PositionId newPositionId, String reason)
    
    /**
     * 停职（临时离岗）⭐新增
     */
    public void suspend(String reason)
    
    /**
     * 复职（恢复工作）⭐新增
     */
    public void reinstate()
    
    
    // ========== 离职相关 ==========
    
    /**
     * 主动辞职 ✏️增强
     */
    public void resign(LocalDate resignDate, String reason)
    
    /**
     * 被动辞退 ⭐新增
     */
    public void terminate(LocalDate terminateDate, String reason)
    
    
    // ========== 通用方法 ==========
    
    /**
     * 变更状态
     */
    public void changeStatus(EmployeeStatus newStatus)
    
    /**
     * 更新联系信息
     */
    public void updateContactInfo(ContactInfo newContactInfo)
    
    /**
     * 获取在职天数 ⭐新增
     */
    public long getWorkingDays()
    
    /**
     * 是否在职
     */
    public boolean isActive()
    
    /**
     * 是否在试用期 ⭐新增
     */
    public boolean isProbation()
}
```

---

## 📋 值对象

### 1. EmployeeId（员工ID）

```java
@Value
public class EmployeeId {
    String value;
    
    public static EmployeeId generate()  // 生成新ID
    public static EmployeeId of(String value)  // 从字符串创建
}
```

### 2. PersonalInfo（个人信息）

```java
@Value
public class PersonalInfo implements ValueObject {
    String firstName;
    String lastName;
    String idCardNumber;
    LocalDate birthDate;
    Gender gender;
    
    public String getFullName()  // 获取全名
    public int getAge()          // 计算年龄
}
```

### 3. ContactInfo（联系信息）

```java
@Value
public class ContactInfo implements ValueObject {
    String email;
    String phoneNumber;
    String address;
    String emergencyContact;
    String emergencyPhone;
    
    public void validate()  // 验证邮箱和手机号格式
}
```

---

## 🏷️ 枚举类型

### 1. Gender（性别）

```java
public enum Gender {
    MALE("男"),
    FEMALE("女"),
    OTHER("其他");
}
```

### 2. EmployeeStatus（员工状态）

```java
public enum EmployeeStatus {
    PROBATION("试用期"),   // 试用期
    ACTIVE("在职"),        // 在职
    SUSPENDED("停职"),     // 停职
    RESIGNED("离职");      // 离职
    
    // 状态转换验证
    public boolean canTransitionTo(EmployeeStatus newStatus)
}
```

**状态转换规则**：
```
PROBATION → ACTIVE, RESIGNED
ACTIVE → SUSPENDED, RESIGNED  
SUSPENDED → ACTIVE, RESIGNED
RESIGNED → 无（终态）
```

---

## 📢 领域事件

### 1. EmployeeHiredEvent（员工入职事件）

```java
@Getter
public class EmployeeHiredEvent extends DomainEvent {
    private final EmployeeId employeeId;
    private final String employeeNumber;
    private final LocalDate hireDate;
}
```

**触发时机**：创建新员工时

---

### 2. EmployeeStatusChangedEvent（状态变更事件）

```java
@Getter
public class EmployeeStatusChangedEvent extends DomainEvent {
    private final EmployeeId employeeId;
    private final EmployeeStatus oldStatus;
    private final EmployeeStatus newStatus;
}
```

**触发时机**：
- 正常转正：PROBATION → ACTIVE
- 提前转正：PROBATION → ACTIVE
- 停职：ACTIVE → SUSPENDED
- 复职：SUSPENDED → ACTIVE

---

### 3. ProbationExtendedEvent（试用期延长事件）⭐新增

```java
@Getter
public class ProbationExtendedEvent extends DomainEvent {
    private final EmployeeId employeeId;
    private final LocalDate oldProbationEndDate;
    private final LocalDate newProbationEndDate;
    private final String reason;
}
```

**触发时机**：延长试用期时

**包含信息**：
- 员工ID
- 原试用期结束日期
- 新试用期结束日期
- 延长原因

---

### 4. EmployeeTransferredEvent（员工调动事件）✏️增强

```java
@Getter
public class EmployeeTransferredEvent extends DomainEvent {
    private final EmployeeId employeeId;
    private final DepartmentId oldDepartmentId;
    private final DepartmentId newDepartmentId;
    private final PositionId oldPositionId;
    private final PositionId newPositionId;
    private final String reason;  // ✏️新增字段
}
```

**触发时机**：部门调动时

**包含信息**：
- 员工ID
- 原部门ID和新部门ID
- 原职位ID和新职位ID
- 调动原因（新增）

---

### 5. EmployeePromotedEvent（员工晋升事件）⭐新增

```java
@Getter
public class EmployeePromotedEvent extends DomainEvent {
    private final EmployeeId employeeId;
    private final PositionId oldPositionId;
    private final PositionId newPositionId;
    private final String reason;
}
```

**触发时机**：职位晋升时

**包含信息**：
- 员工ID
- 原职位ID
- 新职位ID
- 晋升原因

**与调动的区别**：晋升只变更职位，不变更部门

---

### 6. EmployeeResignedEvent（员工离职事件）✏️增强

```java
@Getter
public class EmployeeResignedEvent extends DomainEvent {
    private final EmployeeId employeeId;
    private final String employeeNumber;
    private final LocalDate resignDate;
    private final String reason;        // ✏️新增字段
    private final String resignType;    // ✏️新增字段
}
```

**触发时机**：
- 主动辞职：resignType = "RESIGNATION"
- 被动辞退：resignType = "TERMINATION"

**包含信息**：
- 员工ID和工号
- 离职日期
- 离职原因（新增）
- 离职类型（新增）

---

## 🔄 业务流程映射

### 入职流程
```
API调用 → Employee构造函数 → 发布EmployeeHiredEvent
```

### 转正流程
```
正常转正: confirmEmployment() → 发布EmployeeStatusChangedEvent
提前转正: confirmEmploymentEarly() → 发布EmployeeStatusChangedEvent
延长试用: extendProbation() → 发布ProbationExtendedEvent
```

### 调动流程
```
部门调动: transfer() → 发布EmployeeTransferredEvent
职位晋升: promote() → 发布EmployeePromotedEvent
停职: suspend() → 发布EmployeeStatusChangedEvent
复职: reinstate() → 发布EmployeeStatusChangedEvent
```

### 离职流程
```
主动辞职: resign() → 发布EmployeeResignedEvent(RESIGNATION)
被动辞退: terminate() → 发布EmployeeResignedEvent(TERMINATION)
```

---

## 📊 领域模型统计

| 类型 | 数量 | 文件 |
|------|-----|------|
| 聚合根 | 1 | Employee.java |
| 值对象 | 3 | EmployeeId, PersonalInfo, ContactInfo |
| 枚举 | 2 | Gender, EmployeeStatus |
| 领域事件 | 6 | 6个事件类 |
| 仓储接口 | 1 | EmployeeRepository |
| **总计** | **13** | **13个文件** |

---

## 🎯 业务规则封装

### 转正规则
```java
// 1. 正常转正
if (status != EmployeeStatus.PROBATION) {
    throw new DomainException("只有试用期员工才能转正");
}
if (LocalDate.now().isBefore(probationEndDate)) {
    throw new DomainException("试用期未结束，不能转正");
}

// 2. 提前转正
if (reason == null || reason.trim().isEmpty()) {
    throw new DomainException("提前转正必须提供理由");
}

// 3. 延长试用期
if (months <= 0 || months > 6) {
    throw new DomainException("试用期延长时间必须在1-6个月之间");
}
long totalMonths = ChronoUnit.MONTHS.between(hireDate, probationEndDate);
if (totalMonths > 6) {
    throw new DomainException("试用期总长度不能超过6个月");
}
```

### 调动规则
```java
// 1. 部门调动
if (status != EmployeeStatus.ACTIVE) {
    throw new DomainException("只有在职员工才能调动");
}
if (newDepartmentId.equals(departmentId) && newPositionId.equals(positionId)) {
    throw new DomainException("新部门和职位与当前相同，无需调动");
}
if (reason == null || reason.trim().isEmpty()) {
    throw new DomainException("调动必须提供原因");
}

// 2. 晋升
if (newPositionId.equals(positionId)) {
    throw new DomainException("新职位与当前相同，无需晋升");
}
```

### 离职规则
```java
// 1. 主动辞职/被动辞退
if (status == EmployeeStatus.RESIGNED) {
    throw new DomainException("员工已经离职");
}
if (resignDate.isBefore(LocalDate.now())) {
    throw new DomainException("离职日期不能早于当前日期");
}
if (reason == null || reason.trim().isEmpty()) {
    throw new DomainException("离职必须提供原因");
}
```

---

## 🚀 使用示例

### 示例1：完整的员工生命周期

```java
// 1. 入职
Employee employee = new Employee(
    EmployeeId.generate(),
    "2024000001",
    personalInfo,
    contactInfo,
    departmentId,
    positionId,
    LocalDate.now()
);
// 发布事件：EmployeeHiredEvent

// 2. 延长试用期（2个月后）
employee.extendProbation(2, "需要更多时间适应岗位");
// 发布事件：ProbationExtendedEvent

// 3. 转正（试用期满后）
employee.confirmEmployment();
// 发布事件：EmployeeStatusChangedEvent

// 4. 晋升（1年后）
employee.promote(seniorPositionId, "工作表现优秀");
// 发布事件：EmployeePromotedEvent

// 5. 调动（2年后）
employee.transfer(newDeptId, newPosId, "组织架构调整");
// 发布事件：EmployeeTransferredEvent

// 6. 离职（3年后）
employee.resign(LocalDate.now().plusDays(30), "个人发展");
// 发布事件：EmployeeResignedEvent
```

### 示例2：特殊场景

```java
// 优秀员工提前转正
employee.confirmEmploymentEarly("表现优异，提前转正");

// 停职调查
employee.suspend("配合调查");
// 调查结束后复职
employee.reinstate();

// 公司辞退
employee.terminate(LocalDate.now().plusDays(7), "严重违反公司规定");
```

---

## 📝 领域模型设计亮点

### 1. 充血模型
✅ 业务逻辑封装在领域对象中
✅ 而不是在服务层

### 2. 不变性保护
✅ 值对象使用`@Value`，不可变
✅ 聚合根通过方法控制状态变更

### 3. 业务规则验证
✅ 状态转换规则
✅ 试用期长度限制
✅ 参数合法性检查

### 4. 领域事件驱动
✅ 关键业务操作发布事件
✅ 支持事件溯源
✅ 解耦上下文

### 5. 聚合边界清晰
✅ Employee是聚合根
✅ PersonalInfo、ContactInfo是值对象
✅ 外部只能通过聚合根访问

---

## 相关文档

- [Employee.java](src/main/java/com/company/hr/employee/domain/model/Employee.java) - 查看完整源码
- [EMPLOYEE_LIFECYCLE.md](EMPLOYEE_LIFECYCLE.md) - 完整业务流程
- [ARCHITECTURE.md](ARCHITECTURE.md) - 整体架构设计

---

**最后更新**: 2024-12-01
**领域模型版本**: 2.0（完整的入转调离）

