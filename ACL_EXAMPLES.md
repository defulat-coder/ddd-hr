# 防腐层使用示例

## 场景1：从招聘系统导入员工

### 业务场景
公司使用第三方招聘系统管理候选人，当候选人通过面试后，需要将其导入到HR系统成为正式员工。

### 问题
- 招聘系统使用不同的数据格式
- 字段名称不一致
- 数据类型不匹配

### 解决方案：使用防腐层

#### 1. 外部系统的数据结构
```json
{
  "id": "CAND-2024-001",
  "fullName": "张三",
  "sex": "M",
  "birthDateStr": "1990-01-01",
  "emailAddress": "zhangsan@example.com",
  "mobile": "13800138000",
  "departmentCode": "TECH",
  "positionCode": "DEV",
  "hiringDate": "2024-12-01"
}
```

#### 2. 内部领域模型
```java
Employee {
    EmployeeId id;
    PersonalInfo personalInfo {
        String firstName;
        String lastName;
        Gender gender;         // 枚举类型
        LocalDate birthDate;   // 日期类型
    }
    ContactInfo contactInfo {
        String email;
        String phoneNumber;
    }
    DepartmentId departmentId; // ID类型
    PositionId positionId;     // ID类型
}
```

#### 3. 适配器实现
```java
@Component
public class RecruitmentSystemAdapter {
    
    public Employee toDomainModel(CandidateData external) {
        // 转换性别：M/F -> Gender枚举
        Gender gender = "M".equals(external.getSex()) 
            ? Gender.MALE : Gender.FEMALE;
        
        // 转换日期：字符串 -> LocalDate
        LocalDate birthDate = LocalDate.parse(
            external.getBirthDateStr()
        );
        
        // 转换部门：code -> ID
        DepartmentId departmentId = findDepartmentByCode(
            external.getDepartmentCode()
        );
        
        // 创建领域对象
        return new Employee(...);
    }
}
```

#### 4. 使用防腐服务
```java
@Service
public class RecruitmentSystemService {
    
    public Employee importCandidateAsEmployee(String candidateId) {
        // 1. 从外部系统获取
        CandidateData data = recruitmentClient
            .getCandidateById(candidateId);
        
        // 2. 通过适配器转换
        Employee employee = adapter.toDomainModel(data);
        
        // 3. 保存到领域
        employee = employeeRepository.save(employee);
        
        // 4. 更新外部系统
        recruitmentClient.updateCandidateStatus(
            candidateId, "HIRED"
        );
        
        return employee;
    }
}
```

#### 5. API调用
```bash
# 导入候选人
POST /api/employees/import/from-recruitment/CAND-2024-001

# 响应
{
  "success": true,
  "data": {
    "id": "emp-uuid-123",
    "employeeNumber": "2024000001",
    "firstName": "张",
    "lastName": "三",
    "status": "PROBATION"
  }
}
```

---

## 场景2：福利上下文查询员工信息

### 业务场景
员工申请参加福利时，福利上下文需要验证员工是否有资格（是否在职）。

### 问题
如果福利上下文直接依赖Employee聚合根：
```java
❌ 不好的做法：
@Service
public class BenefitApplicationService {
    @Autowired
    private EmployeeRepository employeeRepository; // 跨上下文直接依赖
    
    public void enrollBenefit(BenefitId benefitId, EmployeeId employeeId) {
        Employee employee = employeeRepository.findById(employeeId).get();
        if (!employee.isActive()) {
            throw new BusinessException("员工不在职");
        }
        // 福利业务逻辑...
    }
}
```

**问题**：
- 福利上下文与员工上下文强耦合
- 员工聚合根变化会影响福利上下文
- 破坏了限界上下文的独立性

### 解决方案：使用防腐层

#### 1. 创建翻译器
```java
@Component
public class EmployeeContextTranslator 
    implements Translator<EmployeeId, EmployeeInfo> {
    
    @Override
    public EmployeeInfo translate(EmployeeId source) {
        // 只返回福利上下文需要的信息
        // 实际应该调用员工上下文的查询接口
        return new EmployeeInfo(
            source.getValue(),
            "2024000001",
            "张三",
            true // isActive
        );
    }
    
    // 简化的DTO
    public static class EmployeeInfo {
        private String employeeId;
        private String employeeNumber;
        private String name;
        private boolean active;
    }
}
```

#### 2. 创建防腐服务
```java
@Service
public class EmployeeContextService {
    
    private final EmployeeContextTranslator translator;
    
    public boolean isEmployeeEligibleForBenefit(EmployeeId employeeId) {
        // 通过翻译器获取信息
        EmployeeInfo info = translator.translate(employeeId);
        
        // 福利领域的规则
        return info.isActive();
    }
}
```

#### 3. 在福利领域使用
```java
✅ 好的做法：
@Service
public class BenefitApplicationService {
    // 依赖防腐层服务
    private final EmployeeContextService employeeContextService;
    
    public void enrollBenefit(BenefitId benefitId, EmployeeId employeeId) {
        // 通过防腐层检查
        if (!employeeContextService.isEmployeeEligibleForBenefit(employeeId)) {
            throw new BusinessException("员工不符合福利资格");
        }
        
        // 福利领域的业务逻辑
        Benefit benefit = benefitRepository.findById(benefitId).get();
        benefit.addEnrollment(new BenefitEnrollment(...));
        benefitRepository.save(benefit);
    }
}
```

#### 4. 对比图
```
❌ 直接依赖：
┌────────────┐
│ Benefit    │
│ Context    │
└──────┬─────┘
       │ 直接依赖
       ▼
┌────────────┐
│ Employee   │
│ Aggregate  │
└────────────┘


✅ 使用防腐层：
┌────────────┐
│ Benefit    │
│ Context    │
└──────┬─────┘
       │ 依赖防腐层
       ▼
┌────────────┐
│Employee    │
│Context ACL │  ← 翻译器
└──────┬─────┘
       │ 内部调用
       ▼
┌────────────┐
│ Employee   │
│ Context    │
└────────────┘
```

---

## 场景3：迁移遗留OA系统

### 业务场景
公司原有一套旧的OA系统，现在要迁移到新的HR系统。

### 旧系统数据结构
```sql
CREATE TABLE t_department (
    dept_id INT,
    dept_name VARCHAR(50),
    dept_type INT,          -- 1,2,3,4 数字代码
    parent_dept_id INT,
    status INT,             -- 0/1
    create_time VARCHAR(20) -- 字符串时间
);
```

### 新系统领域模型
```java
class Department {
    DepartmentId id;              // UUID
    String name;
    DepartmentType type;          // 枚举
    DepartmentId parentId;        // 类型安全的ID
    boolean active;               // 布尔值
    LocalDateTime createdAt;      // 日期时间类型
}
```

### 适配器实现
```java
@Component
public class LegacyOrgSystemAdapter {
    
    public Department toDomainModel(LegacyDepartmentData legacy) {
        // 转换类型代码
        DepartmentType type = switch (legacy.getDeptType()) {
            case 1 -> DepartmentType.HEADQUARTERS;
            case 2 -> DepartmentType.BRANCH;
            case 3 -> DepartmentType.DEPARTMENT;
            case 4 -> DepartmentType.TEAM;
            default -> DepartmentType.DEPARTMENT;
        };
        
        // 转换状态
        boolean active = legacy.getStatus() == 1;
        
        // 转换时间
        LocalDateTime createdAt = LocalDateTime.parse(
            legacy.getCreateTime(),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
        
        // 创建领域对象
        return new Department(
            DepartmentId.of(legacy.getDeptId().toString()),
            legacy.getDeptName(),
            type,
            ...
        );
    }
}
```

### 数据迁移服务
```java
@Service
public class DataMigrationService {
    
    public void migrateDepartments() {
        // 1. 从旧系统查询数据
        List<LegacyDepartmentData> legacyDepts = 
            legacyOrgSystemClient.getAllDepartments();
        
        // 2. 通过适配器转换
        List<Department> departments = legacyDepts.stream()
            .map(adapter::toDomainModel)
            .collect(Collectors.toList());
        
        // 3. 保存到新系统
        departments.forEach(departmentRepository::save);
        
        log.info("迁移完成，共 {} 个部门", departments.size());
    }
}
```

---

## 场景4：活动通知员工（上下文间通信）

### 业务场景
文化活动开放报名时，需要给所有符合条件的员工发送通知。

### 使用防腐门面
```java
@Service
public class EmployeeContextFacade {
    
    // 批量获取员工信息
    public List<EmployeeSimpleInfo> getEmployeesSimpleInfo(
            List<EmployeeId> employeeIds) {
        
        // 通过防腐层查询，只返回需要的信息
        return employeeIds.stream()
            .map(this::getEmployeeSimpleInfo)
            .toList();
    }
    
    public static class EmployeeSimpleInfo {
        private String employeeId;
        private String employeeName;
        private String email;      // 用于发送通知
        private String phoneNumber; // 用于发送短信
    }
}
```

### 活动服务使用
```java
@Service
public class CultureActivityService {
    
    private final EmployeeContextFacade employeeFacade;
    
    public void notifyActivityOpening(ActivityId activityId) {
        CultureActivity activity = activityRepository
            .findById(activityId).get();
        
        // 获取目标员工列表
        List<EmployeeId> targetEmployees = 
            getTargetEmployees(activity);
        
        // 通过防腐层获取员工信息
        List<EmployeeSimpleInfo> employees = 
            employeeFacade.getEmployeesSimpleInfo(targetEmployees);
        
        // 发送通知（只使用需要的信息）
        employees.forEach(emp -> {
            emailService.send(
                emp.getEmail(),
                "活动通知",
                activity.getTitle()
            );
        });
    }
}
```

---

## 防腐层最佳实践总结

### 1. 何时使用防腐层？

✅ **应该使用**：
- 集成第三方系统
- 对接遗留系统
- 不同限界上下文通信
- 数据格式差异较大
- 需要保护领域模型

❌ **不需要使用**：
- 简单的CRUD操作
- 上下文内部通信
- 数据格式完全一致
- 过度设计的场景

### 2. 选择合适的模式

| 场景 | 推荐模式 | 复杂度 |
|------|---------|-------|
| 外部系统集成 | Adapter | 高 |
| 上下文查询 | Translator | 中 |
| 简单查询 | Facade | 低 |
| 遗留系统迁移 | Adapter | 高 |

### 3. 防腐层的价值

🛡️ **保护性**：
- 领域模型不被外部污染
- 业务规则保持纯净

🔄 **灵活性**：
- 外部系统变化不影响内部
- 便于系统演化

🔌 **解耦性**：
- 降低上下文间耦合
- 提高可测试性

📈 **可维护性**：
- 变更隔离在防腐层
- 代码结构清晰

---

## 相关文档

- [ACL_DESIGN.md](ACL_DESIGN.md) - 防腐层详细设计
- [ARCHITECTURE.md](ARCHITECTURE.md) - 整体架构设计
- [README.md](README.md) - 项目说明

## API测试

```bash
# 测试防腐层：从招聘系统导入员工
curl -X POST http://localhost:8080/api/employees/import/from-recruitment/CAND-2024-001

# 响应
{
  "success": true,
  "data": {
    "id": "employee-id",
    "employeeNumber": "2024000001",
    "firstName": "李",
    "lastName": "四",
    "email": "lisi@example.com",
    "status": "PROBATION"
  },
  "message": "从招聘系统导入员工成功"
}
```

