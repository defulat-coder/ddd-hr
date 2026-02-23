# AGENTS.md

## 仓库概览
- 技术栈：Spring Boot 3.3.5 + Maven + Java 17 + PostgreSQL + MyBatis-Plus + DDD
- 当前状态：DDD 骨架已落地（组织、人事、考勤、薪酬）
- 必须使用 DDD 领域驱动设计开发
- 必须使用 Lombok

## 目录规范
- `com.cy.hr.<bounded_context>.domain`：实体/值对象/领域服务/仓储接口
- `com.cy.hr.<bounded_context>.application`：应用服务、命令对象
- `com.cy.hr.<bounded_context>.infrastructure`：仓储实现、外部网关实现
- `com.cy.hr.<bounded_context>.interfaces.rest`：控制器与入参
- `com.cy.hr.shared`：跨上下文共享能力（异常、通用组件）

## 当前限界上下文
- `organization`：组织管理（部门）
- `personnel`：人事档案（入职、转正、调岗、离职）
- `attendance`：考勤管理（请假申请、审批、余额）
- `payroll`：薪酬管理（薪资档案、考勤锁定、月度核算）

## 项目实现总结
- 已实现 DDD 四层结构：`domain`、`application`、`infrastructure`、`interfaces.rest`
- 已实现 PostgreSQL 持久化，仓储全部基于 MyBatis-Plus（Mapper + PO + Repository）
- 已实现核心用例：部门创建/查询、员工入转调离、请假申请与审批、薪资档案与月度核算
- 已实现薪资领域服务：`PayrollCalculationDomainService`，应用层仅做编排
- 已集成 OpenAPI3 + Knife4j，文档地址：`/doc.html`，OpenAPI 地址：`/v3/api-docs`
- 已提供数据库初始化脚本：`src/main/resources/schema.sql`、`src/main/resources/data.sql`

## 编码规范
- 必须遵守 DDD 分层，不允许跨层直接调用基础设施实现
- Controller 只调用 Application Service
- Application Service 不承载基础校验以外的业务规则
- 业务规则必须进入 Domain 层
- Repository 接口放在 Domain，实现在 Infrastructure
- 禁止在 Domain 中依赖 Spring 框架注解
- API 入参使用 Command/Request，不直接暴露 Domain 对象作为写入参数
- 接口层禁止直接返回 Domain 对象，必须返回接口层 DTO
- 优先使用 Lombok 注解减少样板代码（如 `@Getter`、`@Setter`、`@Builder`、`@RequiredArgsConstructor`）
- PostgreSQL 仓储必须使用 MyBatis-Plus（`BaseMapper` + PO + Repository 实现）
- Application Service 只负责用例编排，核心业务计算/规则必须下沉到 Domain Service/Entity/VO
- 所有 Java 文件必须包含中文注释（至少文件说明注释）
- 所有实体类（Domain Entity/VO、Persistence PO）字段必须写中文字段注释
- Application/Domain 的公共方法必须有中文注释说明用途与关键规则
- 涉及业务约束的代码必须在方法内补充简短中文注释
- 业务逻辑方法必须写 JavaDoc 方法注释（`/** ... */`），禁止省略

## Swagger 文档规范
- 必须使用 OpenAPI3 + Knife4j 维护接口文档
- 控制器类必须标注 `@Tag`
- 每个接口方法必须标注 `@Operation`
- 路径参数/查询参数必须标注 `@Parameter`
- 所有 Command/Request/Response DTO 字段必须标注 `@Schema(description = \"...\")`
- 接口层只返回 DTO，禁止直接返回 Domain 对象
- 文档访问地址：`/doc.html`
- OpenAPI 地址：`/v3/api-docs`

## 业务规则基线
- 部门层级不超过 5 级
- 员工身份证号唯一
- 员工入职必须包含合同信息
- 请假需校验余额，审批通过后扣减余额
- 薪资核算前必须锁定考勤数据

## 后续演进要求
- 当前仓储统一为 PostgreSQL + MyBatis-Plus 实现，不保留内存仓储
- 审批、通知、审计日志按领域事件方式扩展
- 新增模块必须先定义限界上下文与聚合根，再落代码
