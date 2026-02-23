# HR系统项目概览

## 📊 项目统计

### 代码规模

- **总文件数**: 75+ Java源文件
- **代码行数**: 约4000+ LOC
- **限界上下文数**: 5个
- **聚合根数**: 5个
- **值对象数**: 10+
- **领域事件数**: 10+
- **防腐层数**: 8个（3个基础接口 + 5个实现）

### 目录结构

```
hr-system/
├── 📄 文档 (6个)
│   ├── README.md              # 项目说明
│   ├── ARCHITECTURE.md        # 架构设计文档
│   ├── QUICKSTART.md          # 快速启动指南
│   ├── PROJECT_OVERVIEW.md    # 项目概览（本文档）
│   ├── ACL_DESIGN.md          # 防腐层设计文档
│   └── ACL_EXAMPLES.md        # 防腐层使用示例
│
├── 🔧 配置文件
│   ├── pom.xml               # Maven配置
│   ├── .gitignore            # Git忽略配置
│   └── application.yml       # Spring Boot配置
│
└── 💻 源代码
    └── src/main/java/com/company/hr/
        ├── 🌐 shared/                    # 共享内核 (10个文件)
        │   ├── domain/                   # 领域基础 (5个)
        │   ├── acl/                      # 防腐层基础 (3个) ⭐新增
        │   └── exception/                # 异常定义 (2个)
        ├── 🏗️  infrastructure/           # 基础设施层 (4个文件)
        ├── 👤 employee/                  # 员工中心 (22个文件)
        │   ├── acl/external/             # 外部系统防腐层 (4个) ⭐新增
        │   └── ...
        ├── 🏢 organization/              # 组织管理 (10个文件)
        │   ├── acl/legacy/               # 遗留系统防腐层 (1个) ⭐新增
        │   └── ...
        ├── 🎯 performance/               # 目标与绩效 (12个文件)
        │   ├── acl/                      # 上下文防腐层 (1个) ⭐新增
        │   └── ...
        ├── 💰 benefit/                   # 福利管理 (12个文件)
        │   ├── acl/                      # 上下文防腐层 (2个) ⭐新增
        │   └── ...
        └── 🎨 culture/                   # 企业文化 (13个文件)
            ├── acl/                      # 上下文防腐层 (1个) ⭐新增
            └── ...
```

## 📦 模块详细说明

### 1. 共享内核 (Shared Kernel)

**位置**: `com.company.hr.shared`

**文件列表**:
```
shared/
├── domain/
│   ├── AggregateRoot.java      # 聚合根基类
│   ├── Entity.java             # 实体基类
│   ├── ValueObject.java        # 值对象接口
│   ├── DomainEvent.java        # 领域事件基类
│   └── Repository.java         # 仓储接口
├── acl/                        ⭐ 防腐层基础（新增）
│   ├── ExternalSystemAdapter.java  # 外部系统适配器接口
│   ├── Translator.java             # 翻译器接口
│   └── AntiCorruptionService.java  # 防腐服务标记接口
└── exception/
    ├── DomainException.java    # 领域异常
    └── BusinessException.java  # 业务异常
```

### 2. 基础设施层 (Infrastructure)

**位置**: `com.company.hr.infrastructure`

**文件列表**:
```
infrastructure/
├── persistence/
│   └── BaseJpaEntity.java          # JPA实体基类
├── config/
│   └── JpaConfig.java              # JPA配置
└── web/
    ├── ApiResponse.java            # 统一响应格式
    └── GlobalExceptionHandler.java # 全局异常处理
```

### 3. 员工中心 (Employee Context)

**位置**: `com.company.hr.employee`

**核心类**:
- 聚合根: `Employee`
- 值对象: `EmployeeId`, `PersonalInfo`, `ContactInfo`
- 枚举: `Gender`, `EmployeeStatus`
- 事件: `EmployeeHiredEvent`, `EmployeeResignedEvent`, `EmployeeTransferredEvent`, `EmployeeStatusChangedEvent`

**文件结构**:
```
employee/
├── acl/                        ⭐ 防腐层（新增）
│   └── external/
│       ├── RecruitmentSystemClient.java
│       ├── RecruitmentSystemClientImpl.java
│       ├── RecruitmentSystemAdapter.java
│       └── RecruitmentSystemService.java
├── domain/
│   ├── model/ (10个文件)
│   │   ├── Employee.java
│   │   ├── EmployeeId.java
│   │   ├── PersonalInfo.java
│   │   ├── ContactInfo.java
│   │   ├── Gender.java
│   │   ├── EmployeeStatus.java
│   │   ├── EmployeeHiredEvent.java
│   │   ├── EmployeeStatusChangedEvent.java
│   │   ├── EmployeeTransferredEvent.java
│   │   └── EmployeeResignedEvent.java
│   └── repository/
│       └── EmployeeRepository.java
├── application/
│   ├── EmployeeApplicationService.java
│   └── dto/
│       ├── CreateEmployeeCommand.java
│       ├── TransferEmployeeCommand.java
│       ├── UpdateContactCommand.java
│       └── EmployeeDTO.java
├── infrastructure/
│   └── persistence/
│       ├── EmployeeJpaEntity.java
│       ├── EmployeeJpaRepository.java
│       └── EmployeeRepositoryImpl.java
└── interfaces/
    └── rest/
        └── EmployeeController.java
```

### 4. 组织管理 (Organization Context)

**位置**: `com.company.hr.organization`

**核心类**:
- 聚合根: `Department`
- 实体: `Position`
- 值对象: `DepartmentId`, `PositionId`
- 枚举: `DepartmentType`, `PositionLevel`
- 事件: `DepartmentCreatedEvent`, `DepartmentManagerChangedEvent`

**文件结构**:
```
organization/
└── domain/
    ├── model/ (8个文件)
    │   ├── Department.java
    │   ├── DepartmentId.java
    │   ├── DepartmentType.java
    │   ├── DepartmentCreatedEvent.java
    │   ├── DepartmentManagerChangedEvent.java
    │   ├── Position.java
    │   ├── PositionId.java
    │   └── PositionLevel.java
    └── repository/
        └── DepartmentRepository.java
```

### 5. 目标与绩效 (Performance Context)

**位置**: `com.company.hr.performance`

**核心类**:
- 聚合根: `Goal`
- 实体: `Objective`
- 值对象: `GoalId`, `ObjectiveId`, `GoalPeriod`
- 枚举: `GoalStatus`, `ObjectiveStatus`
- 事件: `GoalCreatedEvent`, `GoalActivatedEvent`, `GoalCompletedEvent`

**文件结构**:
```
performance/
└── domain/
    ├── model/ (10个文件)
    │   ├── Goal.java
    │   ├── GoalId.java
    │   ├── GoalStatus.java
    │   ├── GoalPeriod.java
    │   ├── Objective.java
    │   ├── ObjectiveId.java
    │   ├── ObjectiveStatus.java
    │   ├── GoalCreatedEvent.java
    │   ├── GoalActivatedEvent.java
    │   └── GoalCompletedEvent.java
    └── repository/
        └── GoalRepository.java
```

### 6. 福利管理 (Benefit Context)

**位置**: `com.company.hr.benefit`

**核心类**:
- 聚合根: `Benefit`
- 实体: `BenefitEnrollment`
- 值对象: `BenefitId`, `EnrollmentId`, `BenefitCost`
- 枚举: `BenefitType`, `EnrollmentStatus`
- 事件: `BenefitCreatedEvent`, `EmployeeEnrolledInBenefitEvent`
- 防腐层: `EmployeeContextTranslator`, `EmployeeContextService` ⭐

**文件结构**:
```
benefit/
├── acl/                        ⭐ 防腐层（新增）
│   ├── EmployeeContextTranslator.java
│   └── EmployeeContextService.java
└── domain/
    ├── model/ (9个文件)
    │   ├── Benefit.java
    │   ├── BenefitId.java
    │   ├── BenefitType.java
    │   ├── BenefitCost.java
    │   ├── BenefitEnrollment.java
    │   ├── EnrollmentId.java
    │   ├── EnrollmentStatus.java
    │   ├── BenefitCreatedEvent.java
    │   └── EmployeeEnrolledInBenefitEvent.java
    └── repository/
        └── BenefitRepository.java
```

### 7. 企业文化 (Culture Context)

**位置**: `com.company.hr.culture`

**核心类**:
- 聚合根: `CultureActivity`
- 实体: `ActivityParticipation`
- 值对象: `ActivityId`, `ParticipationId`, `ActivitySchedule`
- 枚举: `ActivityType`, `ActivityStatus`, `ParticipationStatus`
- 事件: `ActivityCreatedEvent`, `EmployeeRegisteredForActivityEvent`, `ActivityCompletedEvent`

**文件结构**:
```
culture/
└── domain/
    ├── model/ (11个文件)
    │   ├── CultureActivity.java
    │   ├── ActivityId.java
    │   ├── ActivityType.java
    │   ├── ActivityStatus.java
    │   ├── ActivitySchedule.java
    │   ├── ActivityParticipation.java
    │   ├── ParticipationId.java
    │   ├── ParticipationStatus.java
    │   ├── ActivityCreatedEvent.java
    │   ├── EmployeeRegisteredForActivityEvent.java
    │   └── ActivityCompletedEvent.java
    └── repository/
        └── CultureActivityRepository.java
```

## 🎯 核心设计模式

### 1. 防腐层模式 (Anti-Corruption Layer Pattern) ⭐新增
保护领域模型不受外部系统影响，实现系统间的适配和翻译。

**实现类型**:
- **外部系统防腐层**: `RecruitmentSystemAdapter` (适配招聘系统)
- **上下文间防腐层**: `EmployeeContextTranslator` (上下文翻译)
- **遗留系统防腐层**: `LegacyOrgSystemAdapter` (适配旧OA系统)

详见：[ACL_DESIGN.md](ACL_DESIGN.md)

### 2. 聚合模式 (Aggregate Pattern)
每个限界上下文都有明确的聚合根，负责维护聚合内的一致性。

### 3. 仓储模式 (Repository Pattern)
所有聚合根都有对应的Repository接口，用于持久化。

### 4. 值对象模式 (Value Object Pattern)
使用不可变对象表达领域概念，如PersonalInfo、ContactInfo等。

### 5. 领域事件模式 (Domain Event Pattern)
关键业务操作会发布领域事件，实现限界上下文间的解耦。

### 6. 分层架构模式
- Domain Layer: 领域逻辑
- Anti-Corruption Layer: 外部适配和翻译 ⭐
- Application Layer: 用例编排
- Infrastructure Layer: 技术实现
- Interface Layer: API暴露

## 📈 技术特性

### DDD战略设计
- ✅ 限界上下文划分清晰
- ✅ 上下文映射关系明确
- ✅ 核心域、支撑域、通用域识别

### DDD战术设计
- ✅ 聚合根设计
- ✅ 实体和值对象区分
- ✅ 领域事件应用
- ✅ 仓储模式实现
- ✅ 充血领域模型

### 技术实现
- ✅ Spring Boot 3.2
- ✅ Spring Data JPA
- ✅ RESTful API
- ✅ 全局异常处理
- ✅ 统一响应格式
- ✅ Lombok简化代码

## 🔄 业务流程完整性

### 员工生命周期（完整的入转调离）
```
入职 → 试用期 → [提前转正/延长试用期] → 转正 → 在职 → [调动/晋升] → 离职/辞退
 ✓      ✓              ✓                  ✓      ✓          ✓          ✓

补充功能：停职 ↔ 复职
```

### 目标管理流程
```
创建目标 → 添加KR → 激活 → 跟踪进度 → 完成评分
   ✓        ✓       ✓       ✓         ✓
```

### 福利管理流程
```
创建福利 → 员工申请 → 审批 → 生效 → 管理
   ✓         ✓        ✓      ✓      ✓
```

### 活动管理流程
```
计划活动 → 开放报名 → 员工参加 → 完成 → 反馈评分
   ✓         ✓         ✓        ✓       ✓
```

## 🚀 已实现功能

### 员工中心（完整的入转调离）
- [x] **入**：员工入职
- [x] **入**：从招聘系统导入（防腐层）
- [x] **转**：正常转正
- [x] **转**：提前转正 ⭐新增
- [x] **转**：延长试用期 ⭐新增
- [x] **调**：部门调动
- [x] **调**：职位晋升 ⭐新增
- [x] **调**：员工停职/复职 ⭐新增
- [x] **离**：主动辞职 ⭐增强
- [x] **离**：公司辞退 ⭐新增
- [x] 更新联系信息
- [x] 员工查询
- [x] REST API（14个接口）

### 组织管理
- [x] 部门聚合根设计
- [x] 职位实体设计
- [x] 组织层级支持
- [x] 部门负责人管理
- [x] 职位编制管理

### 目标与绩效
- [x] OKR目标模型
- [x] 目标项(Objective)管理
- [x] 权重验证
- [x] 完成率计算
- [x] 得分计算

### 福利管理
- [x] 福利聚合根设计
- [x] 福利参加管理
- [x] 福利成本计算
- [x] 参加状态流转

### 企业文化
- [x] 文化活动管理
- [x] 活动报名
- [x] 参与管理
- [x] 反馈评分
- [x] 人数限制

### 防腐层 ⭐新增
- [x] 共享防腐层基础接口
- [x] 外部招聘系统适配器
- [x] 遗留OA系统适配器
- [x] 上下文间翻译器（福利→员工）
- [x] 上下文间翻译器（绩效→员工）
- [x] 上下文间翻译器（文化→员工）

### 领域事件发布订阅 ⭐⭐新增
- [x] 事件发布器（DomainEventPublisher）
- [x] 员工事件处理器（6种事件）
- [x] 绩效事件处理器（跨上下文）
- [x] 福利事件处理器（跨上下文）
- [x] 文化事件处理器（跨上下文）
- [x] 异步事件处理（@Async）
- [x] 完整的事件追踪日志

## 📋 待扩展功能

### 短期计划
- [ ] 完善其他领域的应用服务
- [ ] 完善其他领域的REST API
- [ ] 实现JPA持久化（所有聚合）
- [ ] 添加参数校验
- [ ] 添加API文档(Swagger)

### 中期计划
- [ ] 实现领域事件发布订阅
- [ ] 添加单元测试
- [ ] 添加集成测试
- [ ] 实现CQRS读写分离
- [ ] 添加缓存支持

### 长期计划
- [ ] 实现事件溯源(Event Sourcing)
- [ ] 添加分布式事务(Saga)
- [ ] 集成消息队列(RabbitMQ/Kafka)
- [ ] 实现微服务拆分
- [ ] 添加Spring Security权限控制
- [ ] 实现审计日志
- [ ] 性能优化

## 📚 相关文档

| 文档 | 说明 | 适合读者 |
|------|------|---------|
| [README.md](README.md) | 项目介绍、技术栈、API示例 | 所有人 |
| [ARCHITECTURE.md](ARCHITECTURE.md) | DDD架构设计、领域模型详解 | 架构师、开发者 |
| [QUICKSTART.md](QUICKSTART.md) | 快速启动指南、环境配置 | 新手开发者 |
| PROJECT_OVERVIEW.md | 项目概览、文件统计（本文档） | 项目经理、架构师 |
| [ACL_DESIGN.md](ACL_DESIGN.md) ⭐ | 防腐层设计详解 | 架构师、高级开发者 |
| [ACL_EXAMPLES.md](ACL_EXAMPLES.md) ⭐ | 防腐层使用示例 | 开发者 |
| [EMPLOYEE_LIFECYCLE.md](EMPLOYEE_LIFECYCLE.md) ⭐⭐ | 员工入转调离完整业务 | 所有开发者 |
| [DOMAIN_EVENT_DESIGN.md](DOMAIN_EVENT_DESIGN.md) ⭐⭐⭐ | 领域事件发布订阅设计 | 架构师、开发者 |
| [DOMAIN_MODEL_OVERVIEW.md](DOMAIN_MODEL_OVERVIEW.md) | 领域模型完整结构 | 开发者 |

## 🎓 学习路径

### 新手入门
1. 阅读 [README.md](README.md) 了解项目
2. 跟随 [QUICKSTART.md](QUICKSTART.md) 启动项目
3. 使用Postman测试API
4. 查看H2数据库数据

### 深入理解
1. 阅读 [ARCHITECTURE.md](ARCHITECTURE.md) 理解DDD设计
2. 查看领域模型代码
3. 理解聚合边界和业务规则
4. 学习领域事件应用

### 高级开发
1. 实现新的限界上下文
2. 添加复杂的业务规则
3. 实现领域事件发布订阅
4. 优化性能和扩展性

## 🏆 项目亮点

1. **标准的DDD分层架构**
   - 领域层、应用层、基础设施层、接口层职责清晰

2. **完整的限界上下文**
   - 5个业务领域，各司其职，边界明确

3. **充血的领域模型**
   - 业务逻辑封装在领域对象中
   - 避免贫血模型的问题

4. **丰富的领域事件**
   - 10+领域事件，支持事件驱动架构

5. **清晰的代码组织**
   - 按限界上下文组织代码
   - 每个上下文独立完整

6. **完善的防腐层设计** ⭐
   - 8个防腐层实现
   - 外部系统适配
   - 上下文间解耦
   - 遗留系统迁移支持

7. **完整的事件驱动架构** ⭐⭐
   - 事件发布订阅机制
   - 4个事件处理器
   - 异步事件处理
   - 跨上下文通信
   - 完整的事件追踪

8. **良好的可扩展性**
   - 易于添加新的限界上下文
   - 易于实现新的业务规则
   - 易于集成新的外部系统
   - 易于添加新的事件处理器

## 📞 联系方式

如有问题或建议，欢迎通过以下方式联系：

- Issue: 在项目中创建Issue
- Email: your-email@company.com
- 文档: 查看详细设计文档

---

**项目状态**: 🟢 活跃开发中

**最后更新**: 2024-12-01

**版本**: 1.0.0-SNAPSHOT

