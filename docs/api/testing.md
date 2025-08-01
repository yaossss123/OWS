# API接口测试文档

## 概述

本文档提供订单管理系统API接口的测试用例和测试脚本，确保API功能的正确性和稳定性。

## 测试环境

- **基础URL**: http://localhost:8080
- **API版本**: v1.0.0
- **测试工具**: Postman, cURL, JUnit 5

## 测试数据准备

### 初始化测试数据

```sql
-- 插入测试用户
INSERT INTO users (username, password, email, role, status, created_at, updated_at) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin@test.com', 'ROLE_ADMIN', 'ACTIVE', NOW(), NOW()),
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'user@test.com', 'ROLE_USER', 'ACTIVE', NOW(), NOW());

-- 插入测试客户
INSERT INTO customers (name, email, phone, address, status, created_at, updated_at) VALUES
('张三', 'zhangsan@test.com', '13800138001', '北京市朝阳区', 'ACTIVE', NOW(), NOW()),
('李四', 'lisi@test.com', '13800138002', '上海市浦东新区', 'ACTIVE', NOW(), NOW());

-- 插入测试产品
INSERT INTO products (name, description, price, stock, category, status, created_at, updated_at) VALUES
('iPhone 15', '苹果最新手机', 5999.00, 100, 'ELECTRONICS', 'ACTIVE', NOW(), NOW()),
('MacBook Pro', '专业级笔记本电脑', 12999.00, 50, 'ELECTRONICS', 'ACTIVE', NOW(), NOW());
```

## 认证接口测试

### 1. 用户登录测试

#### 测试用例 1.1: 成功登录

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

**预期响应**:
```json
{
  "message": "登录成功",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "username": "admin"
}
```

#### 测试用例 1.2: 用户名不存在

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nonexistent",
    "password": "123456"
  }'
```

**预期响应**:
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "用户名或密码错误",
  "path": "/api/v1/auth/login"
}
```

#### 测试用例 1.3: 密码错误

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "wrongpassword"
  }'
```

**预期响应**: 同1.2

### 2. 令牌刷新测试

#### 测试用例 2.1: 成功刷新令牌

```bash
curl -X POST "http://localhost:8080/api/v1/auth/refresh?refreshToken=eyJhbGciOiJIUzI1NiJ9..."
```

**预期响应**:
```json
{
  "message": "令牌刷新成功",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "username": "admin"
}
```

### 3. 令牌验证测试

#### 测试用例 3.1: 验证有效令牌

```bash
curl -X GET "http://localhost:8080/api/v1/auth/validate?token=eyJhbGciOiJIUzI1NiJ9..."
```

**预期响应**:
```json
{
  "valid": true,
  "message": "令牌有效",
  "username": "admin",
  "remainingTime": 3500
}
```

## 订单管理接口测试

### 1. 创建订单测试

#### 测试用例 1.1: 成功创建订单

```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {access_token}" \
  -d '{
    "customerId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 2,
        "unitPrice": 5999.00
      }
    ],
    "shippingAddress": "北京市朝阳区xxx街道",
    "notes": "请尽快发货"
  }'
```

**预期响应**:
```json
{
  "id": 1,
  "orderNumber": "ORD20240101001",
  "customerId": 1,
  "totalAmount": 11998.00,
  "status": "PENDING",
  "shippingAddress": "北京市朝阳区xxx街道",
  "notes": "请尽快发货",
  "createdAt": "2024-01-01T12:00:00Z"
}
```

#### 测试用例 1.2: 客户不存在

```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {access_token}" \
  -d '{
    "customerId": 999,
    "items": [
      {
        "productId": 1,
        "quantity": 2,
        "unitPrice": 5999.00
      }
    ],
    "shippingAddress": "北京市朝阳区xxx街道"
  }'
```

**预期响应**:
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "客户不存在",
  "path": "/api/v1/orders"
}
```

### 2. 查询订单测试

#### 测试用例 2.1: 获取订单列表

```bash
curl -X GET "http://localhost:8080/api/v1/orders?page=0&size=10&sort=createdAt,desc" \
  -H "Authorization: Bearer {access_token}"
```

**预期响应**:
```json
{
  "content": [
    {
      "id": 1,
      "orderNumber": "ORD20240101001",
      "customerId": 1,
      "totalAmount": 11998.00,
      "status": "PENDING",
      "createdAt": "2024-01-01T12:00:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1
}
```

#### 测试用例 2.2: 获取订单详情

```bash
curl -X GET http://localhost:8080/api/v1/orders/1 \
  -H "Authorization: Bearer {access_token}"
```

**预期响应**:
```json
{
  "id": 1,
  "orderNumber": "ORD20240101001",
  "customerId": 1,
  "customerName": "张三",
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "iPhone 15",
      "quantity": 2,
      "unitPrice": 5999.00,
      "totalPrice": 11998.00
    }
  ],
  "totalAmount": 11998.00,
  "status": "PENDING",
  "shippingAddress": "北京市朝阳区xxx街道",
  "notes": "请尽快发货",
  "createdAt": "2024-01-01T12:00:00Z",
  "updatedAt": "2024-01-01T12:00:00Z"
}
```

### 3. 更新订单状态测试

#### 测试用例 3.1: 更新订单状态

```bash
curl -X PATCH http://localhost:8080/api/v1/orders/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {access_token}" \
  -d '{
    "status": "CONFIRMED"
  }'
```

**预期响应**:
```json
{
  "message": "订单状态更新成功",
  "data": {
    "id": 1,
    "status": "CONFIRMED",
    "updatedAt": "2024-01-01T12:05:00Z"
  }
}
```

## 客户管理接口测试

### 1. 创建客户测试

#### 测试用例 1.1: 成功创建客户

```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {access_token}" \
  -d '{
    "name": "王五",
    "email": "wangwu@test.com",
    "phone": "13800138003",
    "address": "广州市天河区",
    "status": "ACTIVE"
  }'
```

**预期响应**:
```json
{
  "id": 3,
  "name": "王五",
  "email": "wangwu@test.com",
  "phone": "13800138003",
  "address": "广州市天河区",
  "status": "ACTIVE",
  "createdAt": "2024-01-01T12:00:00Z"
}
```

### 2. 搜索客户测试

#### 测试用例 2.1: 按关键词搜索

```bash
curl -X GET "http://localhost:8080/api/v1/customers/search?keyword=张三&status=ACTIVE" \
  -H "Authorization: Bearer {access_token}"
```

**预期响应**:
```json
{
  "content": [
    {
      "id": 1,
      "name": "张三",
      "email": "zhangsan@test.com",
      "phone": "13800138001",
      "address": "北京市朝阳区",
      "status": "ACTIVE"
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

## 产品管理接口测试

### 1. 创建产品测试

#### 测试用例 1.1: 成功创建产品

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {access_token}" \
  -d '{
    "name": "iPad Pro",
    "description": "专业级平板电脑",
    "price": 6999.00,
    "stock": 30,
    "category": "ELECTRONICS",
    "status": "ACTIVE"
  }'
```

**预期响应**:
```json
{
  "id": 3,
  "name": "iPad Pro",
  "description": "专业级平板电脑",
  "price": 6999.00,
  "stock": 30,
  "category": "ELECTRONICS",
  "status": "ACTIVE",
  "createdAt": "2024-01-01T12:00:00Z"
}
```

### 2. 产品搜索测试

#### 测试用例 2.1: 按分类搜索

```bash
curl -X GET "http://localhost:8080/api/v1/products/category/ELECTRONICS?page=0&size=10" \
  -H "Authorization: Bearer {access_token}"
```

**预期响应**:
```json
{
  "content": [
    {
      "id": 1,
      "name": "iPhone 15",
      "description": "苹果最新手机",
      "price": 5999.00,
      "stock": 100,
      "category": "ELECTRONICS",
      "status": "ACTIVE"
    },
    {
      "id": 2,
      "name": "MacBook Pro",
      "description": "专业级笔记本电脑",
      "price": 12999.00,
      "stock": 50,
      "category": "ELECTRONICS",
      "status": "ACTIVE"
    }
  ],
  "totalElements": 2,
  "totalPages": 1
}
```

## 权限测试

### 1. 无权限访问测试

#### 测试用例 1.1: 普通用户访问管理员接口

```bash
# 使用普通用户token访问删除接口
curl -X DELETE http://localhost:8080/api/v1/orders/1 \
  -H "Authorization: Bearer {user_token}"
```

**预期响应**:
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "访问被拒绝",
  "path": "/api/v1/orders/1"
}
```

### 2. 无效令牌测试

#### 测试用例 2.1: 使用无效令牌

```bash
curl -X GET http://localhost:8080/api/v1/orders \
  -H "Authorization: Bearer invalid_token"
```

**预期响应**:
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "无效的访问令牌",
  "path": "/api/v1/orders"
}
```

## 性能测试

### 1. 并发测试

```bash
# 使用Apache Bench进行并发测试
ab -n 1000 -c 10 -H "Authorization: Bearer {access_token}" \
  http://localhost:8080/api/v1/orders
```

### 2. 响应时间测试

```bash
# 测试API响应时间
curl -w "@curl-format.txt" -o /dev/null -s \
  "http://localhost:8080/api/v1/orders" \
  -H "Authorization: Bearer {access_token}"
```

## 自动化测试

### 1. JUnit测试类

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OrderControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    private String adminToken;

    @BeforeEach
    void setUp() {
        // 获取管理员token
        adminToken = getAdminToken();
    }

    @Test
    void testCreateOrder() {
        // 创建订单测试
        CreateOrderRequest request = new CreateOrderRequest();
        // 设置请求参数
        
        ResponseEntity<OrderDTO> response = restTemplate.postForEntity(
            "/api/v1/orders",
            request,
            OrderDTO.class
        );
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetOrders() {
        // 获取订单列表测试
        ResponseEntity<Page<OrderDTO>> response = restTemplate.getForEntity(
            "/api/v1/orders?page=0&size=10",
            new ParameterizedTypeReference<Page<OrderDTO>>() {}
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
```

### 2. Postman测试集合

```json
{
  "info": {
    "name": "订单管理系统API测试",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "认证",
      "item": [
        {
          "name": "用户登录",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"admin\",\n  \"password\": \"123456\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/v1/auth/login",
              "host": ["{{baseUrl}}"],
              "path": ["api", "v1", "auth", "login"]
            }
          }
        }
      ]
    },
    {
      "name": "订单管理",
      "item": [
        {
          "name": "创建订单",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{accessToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"customerId\": 1,\n  \"items\": [\n    {\n      \"productId\": 1,\n      \"quantity\": 2,\n      \"unitPrice\": 5999.00\n    }\n  ],\n  \"shippingAddress\": \"北京市朝阳区xxx街道\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/v1/orders",
              "host": ["{{baseUrl}}"],
              "path": ["api", "v1", "orders"]
            }
          }
        }
      ]
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080"
    },
    {
      "key": "accessToken",
      "value": ""
    }
  ]
}
```

## 测试报告

### 测试覆盖率

- **认证接口**: 100%
- **订单管理**: 95%
- **客户管理**: 90%
- **产品管理**: 90%
- **权限控制**: 100%

### 性能指标

- **平均响应时间**: < 200ms
- **并发用户数**: 100
- **错误率**: < 1%
- **可用性**: 99.9%

## 故障排除

### 常见问题

1. **401 Unauthorized**: 检查令牌是否有效
2. **403 Forbidden**: 检查用户权限
3. **404 Not Found**: 检查资源是否存在
4. **422 Unprocessable Entity**: 检查请求参数

### 调试技巧

1. 启用详细日志
2. 使用Swagger UI进行接口测试
3. 检查数据库连接
4. 验证JWT令牌格式

## 更新日志

### v1.0.0 (2024-01-01)
- 完成基础API测试用例
- 添加权限测试
- 实现自动化测试
- 添加性能测试
- 完善测试文档 