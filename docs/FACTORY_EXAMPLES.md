# 工厂模式使用示例

本文档提供工厂模式在实际业务场景中的完整使用示例。

## 示例1：标准员工入职

### 业务场景
新员工通过HR系统入职，需要：
1. 生成员工ID和员工号
2. 设置试用期（3个月）
3. 初始化为试用期状态
4. 发布入职事件

### 代码实现

```java
// REST API调用
POST /api/employees
Content-Type: application/json

{
  "firstName": "张",
  "lastName": "三",
  "idCardNumber": "110101199001011234",
  "birthDate": "1990-01-01",
  "gender": "MALE",
  "email": "zhangsan@company.com",
  "phoneNumber": "13800138000",
  "address": "北京市朝阳区xxx",
  "emergencyContact": "李四",
  "emergencyPhone": "13900139000",
  "departmentId": "dept-001",
  "positionId": "pos-001"
}
```

```java
// 应用服务
@Service
@RequiredArgsConstructor
public class EmployeeApplicationService {
    
    private final EmployeeFactory employeeFactory;
    private final EmployeeRepository employeeRepository;
    private final DomainEventPublisher eventPublisher;
    
    @Transactional
    public EmployeeDTO createEmployee(CreateEmployeeCommand command) {
        // 构建值对象
        PersonalInfo personalInfo = new PersonalInfo(
            command.getFirstName(),
            command.getLastName(),
            command.getIdCardNumber(),
            command.getBirthDate(),
            command.getGender()
        );
        
        ContactInfo contactInfo = new ContactInfo(
            command.getEmail(),
            command.getPhoneNumber(),
            command.getAddress(),
            command.getEmergencyContact(),
            command.getEmergencyPhone()
        );
        
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
        
        // 发布领域事件
        eventPublisher.publishAll(employee.getDomainEvents());
        employee.clearDomainEvents();
        
        return EmployeeDTO.fromDomain(employee);
    }
}
```

### 工厂处理流程

```java
@Component
@RequiredArgsConstructor
public class EmployeeFactory {
    
    private final EmployeeRepository employeeRepository;
    
    public Employee createEmployee(...) {
        // 1. 验证联系信息
        contactInfo.validate();
        
        // 2. 生成员工ID
        EmployeeId employeeId = EmployeeId.generate();
        
        // 3. 生成员工号（从仓储获取序列号）
        String employeeNumber = employeeRepository.generateEmployeeNumber();
        // 结果如: EMP20231201001
        
        // 4. 创建聚合根（构造函数会自动设置试用期）
        Employee employee = new Employee(
            employeeId,
            employeeNumber,
            personalInfo,
            contactInfo,
            departmentId,
            positionId,
            hireDate
        );
        
        // 聚合根构造函数会：
        // - 设置状态为 PROBATION
        // - 计算试用期结束日期（入职日期 + 3个月）
        // - 发布 EmployeeHiredEvent
        
        return employee;
    }
}
```

## 示例2：特殊人才入职（无试用期）

### 业务场景
引进的高级技术专家，无需试用期，直接转正。

### 代码实现

```java
// REST API
POST /api/employees/special-talent
Content-Type: application/json

{
  "firstName": "王",
  "lastName": "专家",
  "idCardNumber": "110101198001011234",
  "birthDate": "1980-01-01",
  "gender": "MALE",
  "email": "wangzhuanjia@company.com",
  "phoneNumber": "13800138001",
  "address": "北京市海淀区xxx",
  "emergencyContact": "家属",
  "emergencyPhone": "13900139001",
  "departmentId": "dept-tech",
  "positionId": "pos-expert",
  "reason": "高级技术专家引进，经CEO批准免试用期"
}
```

```java
// 应用服务
@Transactional
public EmployeeDTO createSpecialTalentEmployee(
        CreateEmployeeCommand command, 
        String reason) {
    
    PersonalInfo personalInfo = new PersonalInfo(...);
    ContactInfo contactInfo = new ContactInfo(...);
    
    // 使用工厂创建特殊人才
    Employee employee = employeeFactory.createSpecialTalentEmployee(
        personalInfo,
        contactInfo,
        DepartmentId.of(command.getDepartmentId()),
        PositionId.of(command.getPositionId()),
        LocalDate.now(),
        reason
    );
    
    employee = employeeRepository.save(employee);
    eventPublisher.publishAll(employee.getDomainEvents());
    
    return EmployeeDTO.fromDomain(employee);
}
```

```java
// 工厂方法
public Employee createSpecialTalentEmployee(..., String reason) {
    // 1. 创建标准员工
    Employee employee = createEmployee(
        personalInfo, contactInfo, departmentId, positionId, hireDate
    );
    
    // 2. 立即转正
    employee.confirmEmploymentEarly(reason);
    // 这会：
    // - 状态改为 FORMAL
    // - 试用期结束日期设为当天
    // - 发布 EmployeeStatusChangedEvent
    
    return employee;
}
```

## 示例3：从招聘系统导入候选人

### 业务场景
候选人通过招聘系统完成面试流程，需要导入到HR系统成为员工。

### 防腐层数据转换

```java
// 外部招聘系统的候选人数据结构
{
  "id": "candidate-12345",
  "firstName": "李",
  "lastName": "四",
  "fullName": "李四",
  "idNumber": "110101199101011234",
  "birthDateStr": "1991-01-01",
  "sex": "M",
  "emailAddress": "lisi@external.com",
  "mobile": "13800138002",
  "homeAddress": "北京市东城区xxx",
  "emergencyContactName": "家属",
  "emergencyContactPhone": "13900139002",
  "departmentCode": "TECH",
  "positionCode": "ENG-SENIOR",
  "hiringDate": "2023-12-01",
  "status": "OFFER_ACCEPTED"
}
```

### 代码实现

```java
// 防腐服务
@Service
@RequiredArgsConstructor
public class RecruitmentSystemService {
    
    private final RecruitmentSystemClient client;
    private final RecruitmentSystemAdapter adapter;
    private final EmployeeFactory employeeFactory;
    private final EmployeeRepository employeeRepository;
    private final DomainEventPublisher eventPublisher;
    
    @Transactional
    public Employee importCandidate(String candidateId) {
        // 1. 从外部系统获取候选人数据
        CandidateData candidateData = client.getCandidateById(candidateId);
        
        // 2. 使用工厂+适配器创建员工
        Employee employee = employeeFactory.createFromRecruitmentSystem(
            candidateData,
            adapter
        );
        
        // 3. 保存
        employee = employeeRepository.save(employee);
        
        // 4. 发布事件
        eventPublisher.publishAll(employee.getDomainEvents());
        employee.clearDomainEvents();
        
        // 5. 更新外部系统状态
        client.updateCandidateStatus(candidateId, "HIRED");
        
        return employee;
    }
}
```

```java
// 工厂方法（与适配器协作）
public Employee createFromRecruitmentSystem(
        CandidateData candidateData,
        RecruitmentSystemAdapter adapter) {
    
    // 1. 适配器转换数据（防腐层）
    ConvertedData convertedData = adapter.toDomainModel(candidateData);
    // 适配器负责：
    // - 字段名映射（firstName vs firstName）
    // - 数据格式转换（"1991-01-01" -> LocalDate）
    // - 枚举转换（"M" -> Gender.MALE）
    // - ID映射（"TECH" -> DepartmentId）
    
    // 2. 使用标准工厂方法创建
    Employee employee = createEmployee(
        convertedData.getPersonalInfo(),
        convertedData.getContactInfo(),
        convertedData.getDepartmentId(),
        convertedData.getPositionId(),
        convertedData.getHireDate()
    );
    
    return employee;
}
```

## 示例4：创建技术部门（包含标准职位）

### 业务场景
新建技术部门，自动创建标准技术职位（初级/中级/高级/专家工程师）。

### 代码实现

```java
// 应用服务
@Service
@RequiredArgsConstructor
public class DepartmentApplicationService {
    
    private final DepartmentFactory departmentFactory;
    private final DepartmentRepository departmentRepository;
    
    @Transactional
    public DepartmentDTO createTechnicalDepartment(
            String name, 
            String code, 
            String managerId) {
        
        // 使用工厂创建技术部门（包含标准职位）
        Department department = departmentFactory.createTechnicalDepartment(
            name,
            code,
            EmployeeId.of(managerId)
        );
        
        department = departmentRepository.save(department);
        
        return DepartmentDTO.fromDomain(department);
    }
}
```

```java
// 工厂方法
public Department createTechnicalDepartment(
        String name,
        String code,
        EmployeeId managerId) {
    
    // 1. 创建部门
    Department department = createDepartment(
        name, 
        code, 
        DepartmentType.DEPARTMENT, 
        null, 
        managerId, 
        "技术部门"
    );
    
    // 2. 添加标准职位
    // 初级工程师
    Position juniorPosition = createPosition(
        "初级工程师", 
        code + "-JUNIOR", 
        PositionLevel.JUNIOR,
        new BigDecimal("8000"), 
        new BigDecimal("12000"),
        "初级技术岗位", 
        5
    );
    department.addPosition(juniorPosition);
    
    // 中级工程师
    Position midPosition = createPosition(
        "中级工程师", 
        code + "-MID", 
        PositionLevel.INTERMEDIATE,
        new BigDecimal("12000"), 
        new BigDecimal("18000"),
        "中级技术岗位", 
        3
    );
    department.addPosition(midPosition);
    
    // 高级工程师
    Position seniorPosition = createPosition(
        "高级工程师", 
        code + "-SENIOR", 
        PositionLevel.SENIOR,
        new BigDecimal("18000"), 
        new BigDecimal("30000"),
        "高级技术岗位", 
        2
    );
    department.addPosition(seniorPosition);
    
    // 技术专家
    Position expertPosition = createPosition(
        "技术专家", 
        code + "-EXPERT", 
        PositionLevel.EXPERT,
        new BigDecimal("30000"), 
        new BigDecimal("50000"),
        "专家级技术岗位", 
        1
    );
    department.addPosition(expertPosition);
    
    return department;
}
```

## 示例5：创建标准工程师OKR

### 业务场景
为工程师创建年度OKR，使用标准模板。

### 代码实现

```java
// 应用服务
@Service
@RequiredArgsConstructor
public class GoalApplicationService {
    
    private final GoalFactory goalFactory;
    private final GoalRepository goalRepository;
    
    @Transactional
    public GoalDTO createEngineerAnnualGoal(String employeeId, int year) {
        // 使用工厂创建标准工程师OKR
        Goal goal = goalFactory.createEngineerAnnualGoal(
            EmployeeId.of(employeeId),
            year
        );
        
        goal = goalRepository.save(goal);
        
        return GoalDTO.fromDomain(goal);
    }
}
```

```java
// 工厂生成的OKR
public Goal createEngineerAnnualGoal(EmployeeId employeeId, int year) {
    GoalId goalId = GoalId.generate();
    
    GoalPeriod period = new GoalPeriod(
        LocalDate.of(year, 1, 1),
        LocalDate.of(year, 12, 31),
        LocalDate.of(year, 1, 15)
    );
    
    Goal goal = new Goal(
        goalId,
        employeeId,
        String.format("%d年度工作目标", year),
        "工程师年度关键目标",
        period
    );
    
    // O1: 技术能力提升（30%）
    goal.addObjective(createObjective(
        "技术能力提升",
        "完成3个技术专项学习，通过技术认证考试",
        new BigDecimal("30"),
        new BigDecimal("3")
    ));
    
    // O2: 项目按时交付（40%）
    goal.addObjective(createObjective(
        "项目按时交付",
        "负责的项目100%按时交付，质量达标",
        new BigDecimal("40"),
        new BigDecimal("100")
    ));
    
    // O3: 团队协作（20%）
    goal.addObjective(createObjective(
        "团队协作与分享",
        "完成6次技术分享，帮助2名新人成长",
        new BigDecimal("20"),
        new BigDecimal("6")
    ));
    
    // O4: 创新贡献（10%）
    goal.addObjective(createObjective(
        "创新与改进",
        "提出并落地3个流程或技术改进方案",
        new BigDecimal("10"),
        new BigDecimal("3")
    ));
    
    return goal;
}
```

### 生成结果

```json
{
  "goalId": "goal-20230101001",
  "employeeId": "emp-001",
  "title": "2023年度工作目标",
  "description": "工程师年度关键目标",
  "period": {
    "startDate": "2023-01-01",
    "endDate": "2023-12-31",
    "registrationDeadline": "2023-01-15"
  },
  "objectives": [
    {
      "objectiveId": "obj-001",
      "description": "技术能力提升",
      "keyResult": "完成3个技术专项学习，通过技术认证考试",
      "weight": 30,
      "targetValue": 3,
      "actualValue": 0,
      "status": "NOT_STARTED"
    },
    {
      "objectiveId": "obj-002",
      "description": "项目按时交付",
      "keyResult": "负责的项目100%按时交付，质量达标",
      "weight": 40,
      "targetValue": 100,
      "actualValue": 0,
      "status": "NOT_STARTED"
    },
    {
      "objectiveId": "obj-003",
      "description": "团队协作与分享",
      "keyResult": "完成6次技术分享，帮助2名新人成长",
      "weight": 20,
      "targetValue": 6,
      "actualValue": 0,
      "status": "NOT_STARTED"
    },
    {
      "objectiveId": "obj-004",
      "description": "创新与改进",
      "keyResult": "提出并落地3个流程或技术改进方案",
      "weight": 10,
      "targetValue": 3,
      "actualValue": 0,
      "status": "NOT_STARTED"
    }
  ],
  "status": "DRAFT"
}
```

## 示例6：创建福利包

### 业务场景
员工转正后，自动分配正式员工福利包。

### 代码实现

```java
// 事件处理器（员工转正后触发）
@Component
@RequiredArgsConstructor
public class EmployeeEventHandler {
    
    private final BenefitFactory benefitFactory;
    private final BenefitRepository benefitRepository;
    
    @EventListener
    public void handleEmployeeConfirmed(EmployeeStatusChangedEvent event) {
        if (event.getNewStatus() == EmployeeStatus.FORMAL) {
            // 员工转正，分配正式员工福利包
            List<Benefit> benefits = 
                benefitFactory.createFormalEmployeeBenefitPackage();
            
            benefits.forEach(benefit -> {
                benefitRepository.save(benefit);
                // 自动为员工登记福利
                enrollEmployeeInBenefit(event.getEmployeeId(), benefit);
            });
        }
    }
}
```

```java
// 工厂生成福利包
public List<Benefit> createFormalEmployeeBenefitPackage() {
    List<Benefit> benefits = new ArrayList<>();
    
    // 五险
    benefits.add(createSocialInsurance());
    
    // 住房公积金
    benefits.add(createHousingFund());
    
    // 餐补
    benefits.add(createMealAllowance(new BigDecimal("500")));
    
    // 交通补贴
    benefits.add(createTransportAllowance(new BigDecimal("300")));
    
    // 健身房会员
    benefits.add(createGymMembership());
    
    return benefits;
}
```

## 示例7：从数据库重建员工

### 业务场景
查询员工详情，需要从数据库重建员工聚合根。

### 代码实现

```java
// 仓储实现
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
// JPA实体的toDomain方法
public Employee toDomain(EmployeeFactory factory) {
    PersonalInfo personalInfo = new PersonalInfo(
        firstName, lastName, idCardNumber, birthDate, gender
    );
    
    ContactInfo contactInfo = new ContactInfo(
        email, phoneNumber, address, emergencyContact, emergencyPhone
    );
    
    // 使用工厂的reconstitute方法重建
    // 注意：reconstitute不执行业务验证，不发布事件
    return factory.reconstitute(
        EmployeeId.of(id),
        employeeNumber,
        personalInfo,
        contactInfo,
        DepartmentId.of(departmentId),
        PositionId.of(positionId),
        status,                 // 恢复状态
        hireDate,
        probationEndDate,       // 恢复试用期结束日期
        resignDate              // 恢复离职日期（如果有）
    );
}
```

```java
// 工厂的reconstitute方法
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
        LocalDate resignDate) {
    
    // 调用聚合根的静态重建方法
    return Employee.reconstitute(
        id, employeeNumber, personalInfo, contactInfo,
        departmentId, positionId, status,
        hireDate, probationEndDate, resignDate
    );
}
```

## 对比：构造函数 vs 工厂方法 vs Reconstitute

### 场景对比

| 场景 | 使用方式 | 执行验证 | 生成ID | 发布事件 | 调用者 |
|------|---------|---------|--------|---------|--------|
| 新员工入职 | `factory.createEmployee()` | ✅ | ✅ | ✅ | ApplicationService |
| 特殊人才入职 | `factory.createSpecialTalent()` | ✅ | ✅ | ✅ | ApplicationService |
| 外部系统导入 | `factory.createFromRecruitment()` | ✅ | ✅ | ✅ | ACL Service |
| 数据库查询 | `factory.reconstitute()` | ❌ | ❌ | ❌ | Repository |
| 单元测试 | 构造函数或工厂 | 可选 | 可选 | 可选 | Test |

## 总结

工厂模式的核心价值：

1. **封装创建逻辑**：集中管理对象创建的复杂性
2. **保证对象有效**：通过验证确保对象处于有效状态
3. **支持多种场景**：标准创建、特殊创建、导入、重建
4. **与防腐层协作**：隔离外部系统的影响
5. **预设模板**：提供常见场景的标准配置

通过这些示例，您可以看到工厂在实际业务中的应用方式和价值。


