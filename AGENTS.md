# AGENTS.md

## 项目事实

- 技术栈：Java 17、Spring Boot 3、Spring Data JPA、Maven
- 根包：`com.company.hr`
- 当前是 **DDD 风格分层**，非严格纯领域实现
- 限界上下文：`employee`、`organization`、`performance`、`benefit`、`culture`

## 目录约定

```text
src/main/java/com/company/hr/
  shared/                # 共享内核（domain/acl/exception/event）
  infrastructure/        # 全局基础设施（config/persistence/web）
  employee/              # 员工上下文
  organization/          # 组织上下文
  performance/           # 绩效上下文
  benefit/               # 福利上下文
  culture/               # 企业文化上下文
```

## 分层规则（必须遵守）

1. `interfaces` 只负责入参/出参和协议层，不写业务规则。
2. `application` 负责用例编排、事务、仓储调用、事件发布。
3. `domain` 放业务规则、聚合、值对象、领域事件、仓储接口。
4. `infrastructure` 放 JPA 实体、Repository 实现、外部系统实现。
5. 跨上下文访问优先走 `acl`，不要直接依赖对方应用层实现。

## 代码变更规则

1. 新业务优先落在已有上下文中，避免新增无边界的 shared 逻辑。
2. 新增持久化时：
   - 在 `domain/repository` 定义接口
   - 在 `infrastructure/persistence` 实现
   - 应用层通过仓储接口使用
3. 新增用例时：
   - `application/dto` 定义 Command/DTO
   - `application/*ApplicationService` 编排
   - `interfaces/rest/*Controller` 暴露接口
4. 领域事件通过 `shared.event.DomainEventPublisher` 发布。
5. 不要把 Web/JPA 注解加到 `domain/model`。

## 本地命令

```bash
mvn clean compile
mvn test
```

