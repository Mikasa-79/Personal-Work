# CampusHub API 规范文档

## 1. 通用约定

### 1.1 基础信息

| 项 | 说明 |
|----|------|
| API 风格 | RESTful |
| 数据格式 | JSON |
| 基础路径 | `/api` |
| 认证方式 | 登录后使用 `Authorization: Bearer <token>` |
| 时间格式 | ISO-8601，例如 `2026-05-16T14:30:00` |
| 分页参数 | `page` 从 1 开始，`size` 默认 10，最大 50 |

### 1.2 统一成功响应

```json
{
  "code": "OK",
  "message": "success",
  "data": {}
}
```

分页响应：

```json
{
  "code": "OK",
  "message": "success",
  "data": {
    "items": [],
    "page": 1,
    "size": 10,
    "total": 0,
    "pages": 0
  }
}
```

### 1.3 统一失败响应

```json
{
  "code": "VALIDATION_ERROR",
  "message": "请求参数不合法",
  "details": [
    {
      "field": "title",
      "reason": "标题不能为空"
    }
  ]
}
```

### 1.4 通用错误码

| 错误码 | HTTP 状态码 | 说明 |
|--------|-------------|------|
| `VALIDATION_ERROR` | 400 | 请求参数校验失败 |
| `UNAUTHORIZED` | 401 | 未登录或 token 无效 |
| `FORBIDDEN` | 403 | 当前用户无权执行该操作 |
| `NOT_FOUND` | 404 | 资源不存在 |
| `CONFLICT` | 409 | 资源状态冲突，例如需求已被接单 |
| `BUSINESS_ERROR` | 422 | 业务规则不满足，例如非法订单流转 |
| `INTERNAL_ERROR` | 500 | 服务端异常 |

## 2. 用户认证接口

### 2.1 用户注册

| 项 | 内容 |
|----|------|
| URL | `/api/auth/register` |
| 方法 | `POST` |
| 是否认证 | 否 |

请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `studentNo` | string | 是 | 学号，唯一，6-32 位 |
| `password` | string | 是 | 密码，8-64 位 |
| `nickname` | string | 是 | 昵称，2-30 位 |
| `college` | string | 否 | 学院 |
| `contact` | string | 否 | 联系方式，建议前端提示隐私风险 |

请求示例：

```json
{
  "studentNo": "20260001",
  "password": "Passw0rd123",
  "nickname": "小陈",
  "college": "软件学院",
  "contact": "chen@example.edu"
}
```

成功响应：

```json
{
  "code": "OK",
  "message": "success",
  "data": {
    "token": "jwt-token",
    "user": {
      "id": 1,
      "studentNo": "20260001",
      "nickname": "小陈",
      "role": "REQUESTER",
      "admin": false,
      "creditScore": 100
    }
  }
}
```

可能错误：

| 错误码 | 说明 |
|--------|------|
| `VALIDATION_ERROR` | 学号、密码或昵称格式错误 |
| `CONFLICT` | 学号已注册 |

### 2.2 用户登录

| 项 | 内容 |
|----|------|
| URL | `/api/auth/login` |
| 方法 | `POST` |
| 是否认证 | 否 |

请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `studentNo` | string | 是 | 学号 |
| `password` | string | 是 | 密码 |

成功响应与注册接口相同。

可能错误：

| 错误码 | 说明 |
|--------|------|
| `VALIDATION_ERROR` | 学号或密码为空 |
| `UNAUTHORIZED` | 学号或密码错误 |
| `FORBIDDEN` | 用户已被禁用 |

### 2.3 查看当前用户

| 项 | 内容 |
|----|------|
| URL | `/api/users/me` |
| 方法 | `GET` |
| 是否认证 | 是 |

成功响应：

```json
{
  "code": "OK",
  "message": "success",
  "data": {
    "id": 1,
    "studentNo": "20260001",
    "nickname": "小陈",
    "avatarUrl": null,
    "college": "软件学院",
    "contact": "chen@example.edu",
    "role": "REQUESTER",
    "admin": false,
    "status": "ACTIVE",
    "creditScore": 100
  }
}
```

## 3. 需求接口

### 3.1 发布需求

| 项 | 内容 |
|----|------|
| URL | `/api/requests` |
| 方法 | `POST` |
| 是否认证 | 是 |

请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `category` | string | 是 | `EXPRESS_PICKUP`、`STUDY_TUTORING`、`SECOND_HAND`、`TEAM_UP`、`OTHER` |
| `title` | string | 是 | 标题，5-80 字 |
| `description` | string | 是 | 描述，10-2000 字 |
| `location` | string | 是 | 校园内地点，2-100 字 |
| `expectedTime` | string | 是 | 期望完成时间 |
| `reward` | number | 否 | 报酬或交换条件金额，允许为 0 |

成功响应：

```json
{
  "code": "OK",
  "message": "success",
  "data": {
    "id": 101,
    "publisherId": 1,
    "category": "EXPRESS_PICKUP",
    "title": "帮取快递到宿舍楼下",
    "description": "菜鸟驿站小件快递，今晚 8 点前送达。",
    "location": "东区菜鸟驿站",
    "expectedTime": "2026-05-17T20:00:00",
    "reward": 5.00,
    "status": "OPEN",
    "auditStatus": "PENDING",
    "createdAt": "2026-05-16T14:30:00"
  }
}
```

可能错误：

| 错误码 | 说明 |
|--------|------|
| `VALIDATION_ERROR` | 标题、描述、分类、地点或时间不合法 |
| `UNAUTHORIZED` | 未登录 |
| `FORBIDDEN` | 用户已被禁用 |

### 3.2 浏览需求列表

| 项 | 内容 |
|----|------|
| URL | `/api/requests` |
| 方法 | `GET` |
| 是否认证 | 否；未登录只能浏览已审核公开数据 |

查询参数：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `keyword` | string | 否 | 标题或描述关键词 |
| `category` | string | 否 | 需求分类 |
| `status` | string | 否 | 默认 `OPEN` |
| `location` | string | 否 | 地点关键词 |
| `fromTime` | string | 否 | 期望时间起点 |
| `toTime` | string | 否 | 期望时间终点 |
| `page` | integer | 否 | 页码，默认 1 |
| `size` | integer | 否 | 每页数量，默认 10 |

成功响应：

```json
{
  "code": "OK",
  "message": "success",
  "data": {
    "items": [
      {
        "id": 101,
        "category": "EXPRESS_PICKUP",
        "title": "帮取快递到宿舍楼下",
        "location": "东区菜鸟驿站",
        "expectedTime": "2026-05-17T20:00:00",
        "reward": 5.00,
        "status": "OPEN",
        "publisher": {
          "id": 1,
          "nickname": "小陈",
          "creditScore": 100
        },
        "createdAt": "2026-05-16T14:30:00"
      }
    ],
    "page": 1,
    "size": 10,
    "total": 1,
    "pages": 1
  }
}
```

可能错误：

| 错误码 | 说明 |
|--------|------|
| `VALIDATION_ERROR` | 分页、时间范围或枚举值不合法 |

### 3.3 查看需求详情

| 项 | 内容 |
|----|------|
| URL | `/api/requests/{id}` |
| 方法 | `GET` |
| 是否认证 | 否；未登录只能查看已审核公开需求 |

路径参数：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | long | 是 | 需求 ID |

可能错误：

| 错误码 | 说明 |
|--------|------|
| `NOT_FOUND` | 需求不存在 |
| `FORBIDDEN` | 需求未审核通过且当前用户无权查看 |

## 4. 订单接口

### 4.1 接单

| 项 | 内容 |
|----|------|
| URL | `/api/orders/{requestId}/accept` |
| 方法 | `POST` |
| 是否认证 | 是 |

路径参数：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `requestId` | long | 是 | 被接单的需求 ID |

成功响应：

```json
{
  "code": "OK",
  "message": "success",
  "data": {
    "id": 501,
    "requestId": 101,
    "requesterId": 1,
    "providerId": 2,
    "status": "ACCEPTED",
    "acceptedAt": "2026-05-16T15:00:00",
    "request": {
      "id": 101,
      "title": "帮取快递到宿舍楼下",
      "status": "LOCKED"
    }
  }
}
```

可能错误：

| 错误码 | 说明 |
|--------|------|
| `UNAUTHORIZED` | 未登录 |
| `FORBIDDEN` | 不能接自己的需求或用户已禁用 |
| `NOT_FOUND` | 需求不存在 |
| `CONFLICT` | 需求不是 `OPEN` 或已存在有效订单 |
| `BUSINESS_ERROR` | 需求未审核通过 |

### 4.2 需求方确认接单者

| 项 | 内容 |
|----|------|
| URL | `/api/orders/{id}/confirm` |
| 方法 | `POST` |
| 是否认证 | 是 |

路径参数：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | long | 是 | 订单 ID |

成功响应中的 `status` 为 `CONFIRMED`。

可能错误：

| 错误码 | 说明 |
|--------|------|
| `FORBIDDEN` | 当前用户不是需求方 |
| `BUSINESS_ERROR` | 当前状态不是 `ACCEPTED` |

### 4.3 服务方开始服务

| 项 | 内容 |
|----|------|
| URL | `/api/orders/{id}/start` |
| 方法 | `POST` |
| 是否认证 | 是 |

成功响应中的 `status` 为 `IN_PROGRESS`。

可能错误：

| 错误码 | 说明 |
|--------|------|
| `FORBIDDEN` | 当前用户不是服务方 |
| `BUSINESS_ERROR` | 当前状态不是 `CONFIRMED` |

### 4.4 完成订单

| 项 | 内容 |
|----|------|
| URL | `/api/orders/{id}/complete` |
| 方法 | `POST` |
| 是否认证 | 是 |

成功响应中的 `status` 为 `COMPLETED`，关联需求状态变为 `DONE`。

可能错误：

| 错误码 | 说明 |
|--------|------|
| `FORBIDDEN` | 当前用户不是订单参与方 |
| `BUSINESS_ERROR` | 当前状态不是 `IN_PROGRESS` |

### 4.5 查看订单详情

| 项 | 内容 |
|----|------|
| URL | `/api/orders/{id}` |
| 方法 | `GET` |
| 是否认证 | 是 |

路径参数：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | long | 是 | 订单 ID |

成功响应：

```json
{
  "code": "OK",
  "message": "success",
  "data": {
    "id": 501,
    "requestId": 101,
    "requester": {
      "id": 1,
      "nickname": "小陈",
      "creditScore": 100
    },
    "provider": {
      "id": 2,
      "nickname": "小李",
      "creditScore": 98
    },
    "status": "IN_PROGRESS",
    "acceptedAt": "2026-05-16T15:00:00",
    "confirmedAt": "2026-05-16T15:10:00",
    "startedAt": "2026-05-16T15:20:00",
    "completedAt": null
  }
}
```

可能错误：

| 错误码 | 说明 |
|--------|------|
| `NOT_FOUND` | 订单不存在 |
| `FORBIDDEN` | 当前用户不是订单参与方或管理员 |

## 5. 评价接口

### 5.1 提交评价

| 项 | 内容 |
|----|------|
| URL | `/api/orders/{id}/reviews` |
| 方法 | `POST` |
| 是否认证 | 是 |

请求体：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `rating` | integer | 是 | 1-5 分 |
| `comment` | string | 否 | 评价内容，最多 500 字 |

成功响应：

```json
{
  "code": "OK",
  "message": "success",
  "data": {
    "id": 9001,
    "orderId": 501,
    "reviewerId": 1,
    "revieweeId": 2,
    "rating": 5,
    "comment": "服务准时，沟通顺畅。",
    "createdAt": "2026-05-16T16:00:00",
    "creditDelta": 2
  }
}
```

可能错误：

| 错误码 | 说明 |
|--------|------|
| `VALIDATION_ERROR` | 评分不在 1-5 范围内或评论过长 |
| `FORBIDDEN` | 当前用户不是订单参与方 |
| `BUSINESS_ERROR` | 订单未完成，不能评价 |
| `CONFLICT` | 当前用户已评价过该订单 |

## 6. 通知接口

### 6.1 查看通知列表

| 项 | 内容 |
|----|------|
| URL | `/api/notifications` |
| 方法 | `GET` |
| 是否认证 | 是 |

查询参数：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `readFlag` | boolean | 否 | 是否已读 |
| `page` | integer | 否 | 页码 |
| `size` | integer | 否 | 每页数量 |

### 6.2 标记通知已读

| 项 | 内容 |
|----|------|
| URL | `/api/notifications/{id}/read` |
| 方法 | `POST` |
| 是否认证 | 是 |

可能错误：

| 错误码 | 说明 |
|--------|------|
| `NOT_FOUND` | 通知不存在 |
| `FORBIDDEN` | 当前用户不是通知接收人 |

## 7. 管理接口概要

管理接口均要求管理员权限。

| 功能 | URL | 方法 | 说明 |
|------|-----|------|------|
| 审核需求 | `/api/admin/requests/{id}/audit` | `POST` | 请求体包含 `auditStatus` 和 `reason` |
| 禁用用户 | `/api/admin/users/{id}/disable` | `POST` | 禁用违规用户 |
| 启用用户 | `/api/admin/users/{id}/enable` | `POST` | 恢复用户 |
| 平台指标 | `/api/admin/metrics` | `GET` | 用户数、需求数、订单数、分类分布 |

## 8. 安全与校验要求

- 密码只接收明文用于注册/登录，服务端必须使用 BCrypt 存储哈希，不返回密码哈希。
- 所有修改类接口必须认证；管理员接口必须校验管理员角色。
- 订单流转必须在 Service 层校验状态和操作者身份，不能只依赖前端按钮控制。
- 需求标题、描述、评价内容需要做长度校验和前端 XSS 安全渲染。
- MyBatis-Plus 查询必须使用参数化条件，不拼接用户输入 SQL。
