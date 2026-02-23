# HR人力资源管理系统 - DDD设计

## 项目概述

这是一个基于领域驱动设计(DDD)和Spring Boot实现的人力资源管理系统，包含以下核心业务领域：

- **员工中心** - 管理员工信息、入职、离职、调动等
- **组织管理** - 管理部门、职位、组织架构
- **目标与绩效** - OKR目标管理、绩效评估
- **福利管理** - 员工福利、保险、补贴等
- **企业文化** - 文化活动、团建、培训等

## 技术栈

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- H2/MySQL Database
- Maven
- Lombok

## DDD架构设计

### 分层架构

```
com.company.hr/
├── shared/                     # 共享内核
│   ├── domain/                 # 领域基础类
│   │   ├── AggregateRoot      # 聚合根基类
│   │   ├── Entity             # 实体基类
│   │   ├── ValueObject        # 值对象接口
│   │   ├── DomainEvent        # 领域事件基类
│   │   └── Repository         # 仓储接口
│   ├── acl/                   # 防腐层基础
│   │   ├── ExternalSystemAdapter  # 外部系统适配器接口
│   │   ├── Translator             # 翻译器接口
│   │   └── AntiCorruptionService  # 防腐服务标记接口
│   └── exception/             # 异常定义
│
├── infrastructure/            # 基础设施层
│   ├── persistence/          # 持久化基础
│   ├── config/               # 配置
│   └── web/                  # Web基础设施
│
├── employee/                 # 员工中心限界上下文
│   ├── domain/              # 领域层
│   │   ├── model/           # 领域模型
│   │   │   ├── Employee              # 员工聚合根
│   │   │   ├── EmployeeId            # 员工ID值对象
│   │   │   ├── PersonalInfo          # 个人信息值对象
│   │   │   ├── ContactInfo           # 联系信息值对象
│   │   │   ├── EmployeeStatus        # 员工状态枚举
│   │   │   └── *Event                # 领域事件
│   │   └── repository/      # 仓储接口
│   ├── acl/                 # 防腐层（Anti-Corruption Layer）
│   │   └── external/        # 外部系统防腐层
│   │       ├── RecruitmentSystemClient      # 招聘系统客户端
│   │       ├── RecruitmentSystemAdapter     # 招聘系统适配器
│   │       └── RecruitmentSystemService     # 招聘系统防腐服务
│   ├── application/         # 应用层
│   │   ├── EmployeeApplicationService  # 应用服务
│   │   └── dto/                        # 数据传输对象
│   ├── infrastructure/      # 基础设施实现
│   │   └── persistence/     # JPA实现
│   └── interfaces/          # 接口层
│       └── rest/            # REST API
│
├── organization/            # 组织限界上下文
│   ├── domain/
│   │   └── model/
│   │       ├── Department            # 部门聚合根
│   │       ├── Position              # 职位实体
│   │       ├── DepartmentType        # 部门类型
│   │       └── PositionLevel         # 职位级别
│   ├── application/
│   ├── infrastructure/
│   └── interfaces/
│
├── performance/            # 目标与绩效限界上下文
│   ├── domain/
│   │   └── model/
│   │       ├── Goal                  # 目标聚合根(OKR)
│   │       ├── Objective             # 目标项实体
│   │       ├── GoalPeriod            # 目标周期值对象
│   │       └── GoalStatus            # 目标状态
│   ├── application/
│   ├── infrastructure/
│   └── interfaces/
│
├── benefit/               # 福利限界上下文
│   ├── domain/
│   │   └── model/
│   │       ├── Benefit               # 福利聚合根
│   │       ├── BenefitEnrollment     # 福利参加实体
│   │       ├── BenefitType           # 福利类型
│   │       └── BenefitCost           # 福利成本值对象
│   ├── acl/                # 防腐层
│   │   ├── EmployeeContextTranslator  # 员工上下文翻译器
│   │   └── EmployeeContextService     # 员工上下文防腐服务
│   ├── application/
│   ├── infrastructure/
│   └── interfaces/
│
└── culture/              # 企业文化限界上下文
    ├── domain/
    │   └── model/
    │       ├── CultureActivity       # 文化活动聚合根
    │       ├── ActivityParticipation # 活动参与实体
    │       ├── ActivitySchedule      # 活动时间表值对象
    │       └── ActivityType          # 活动类型
    ├── application/
    ├── infrastructure/
    └── interfaces/
```

## 核心概念

### 防腐层 (Anti-Corruption Layer)

系统实现了完整的防腐层模式，保护领域模型不受外部系统影响：

**外部系统防腐层**：
- `RecruitmentSystemAdapter` - 适配外部招聘系统
- `LegacyOrgSystemAdapter` - 适配遗留OA系统

**上下文间防腐层**：
- `EmployeeContextTranslator` - 福利上下文访问员工信息
- `EmployeeContextAdapter` - 绩效上下文访问员工信息
- `EmployeeContextFacade` - 文化上下文访问员工信息

**作用**：
- 🛡️ 保护领域模型不被外部系统"腐蚀"
- 🔄 在不同系统/上下文间进行模型转换
- 🔌 降低系统耦合，便于独立演化

详细设计请参考：[ACL_DESIGN.md](ACL_DESIGN.md)

### 限界上下文 (Bounded Context)

系统划分为5个限界上下文，每个上下文负责特定的业务领域：

1. **员工中心** - 核心领域，管理员工生命周期
2. **组织管理** - 支撑领域，提供组织架构
3. **目标与绩效** - 核心领域，实现OKR和绩效管理
4. **福利管理** - 支撑领域，管理员工福利
5. **企业文化** - 通用领域，组织文化活动

### 聚合根 (Aggregate Root)

每个限界上下文包含一个或多个聚合根：

- `Employee` - 员工聚合，管理员工信息和状态变更
- `Department` - 部门聚合，管理部门和职位
- `Goal` - 目标聚合，实现OKR目标管理
- `Benefit` - 福利聚合，管理福利项目
- `CultureActivity` - 文化活动聚合，管理活动和参与

### 值对象 (Value Object)

- `PersonalInfo` - 个人信息（姓名、身份证、生日等）
- `ContactInfo` - 联系信息（电话、邮箱、地址等）
- `GoalPeriod` - 目标周期（开始、结束日期）
- `BenefitCost` - 福利成本（企业承担、员工承担）
- `ActivitySchedule` - 活动时间表

### 领域事件 (Domain Event)

系统使用领域事件来实现限界上下文之间的解耦：

- `EmployeeHiredEvent` - 员工入职事件
- `EmployeeResignedEvent` - 员工离职事件
- `EmployeeTransferredEvent` - 员工调动事件
- `GoalCompletedEvent` - 目标完成事件
- `ActivityCreatedEvent` - 活动创建事件

## 主要业务流程

### 员工入职流程（入）

1. 创建员工信息（试用期状态）
2. 分配到部门和职位
3. 默认3个月试用期
4. 发布`EmployeeHiredEvent`事件

### 员工转正流程（转）

1. **正常转正**：试用期满后转正
2. **提前转正**：表现优异，提前转正（需提供理由）
3. **延长试用期**：表现不达标，延长1-6个月（总长度≤6个月）
4. 发布`EmployeeStatusChangedEvent`或`ProbationExtendedEvent`事件

### 员工调动流程（调）

1. **部门调动**：跨部门横向调动（需提供理由）
2. **职位晋升**：纵向发展，职级提升（需提供理由）
3. 验证员工状态（必须在职）
4. 发布`EmployeeTransferredEvent`或`EmployeePromotedEvent`事件

### 员工离职流程（离）

1. **主动辞职**：员工主动提出（需提供理由）
2. **被动辞退**：公司辞退（需提供详细理由）
3. **临时停职**：调查期间停职，可复职
4. 发布`EmployeeResignedEvent`事件

详细流程请查看：[EMPLOYEE_LIFECYCLE.md](EMPLOYEE_LIFECYCLE.md)

### OKR目标管理

1. 创建目标（草稿状态）
2. 添加目标项（Objectives）
3. 验证权重总和为100%
4. 激活目标
5. 跟踪进度和完成率
6. 完成目标并计算得分

### 福利参加流程

1. 创建福利项目
2. 员工申请参加
3. 审批通过
4. 生效并激活
5. 跟踪福利使用

### 文化活动流程

1. 创建活动（计划中）
2. 开放报名
3. 员工报名参加
4. 关闭报名
5. 开始活动
6. 完成活动
7. 收集反馈和评分

## API接口示例

### 员工管理

```bash
# 创建员工
POST /api/employees
{
  "firstName": "张",
  "lastName": "三",
  "idCardNumber": "110101199001011234",
  "birthDate": "1990-01-01",
  "gender": "MALE",
  "email": "zhangsan@company.com",
  "phoneNumber": "13800138000",
  "departmentId": "dept-001",
  "positionId": "pos-001"
}

# 查询员工
GET /api/employees/{employeeId}

# 员工转正（正常）
POST /api/employees/{employeeId}/confirm

# 员工提前转正
POST /api/employees/{employeeId}/confirm-early?reason=表现优异

# 延长试用期
POST /api/employees/extend-probation
{
  "employeeId": "emp-001",
  "months": 2,
  "reason": "需要更多时间适应"
}

# 员工调动
POST /api/employees/transfer
{
  "employeeId": "emp-001",
  "newDepartmentId": "dept-002",
  "newPositionId": "pos-002",
  "reason": "组织架构调整"
}

# 员工晋升
POST /api/employees/promote
{
  "employeeId": "emp-001",
  "newPositionId": "pos-senior-001",
  "reason": "工作表现优秀"
}

# 员工离职（主动辞职）
POST /api/employees/resign
{
  "employeeId": "emp-001",
  "resignDate": "2024-12-31",
  "reason": "个人发展原因",
  "resignType": "RESIGNATION"
}

# 员工辞退
POST /api/employees/resign
{
  "employeeId": "emp-001",
  "resignDate": "2024-12-15",
  "reason": "违反公司制度",
  "resignType": "TERMINATION"
}

# 员工停职
POST /api/employees/{employeeId}/suspend?reason=配合调查

# 员工复职
POST /api/employees/{employeeId}/reinstate

# 从招聘系统导入员工（防腐层示例）
POST /api/employees/import/from-recruitment/{candidateId}
```

## 运行项目

### 前置要求

- JDK 17+
- Maven 3.6+

### 启动步骤

```bash
# 编译项目
mvn clean package

# 运行应用
mvn spring-boot:run

# 或直接运行jar
java -jar target/hr-system-1.0.0-SNAPSHOT.jar
```

### 访问地址

- 应用地址: http://localhost:8080/api
- H2控制台: http://localhost:8080/api/h2-console

## DDD设计原则

### 1. 聚合边界清晰

每个聚合根负责维护自己的不变性，通过聚合根访问内部实体。

### 2. 充血模型

业务逻辑封装在领域对象中，而不是在服务层。

### 3. 领域事件驱动

使用领域事件实现限界上下文之间的松耦合。

### 4. 仓储模式

通过Repository接口抽象持久化逻辑。

### 5. 防腐层模式

通过Adapter、Translator、Service等组件保护领域模型。

### 6. 分层架构

- **领域层**: 核心业务逻辑
- **防腐层**: 外部系统适配和上下文翻译
- **应用层**: 用例编排
- **基础设施层**: 技术实现
- **接口层**: API暴露

## 未来扩展

- [x] 实现领域事件发布订阅机制 ⭐已完成
- [ ] 集成消息队列（RabbitMQ/Kafka）
- [ ] 添加事件存储和事件溯源
- [ ] 添加CQRS读写分离
- [ ] 实现事件溯源(Event Sourcing)
- [ ] 添加分布式事务支持(Saga)
- [ ] 集成消息队列
- [ ] 添加完整的单元测试和集成测试
- [ ] 实现审计日志
- [ ] 添加权限控制

## 参考资料

- Eric Evans - 《领域驱动设计》
- Vaughn Vernon - 《实现领域驱动设计》
- Martin Fowler - 企业应用架构模式

## 许可证

MIT License

