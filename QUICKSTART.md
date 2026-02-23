# 快速启动指南

## 1. 环境准备

### 必需软件

- **JDK 17+**
  ```bash
  java -version  # 确认版本
  ```

- **Maven 3.6+**
  ```bash
  mvn -version  # 确认版本
  ```

### 可选软件

- **IntelliJ IDEA** 或 **Eclipse** (推荐使用IDEA)
- **Postman** 或 **curl** (用于API测试)

## 2. 项目构建

### 2.1 克隆代码

```bash
cd /Users/cy/Documents/cursor/ddd
```

### 2.2 编译项目

```bash
# 清理并编译
mvn clean compile

# 打包（跳过测试）
mvn clean package -DskipTests

# 完整构建（包含测试）
mvn clean install
```

## 3. 运行应用

### 3.1 方式一：使用Maven插件

```bash
mvn spring-boot:run
```

### 3.2 方式二：运行JAR包

```bash
java -jar target/hr-system-1.0.0-SNAPSHOT.jar
```

### 3.3 方式三：在IDE中运行

1. 打开 `HrSystemApplication.java`
2. 右键选择 "Run" 或 "Debug"

## 4. 验证运行

### 4.1 检查应用状态

访问健康检查端点：
```bash
curl http://localhost:8080/api/actuator/health
```

### 4.2 访问H2数据库控制台

浏览器访问：http://localhost:8080/api/h2-console

连接信息：
- JDBC URL: `jdbc:h2:mem:hrdb`
- Username: `sa`
- Password: (留空)

## 5. API测试示例

### 5.1 创建员工

```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "张",
    "lastName": "三",
    "idCardNumber": "110101199001011234",
    "birthDate": "1990-01-01",
    "gender": "MALE",
    "email": "zhangsan@company.com",
    "phoneNumber": "13800138000",
    "address": "北京市朝阳区",
    "emergencyContact": "李四",
    "emergencyPhone": "13900139000",
    "departmentId": "dept-001",
    "positionId": "pos-001"
  }'
```

响应示例：
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "employeeNumber": "2024000001",
    "firstName": "张",
    "lastName": "三",
    "fullName": "张 三",
    "status": "PROBATION",
    "hireDate": "2024-12-01"
  },
  "message": "员工创建成功",
  "timestamp": "2024-12-01T10:00:00"
}
```

### 5.2 查询员工

```bash
# 根据ID查询
curl http://localhost:8080/api/employees/{employeeId}

# 根据工号查询
curl http://localhost:8080/api/employees/number/2024000001

# 查询所有员工
curl http://localhost:8080/api/employees
```

### 5.3 员工转正

```bash
curl -X POST http://localhost:8080/api/employees/{employeeId}/confirm
```

### 5.4 员工调动

```bash
curl -X POST http://localhost:8080/api/employees/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "550e8400-e29b-41d4-a716-446655440000",
    "newDepartmentId": "dept-002",
    "newPositionId": "pos-002"
  }'
```

### 5.5 更新联系信息

```bash
curl -X PUT http://localhost:8080/api/employees/contact \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "newemail@company.com",
    "phoneNumber": "13800138001",
    "address": "北京市海淀区",
    "emergencyContact": "王五",
    "emergencyPhone": "13900139001"
  }'
```

### 5.6 员工离职

```bash
curl -X POST "http://localhost:8080/api/employees/{employeeId}/resign?resignDate=2024-12-31"
```

## 6. 使用Postman测试

### 6.1 导入Postman Collection

创建一个新的Collection，添加以下请求：

**1. 创建员工**
- Method: POST
- URL: `http://localhost:8080/api/employees`
- Body: JSON (见上面的示例)

**2. 查询员工**
- Method: GET
- URL: `http://localhost:8080/api/employees/{{employeeId}}`

**3. 员工转正**
- Method: POST
- URL: `http://localhost:8080/api/employees/{{employeeId}}/confirm`

### 6.2 使用环境变量

在Postman中设置环境变量：
```json
{
  "baseUrl": "http://localhost:8080/api",
  "employeeId": "从创建响应中获取的ID"
}
```

## 7. 数据库初始化

### 7.1 自动创建表结构

应用启动时，JPA会自动创建表结构（配置：`spring.jpa.hibernate.ddl-auto=update`）

### 7.2 初始化数据（可选）

创建 `data.sql` 用于初始化数据：

```sql
-- 初始化部门
INSERT INTO departments (id, name, code, type, active, created_at, updated_at, version) 
VALUES ('dept-001', '技术部', 'TECH', 'DEPARTMENT', true, NOW(), NOW(), 0);

-- 初始化职位
-- 注意：需要先实现Department的JPA Entity
```

## 8. 配置说明

### 8.1 应用配置 (application.yml)

```yaml
server:
  port: 8080  # 修改端口

spring:
  datasource:
    url: jdbc:h2:mem:hrdb  # 使用内存数据库
    # url: jdbc:mysql://localhost:3306/hrdb  # 切换到MySQL
  
  jpa:
    show-sql: true  # 显示SQL日志
    hibernate:
      ddl-auto: update  # 自动更新表结构
```

### 8.2 使用MySQL（可选）

1. 修改 `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hrdb?useSSL=false&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_password
  
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

2. 创建数据库:
```sql
CREATE DATABASE hrdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 9. 开发模式

### 9.1 启用热重载

添加Spring Boot DevTools依赖（已包含在pom.xml中）：

在IDE中：
- IntelliJ IDEA: 启用 "Build project automatically"
- Eclipse: 保存时自动编译

### 9.2 调试模式

```bash
# Maven调试模式
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# JAR调试模式
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar target/hr-system-1.0.0-SNAPSHOT.jar
```

在IDE中连接到端口5005进行远程调试。

## 10. 常见问题

### Q1: 端口被占用

**问题**: `Port 8080 was already in use`

**解决**:
```bash
# 方式1: 修改application.yml中的端口
server.port: 8081

# 方式2: 找到并杀死占用端口的进程
lsof -i :8080
kill -9 <PID>
```

### Q2: 找不到主类

**问题**: `Could not find or load main class`

**解决**:
```bash
# 清理并重新编译
mvn clean compile
mvn clean package
```

### Q3: Lombok不工作

**问题**: `Cannot resolve method/symbol`

**解决**:
1. 确保IDE安装了Lombok插件
2. 启用注解处理器：
   - IntelliJ: Settings → Build → Compiler → Annotation Processors → Enable
   - Eclipse: 安装Lombok.jar

### Q4: 数据库连接失败

**问题**: `Cannot create connection`

**解决**:
- 检查数据库是否运行
- 确认连接字符串正确
- 验证用户名密码

## 11. 下一步

完成快速启动后，您可以：

1. 查看 [README.md](README.md) 了解项目概述
2. 阅读 [ARCHITECTURE.md](ARCHITECTURE.md) 理解DDD架构
3. 实现其他领域的API接口
4. 添加单元测试和集成测试
5. 实现领域事件发布订阅机制
6. 添加Spring Security进行权限控制

## 12. 获取帮助

如果遇到问题：

1. 查看应用日志: `logs/application.log`
2. 查看Spring Boot文档: https://spring.io/projects/spring-boot
3. 查看DDD参考资料: 
   - Eric Evans《领域驱动设计》
   - Vaughn Vernon《实现领域驱动设计》

祝您使用愉快！🎉

