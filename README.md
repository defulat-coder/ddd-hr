# ddd-hr

## 项目简介

基于 DDD 分层实现的人力资源管理系统，包含 5 个限界上下文：`employee`、`organization`、`performance`、`benefit`、`culture`。

## 技术栈

- Java 17
- Spring Boot 3.2.0
- MyBatis-Plus 3.5.5
- H2（默认）/ MySQL（可切换）
- Maven
- Knife4j(OpenAPI 3)

## 目录结构

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

## DDD 分层约定

- `interfaces`：协议层（Controller、入参/出参）
- `application`：用例编排、事务、仓储调用、事件发布
- `domain`：聚合、值对象、领域规则、仓储接口、领域事件
- `infrastructure`：Entity、Mapper、Repository 实现、外部系统实现
- 跨上下文访问优先走 `acl`

## 已实现能力

- 员工：入职、转正、提前转正、延期试用、调动、晋升、离职、停职/复职、导入
- 组织：部门创建、岗位添加、负责人变更、部门启停用
- 绩效：目标创建、目标项管理、激活、完成、取消、查询
- 福利：福利创建、报名、启停用、查询
- 企业文化：活动创建、报名开放/关闭、报名、开始、完成、取消、查询

## API 入口

- 服务根路径：`http://localhost:8080/api`
- OpenAPI JSON：`http://localhost:8080/api/v3/api-docs`
- Knife4j 文档：`http://localhost:8080/api/doc.html`
- H2 控制台：`http://localhost:8080/api/h2-console`

主要资源路径：

- `/employees`
- `/employees/import`
- `/departments`
- `/goals`
- `/benefits`
- `/activities`

## 本地运行

```bash
mvn clean compile
mvn spring-boot:run
```

打包运行：

```bash
mvn clean package
java -jar target/ddd-hr-1.0.0-SNAPSHOT.jar
```

## 配置说明

默认配置文件：`src/main/resources/application.yml`

- 端口：`8080`
- Context Path：`/api`
- 默认数据源：`jdbc:h2:mem:hrdb`
- SQL 初始化：`spring.sql.init.mode=always`
- 初始化脚本：`src/main/resources/schema.sql`

## 测试

```bash
mvn test
```

## 设计文档

- [项目总览](docs/PROJECT_OVERVIEW.md)
- [架构设计](docs/ARCHITECTURE.md)
- [领域模型总览](docs/DOMAIN_MODEL_OVERVIEW.md)
- [领域事件设计](docs/DOMAIN_EVENT_DESIGN.md)
- [事件示例](docs/EVENT_EXAMPLES.md)
- [ACL 设计](docs/ACL_DESIGN.md)
- [ACL 示例](docs/ACL_EXAMPLES.md)
- [员工生命周期](docs/EMPLOYEE_LIFECYCLE.md)
- [工厂模式设计](docs/FACTORY_PATTERN.md)
- [工厂模式示例](docs/FACTORY_EXAMPLES.md)
- [快速开始](docs/QUICKSTART.md)
