# API文档

## 概述

订单管理系统提供完整的RESTful API接口，支持订单管理、客户管理、产品管理和用户认证等功能。

## 访问地址

- **开发环境**: http://localhost:8080/swagger-ui/index.html
- **生产环境**: https://api.ordermanagement.com/swagger-ui/index.html

## 认证方式

系统采用JWT（JSON Web Token）进行身份认证：

### 获取访问令牌

```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

### 使用访问令牌

在请求头中添加Authorization头：

```
Authorization: Bearer {access_token}
```

## API分组

### 1. 认证接口 (/api/v1/auth)

| 接口 | 方法 | 描述 | 权限 |
|------|------|------|------|
| `/login` | POST | 用户登录 | 公开 |
| `/register` | POST | 用户注册 | 公开 |
| `/refresh` | POST | 刷新令牌 | 公开 |
| `/validate` | GET | 验证令牌 | 公开 |

### 2. 订单管理 (/api/v1/orders)

| 接口 | 方法 | 描述 | 权限 |
|------|------|------|------|
| `/` | GET | 查询订单列表 | ROLE_USER |
| `/` | POST | 创建订单 | ROLE_USER |
| `/{id}` | GET | 查询订单详情 | ROLE_USER |
| `/{id}` | PUT | 更新订单 | ROLE_USER |
| `/{id}` | DELETE | 删除订单 | ROLE_ADMIN |
| `/{id}/status` | PATCH | 更新订单状态 | ROLE_USER |
| `/statistics` | GET | 订单统计 | ROLE_ADMIN |

### 3. 客户管理 (/api/v1/customers)

| 接口 | 方法 | 描述 | 权限 |
|------|------|------|------|
| `/` | GET | 查询客户列表 | ROLE_USER |
| `/` | POST | 创建客户 | ROLE_USER |
| `/{id}` | GET | 查询客户详情 | ROLE_USER |
| `/{id}` | PUT | 更新客户 | ROLE_USER |
| `/{id}` | DELETE | 删除客户 | ROLE_ADMIN |
| `/search` | GET | 搜索客户 | ROLE_USER |
| `/statistics` | GET | 客户统计 | ROLE_ADMIN |

### 4. 产品管理 (/api/v1/products)

| 接口 | 方法 | 描述 | 权限 |
|------|------|------|------|
| `/` | GET | 查询产品列表 | ROLE_USER |
| `/` | POST | 创建产品 | ROLE_ADMIN |
| `/{id}` | GET | 查询产品详情 | ROLE_USER |
| `/{id}` | PUT | 更新产品 | ROLE_ADMIN |
| `/{id}` | DELETE | 删除产品 | ROLE_ADMIN |
| `/search` | GET | 搜索产品 | ROLE_USER |
| `/category/{category}` | GET | 按分类查询 | ROLE_USER |
| `/statistics` | GET | 产品统计 | ROLE_ADMIN |

## 请求规范

### 请求头

```http
Content-Type: application/json
Authorization: Bearer {access_token}
Accept: application/json
```

### 分页参数

```http
GET /api/v1/orders?page=0&size=10&sort=createdAt,desc
```

- `page`: 页码（从0开始）
- `size`: 每页大小
- `sort`: 排序字段和方向

### 搜索参数

```http
GET /api/v1/customers/search?keyword=张三&status=ACTIVE
```

## 响应规范

### 成功响应

```json
{
  "message": "操作成功",
  "data": {
    // 响应数据
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### 分页响应

```json
{
  "content": [
    // 数据列表
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 100,
  "totalPages": 10,
  "last": false,
  "first": true,
  "numberOfElements": 10
}
```

### 错误响应

```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "请求参数错误",
  "path": "/api/v1/orders",
  "details": [
    {
      "field": "customerId",
      "message": "客户ID不能为空"
    }
  ]
}
```

## 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 201 | 创建成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 409 | 资源冲突 |
| 422 | 业务逻辑错误 |
| 500 | 服务器内部错误 |

## 数据模型

### 订单状态

- `PENDING`: 待处理
- `CONFIRMED`: 已确认
- `PROCESSING`: 处理中
- `SHIPPED`: 已发货
- `DELIVERED`: 已送达
- `CANCELLED`: 已取消

### 客户状态

- `ACTIVE`: 活跃
- `INACTIVE`: 非活跃
- `BLOCKED`: 已封禁

### 产品状态

- `ACTIVE`: 上架
- `INACTIVE`: 下架
- `DISCONTINUED`: 停售

## 示例代码

### JavaScript (Fetch API)

```javascript
// 登录
const login = async (username, password) => {
  const response = await fetch('/api/v1/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ username, password })
  });
  return response.json();
};

// 获取订单列表
const getOrders = async (token, page = 0, size = 10) => {
  const response = await fetch(`/api/v1/orders?page=${page}&size=${size}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.json();
};
```

### cURL

```bash
# 登录
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 获取订单列表
curl -X GET http://localhost:8080/api/v1/orders \
  -H "Authorization: Bearer {access_token}"
```

### Python (requests)

```python
import requests

# 登录
def login(username, password):
    response = requests.post(
        'http://localhost:8080/api/v1/auth/login',
        json={'username': username, 'password': password}
    )
    return response.json()

# 获取订单列表
def get_orders(token, page=0, size=10):
    headers = {'Authorization': f'Bearer {token}'}
    response = requests.get(
        f'http://localhost:8080/api/v1/orders?page={page}&size={size}',
        headers=headers
    )
    return response.json()
```

## 测试数据

### 默认用户

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | ROLE_ADMIN |
| user | 123456 | ROLE_USER |

### 示例订单

```json
{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "unitPrice": 99.99
    }
  ],
  "shippingAddress": "北京市朝阳区xxx街道",
  "notes": "请尽快发货"
}
```

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 实现基础CRUD操作
- 添加JWT认证
- 支持分页和搜索
- 添加数据验证
- 实现统一异常处理

## 联系方式

- **技术支持**: dev@ordermanagement.com
- **文档更新**: 2024-01-01
- **API版本**: v1.0.0 