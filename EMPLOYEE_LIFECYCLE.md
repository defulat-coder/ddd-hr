# 员工入转调离完整业务流程

## 概述

本文档详细说明员工生命周期管理的四大核心业务流程：
- **入**：入职 (Onboarding)
- **转**：转正 (Confirmation)  
- **调**：调动/晋升 (Transfer/Promotion)
- **离**：离职 (Resignation/Termination)

## 一、员工入职 (入)

### 业务流程

```
候选人 → 录用 → 创建员工档案 → 试用期(3个月) → 分配部门职位
```

### 领域模型

#### 聚合根：Employee

```java
public Employee(EmployeeId id, String employeeNumber, PersonalInfo personalInfo, 
               ContactInfo contactInfo, DepartmentId departmentId, 
               PositionId positionId, LocalDate hireDate)
```

#### 初始状态
- 状态：`PROBATION` (试用期)
- 试用期结束日期：入职日期 + 3个月

#### 领域事件
- `EmployeeHiredEvent` - 员工入职事件

### API接口

#### 1. 创建员工（入职）

```bash
POST /api/employees

{
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
}
```

**响应**：
```json
{
  "success": true,
  "data": {
    "id": "emp-uuid-123",
    "employeeNumber": "2024000001",
    "firstName": "张",
    "lastName": "三",
    "fullName": "张 三",
    "status": "PROBATION",
    "hireDate": "2024-12-01",
    "probationEndDate": "2025-03-01"
  },
  "message": "员工创建成功"
}
```

#### 2. 从招聘系统导入（使用防腐层）

```bash
POST /api/employees/import/from-recruitment/{candidateId}
```

---

## 二、员工转正 (转)

### 业务流程

```
试用期 → 试用期考核 → 转正审批 → 正式员工
```

### 转正场景

#### 2.1 正常转正

**前提条件**：
- 员工状态为 `PROBATION`
- 已到试用期结束日期

**API接口**：
```bash
POST /api/employees/{employeeId}/confirm
```

**响应**：
```json
{
  "success": true,
  "message": "员工转正成功"
}
```

#### 2.2 提前转正

**适用场景**：
- 试用期表现优异
- 特殊人才绿色通道

**前提条件**：
- 员工状态为 `PROBATION`
- 必须提供提前转正理由

**API接口**：
```bash
POST /api/employees/{employeeId}/confirm-early?reason=表现优异，提前转正
```

**领域方法**：
```java
public void confirmEmploymentEarly(String reason)
```

#### 2.3 延长试用期

**适用场景**：
- 试用期表现不达标
- 需要更多时间观察

**业务规则**：
- 员工状态必须为 `PROBATION`
- 延长时间：1-6个月
- 试用期总长度不超过6个月
- 必须提供延长理由

**API接口**：
```bash
POST /api/employees/extend-probation

{
  "employeeId": "emp-uuid-123",
  "months": 2,
  "reason": "需要更多时间适应岗位要求"
}
```

**领域方法**：
```java
public void extendProbation(int months, String reason)
```

**领域事件**：
- `ProbationExtendedEvent` - 试用期延长事件

---

## 三、员工调动 (调)

### 业务流程

```
在职员工 → 调动申请 → 审批 → 部门/职位变更
```

### 调动场景

#### 3.1 部门调动（调）

**适用场景**：
- 跨部门横向调动
- 组织架构调整

**前提条件**：
- 员工状态必须为 `ACTIVE`
- 新部门/职位与当前不同
- 必须提供调动原因

**API接口**：
```bash
POST /api/employees/transfer

{
  "employeeId": "emp-uuid-123",
  "newDepartmentId": "dept-002",
  "newPositionId": "pos-002",
  "reason": "组织架构调整，业务需要"
}
```

**领域方法**：
```java
public void transfer(DepartmentId newDepartmentId, 
                    PositionId newPositionId, 
                    String reason)
```

**领域事件**：
- `EmployeeTransferredEvent` - 员工调动事件
  - oldDepartmentId
  - newDepartmentId
  - oldPositionId
  - newPositionId
  - reason

#### 3.2 晋升（调）

**适用场景**：
- 职级晋升
- 纵向发展

**前提条件**：
- 员工状态必须为 `ACTIVE`
- 新职位与当前不同
- 必须提供晋升原因

**API接口**：
```bash
POST /api/employees/promote

{
  "employeeId": "emp-uuid-123",
  "newPositionId": "pos-senior-001",
  "reason": "工作表现优秀，晋升为高级工程师"
}
```

**领域方法**：
```java
public void promote(PositionId newPositionId, String reason)
```

**领域事件**：
- `EmployeePromotedEvent` - 员工晋升事件

---

## 四、员工离职 (离)

### 业务流程

```
在职员工 → 离职申请 → 审批 → 离职手续 → 档案归档
```

### 离职场景

#### 4.1 主动辞职

**适用场景**：
- 员工主动提出离职
- 合同到期不续签

**前提条件**：
- 员工状态不是 `RESIGNED`
- 离职日期不能早于当前日期
- 必须提供离职原因

**API接口**：
```bash
POST /api/employees/resign

{
  "employeeId": "emp-uuid-123",
  "resignDate": "2024-12-31",
  "reason": "个人发展原因",
  "resignType": "RESIGNATION"
}
```

**领域方法**：
```java
public void resign(LocalDate resignDate, String reason)
```

#### 4.2 辞退

**适用场景**：
- 公司主动辞退
- 违反公司制度

**前提条件**：
- 员工状态不是 `RESIGNED`
- 辞退日期不能早于当前日期
- 必须提供详细辞退原因

**API接口**：
```bash
POST /api/employees/resign

{
  "employeeId": "emp-uuid-123",
  "resignDate": "2024-12-15",
  "reason": "严重违反公司规章制度",
  "resignType": "TERMINATION"
}
```

**领域方法**：
```java
public void terminate(LocalDate terminateDate, String reason)
```

**领域事件**：
- `EmployeeResignedEvent` - 员工离职事件
  - employeeId
  - employeeNumber
  - resignDate
  - reason
  - resignType (RESIGNATION 或 TERMINATION)

#### 4.3 停职

**适用场景**：
- 调查期间
- 暂时离岗

**API接口**：
```bash
POST /api/employees/{employeeId}/suspend?reason=配合调查
```

**领域方法**：
```java
public void suspend(String reason)
```

#### 4.4 复职

**适用场景**：
- 调查结束
- 恢复工作

**API接口**：
```bash
POST /api/employees/{employeeId}/reinstate
```

**领域方法**：
```java
public void reinstate()
```

---

## 五、状态流转图

### 员工状态机

```
                    入职
                     ↓
    ┌──────────> PROBATION (试用期)
    │                │
    │                │ 转正
    │                ↓
延长试用期 ←─────  ACTIVE (在职) ───────┐
    │                │                  │
    │                │ 停职             │ 晋升
    │                ↓                  │
    │            SUSPENDED (停职)        │
    │                │                  │
    │                │ 复职             │
    │                ↓                  │
    └───────────── ACTIVE ◄─────────────┘
                     │
                     │ 离职/辞退
                     ↓
                 RESIGNED (离职)
```

### 状态转换规则

| 当前状态 | 允许转换到 | 业务操作 |
|---------|-----------|---------|
| PROBATION | ACTIVE | 转正（正常/提前） |
| PROBATION | RESIGNED | 试用期离职 |
| ACTIVE | SUSPENDED | 停职 |
| ACTIVE | RESIGNED | 离职/辞退 |
| SUSPENDED | ACTIVE | 复职 |
| SUSPENDED | RESIGNED | 停职期间离职 |
| RESIGNED | - | 终态，不可转换 |

---

## 六、完整业务案例

### 案例1：正常员工生命周期

```bash
# 1. 入职
POST /api/employees
入职日期：2024-01-01
状态：PROBATION
试用期结束：2024-04-01

# 2. 正常转正（3个月后）
POST /api/employees/{id}/confirm
状态：ACTIVE

# 3. 晋升（1年后）
POST /api/employees/promote
原职位：初级工程师
新职位：中级工程师

# 4. 调动（2年后）
POST /api/employees/transfer
原部门：技术一部
新部门：技术二部

# 5. 离职（3年后）
POST /api/employees/resign
离职类型：RESIGNATION
状态：RESIGNED
```

### 案例2：试用期不合格

```bash
# 1. 入职
POST /api/employees
状态：PROBATION

# 2. 延长试用期（2个月后）
POST /api/employees/extend-probation
原试用期结束：2024-04-01
新试用期结束：2024-06-01

# 3. 辞退（试用期结束后）
POST /api/employees/resign
离职类型：TERMINATION
原因：试用期考核不合格
```

### 案例3：特殊人才快速通道

```bash
# 1. 入职
POST /api/employees
状态：PROBATION

# 2. 提前转正（1个月后）
POST /api/employees/{id}/confirm-early?reason=特殊人才，提前转正
状态：ACTIVE

# 3. 快速晋升（半年后）
POST /api/employees/promote
原因：工作表现突出，快速晋升
```

---

## 七、业务规则总结

### 入职规则
✅ 新员工默认试用期3个月
✅ 必须分配部门和职位
✅ 必须验证联系信息格式
✅ 自动生成员工号

### 转正规则
✅ 只有试用期员工才能转正
✅ 正常转正必须到试用期结束日期
✅ 提前转正必须提供理由
✅ 延长试用期不超过6个月总长度

### 调动规则
✅ 只有在职员工才能调动
✅ 新部门/职位必须与当前不同
✅ 必须提供调动原因
✅ 晋升只变更职位，不变更部门

### 离职规则
✅ 已离职员工不能再次离职
✅ 离职日期不能早于当前日期
✅ 必须提供离职原因
✅ 区分主动辞职和被动辞退

---

## 八、API完整列表

| 业务操作 | HTTP方法 | URL | 说明 |
|---------|---------|-----|------|
| 创建员工 | POST | /api/employees | 入职 |
| 导入员工 | POST | /api/employees/import/from-recruitment/{candidateId} | 从招聘系统导入 |
| 正常转正 | POST | /api/employees/{id}/confirm | 试用期满转正 |
| 提前转正 | POST | /api/employees/{id}/confirm-early | 提前转正 |
| 延长试用期 | POST | /api/employees/extend-probation | 延长试用 |
| 部门调动 | POST | /api/employees/transfer | 调动 |
| 职位晋升 | POST | /api/employees/promote | 晋升 |
| 员工离职 | POST | /api/employees/resign | 辞职/辞退 |
| 停职 | POST | /api/employees/{id}/suspend | 停职 |
| 复职 | POST | /api/employees/{id}/reinstate | 复职 |
| 更新联系信息 | PUT | /api/employees/contact | 更新 |
| 查询员工 | GET | /api/employees/{id} | 查询 |
| 查询部门员工 | GET | /api/employees/department/{deptId} | 查询 |
| 查询所有员工 | GET | /api/employees | 查询 |

---

## 九、领域事件汇总

| 事件 | 触发时机 | 包含信息 |
|------|---------|---------|
| EmployeeHiredEvent | 员工入职 | employeeId, employeeNumber, hireDate |
| EmployeeStatusChangedEvent | 状态变更 | employeeId, oldStatus, newStatus |
| ProbationExtendedEvent | 延长试用期 | employeeId, oldEndDate, newEndDate, reason |
| EmployeeTransferredEvent | 部门调动 | employeeId, old/new departmentId, old/new positionId, reason |
| EmployeePromotedEvent | 职位晋升 | employeeId, oldPositionId, newPositionId, reason |
| EmployeeResignedEvent | 员工离职 | employeeId, resignDate, reason, resignType |

---

## 十、扩展功能

### 可以进一步实现的功能

1. **入职流程**
   - 入职手续清单
   - 培训计划
   - 试用期评估

2. **转正流程**
   - 转正评估表
   - 审批工作流
   - 转正面谈记录

3. **调动流程**
   - 调动审批流程
   - 交接清单
   - 新岗位培训

4. **离职流程**
   - 离职审批
   - 离职面谈
   - 交接手续
   - 离职证明

---

## 相关文档

- [README.md](README.md) - 项目整体说明
- [ARCHITECTURE.md](ARCHITECTURE.md) - DDD架构设计
- [ACL_DESIGN.md](ACL_DESIGN.md) - 防腐层设计

---

**版本**: 1.0.0
**最后更新**: 2024-12-01

