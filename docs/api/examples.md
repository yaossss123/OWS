# API示例代码

## 概述

本文档提供订单管理系统API的各种编程语言调用示例，帮助开发者快速集成和使用API。

## JavaScript/TypeScript

### 基础API客户端

```javascript
class OrderManagementAPI {
    constructor(baseURL = 'http://localhost:8080') {
        this.baseURL = baseURL;
        this.token = null;
    }

    setToken(token) {
        this.token = token;
    }

    getHeaders() {
        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        };
        
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }
        
        return headers;
    }

    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const config = {
            headers: this.getHeaders(),
            ...options
        };

        try {
            const response = await fetch(url, config);
            
            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || `HTTP ${response.status}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error('API请求失败:', error);
            throw error;
        }
    }
}
```

### 认证API

```javascript
class AuthAPI extends OrderManagementAPI {
    async login(username, password) {
        const response = await this.request('/api/v1/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
        
        this.setToken(response.accessToken);
        return response;
    }

    async refreshToken(refreshToken) {
        const response = await this.request(`/api/v1/auth/refresh?refreshToken=${refreshToken}`, {
            method: 'POST'
        });
        
        this.setToken(response.accessToken);
        return response;
    }

    async validateToken(token) {
        return await this.request(`/api/v1/auth/validate?token=${token}`, {
            method: 'GET'
        });
    }
}
```

### 订单API

```javascript
class OrderAPI extends OrderManagementAPI {
    async getOrders(page = 0, size = 10, sort = 'createdAt,desc') {
        const params = new URLSearchParams({
            page: page.toString(),
            size: size.toString(),
            sort: sort
        });
        
        return await this.request(`/api/v1/orders?${params}`);
    }

    async createOrder(orderData) {
        return await this.request('/api/v1/orders', {
            method: 'POST',
            body: JSON.stringify(orderData)
        });
    }

    async getOrder(id) {
        return await this.request(`/api/v1/orders/${id}`);
    }

    async updateOrderStatus(id, status) {
        return await this.request(`/api/v1/orders/${id}/status`, {
            method: 'PATCH',
            body: JSON.stringify({ status })
        });
    }
}
```

## Python

### 基础API客户端

```python
import requests
import json
from typing import Dict, Any, Optional

class OrderManagementAPI:
    def __init__(self, base_url: str = 'http://localhost:8080'):
        self.base_url = base_url
        self.token = None
        self.session = requests.Session()
    
    def set_token(self, token: str):
        self.token = token
        self.session.headers.update({'Authorization': f'Bearer {token}'})
    
    def request(self, endpoint: str, method: str = 'GET', data: Optional[Dict] = None) -> Dict[str, Any]:
        url = f"{self.base_url}{endpoint}"
        headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
        
        if self.token:
            headers['Authorization'] = f'Bearer {self.token}'
        
        try:
            if method.upper() == 'GET':
                response = self.session.get(url, headers=headers)
            elif method.upper() == 'POST':
                response = self.session.post(url, headers=headers, json=data)
            elif method.upper() == 'PUT':
                response = self.session.put(url, headers=headers, json=data)
            elif method.upper() == 'DELETE':
                response = self.session.delete(url, headers=headers)
            elif method.upper() == 'PATCH':
                response = self.session.patch(url, headers=headers, json=data)
            else:
                raise ValueError(f"不支持的HTTP方法: {method}")
            
            response.raise_for_status()
            return response.json()
            
        except requests.exceptions.RequestException as e:
            print(f"API请求失败: {e}")
            raise
```

### 认证API

```python
class AuthAPI(OrderManagementAPI):
    def login(self, username: str, password: str) -> Dict[str, Any]:
        data = {
            'username': username,
            'password': password
        }
        
        response = self.request('/api/v1/auth/login', 'POST', data)
        self.set_token(response['accessToken'])
        return response
    
    def refresh_token(self, refresh_token: str) -> Dict[str, Any]:
        response = self.request(f'/api/v1/auth/refresh?refreshToken={refresh_token}', 'POST')
        self.set_token(response['accessToken'])
        return response
    
    def validate_token(self, token: str) -> Dict[str, Any]:
        return self.request(f'/api/v1/auth/validate?token={token}')
```

## cURL示例

### 认证相关

```bash
# 用户登录
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'

# 刷新令牌
curl -X POST "http://localhost:8080/api/v1/auth/refresh?refreshToken=eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json"

# 验证令牌
curl -X GET "http://localhost:8080/api/v1/auth/validate?token=eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json"
```

### 订单管理

```bash
# 获取订单列表
curl -X GET "http://localhost:8080/api/v1/orders?page=0&size=10&sort=createdAt,desc" \
  -H "Authorization: Bearer {access_token}"

# 创建订单
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

# 更新订单状态
curl -X PATCH http://localhost:8080/api/v1/orders/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {access_token}" \
  -d '{
    "status": "CONFIRMED"
  }'
```

### 客户管理

```bash
# 获取客户列表
curl -X GET "http://localhost:8080/api/v1/customers?page=0&size=10" \
  -H "Authorization: Bearer {access_token}"

# 创建客户
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {access_token}" \
  -d '{
    "name": "张三",
    "email": "zhangsan@example.com",
    "phone": "13800138001",
    "address": "北京市朝阳区",
    "status": "ACTIVE"
  }'

# 搜索客户
curl -X GET "http://localhost:8080/api/v1/customers/search?keyword=张三&status=ACTIVE" \
  -H "Authorization: Bearer {access_token}"
```

### 产品管理

```bash
# 获取产品列表
curl -X GET "http://localhost:8080/api/v1/products?page=0&size=10" \
  -H "Authorization: Bearer {access_token}"

# 创建产品
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {access_token}" \
  -d '{
    "name": "iPhone 15",
    "description": "苹果最新手机",
    "price": 5999.00,
    "stock": 100,
    "category": "ELECTRONICS",
    "status": "ACTIVE"
  }'

# 按分类搜索产品
curl -X GET "http://localhost:8080/api/v1/products/category/ELECTRONICS?page=0&size=10" \
  -H "Authorization: Bearer {access_token}"
```

## 使用示例

### JavaScript完整示例

```javascript
async function main() {
    try {
        // 创建API客户端
        const authAPI = new AuthAPI();
        const orderAPI = new OrderAPI();
        
        // 1. 用户登录
        console.log('正在登录...');
        const loginResponse = await authAPI.login('admin', '123456');
        console.log('登录成功:', loginResponse);
        
        // 设置令牌
        orderAPI.setToken(loginResponse.accessToken);
        
        // 2. 创建订单
        console.log('正在创建订单...');
        const orderData = {
            customerId: 1,
            items: [
                {
                    productId: 1,
                    quantity: 2,
                    unitPrice: 5999.00
                }
            ],
            shippingAddress: '北京市朝阳区xxx街道',
            notes: '请尽快发货'
        };
        const order = await orderAPI.createOrder(orderData);
        console.log('订单创建成功:', order);
        
        // 3. 获取订单列表
        console.log('正在获取订单列表...');
        const orders = await orderAPI.getOrders(0, 10);
        console.log('订单列表:', orders);
        
        // 4. 更新订单状态
        console.log('正在更新订单状态...');
        const statusUpdate = await orderAPI.updateOrderStatus(order.id, 'CONFIRMED');
        console.log('订单状态更新成功:', statusUpdate);
        
    } catch (error) {
        console.error('操作失败:', error.message);
    }
}

// 运行示例
main();
```

## 错误处理

### JavaScript错误处理

```javascript
class APIError extends Error {
    constructor(message, status, details) {
        super(message);
        this.name = 'APIError';
        this.status = status;
        this.details = details;
    }
}

// 在request方法中添加错误处理
async request(endpoint, options = {}) {
    try {
        const response = await fetch(url, config);
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new APIError(
                errorData.message || `HTTP ${response.status}`,
                response.status,
                errorData.details
            );
        }
        
        return await response.json();
    } catch (error) {
        if (error instanceof APIError) {
            throw error;
        }
        throw new APIError('网络请求失败', 0, error.message);
    }
}
```

## 最佳实践

### 1. 令牌管理

```javascript
class TokenManager {
    constructor(api) {
        this.api = api;
        this.refreshTimer = null;
    }
    
    setToken(token, expiresIn) {
        this.api.setToken(token);
        
        // 设置自动刷新
        if (this.refreshTimer) {
            clearTimeout(this.refreshTimer);
        }
        
        // 在过期前5分钟刷新
        const refreshTime = (expiresIn - 300) * 1000;
        this.refreshTimer = setTimeout(() => {
            this.refreshToken();
        }, refreshTime);
    }
}
```

### 2. 请求重试

```javascript
async function requestWithRetry(fn, maxRetries = 3) {
    for (let i = 0; i < maxRetries; i++) {
        try {
            return await fn();
        } catch (error) {
            if (i === maxRetries - 1) throw error;
            
            // 等待后重试
            await new Promise(resolve => setTimeout(resolve, 1000 * (i + 1)));
        }
    }
}
```

## 更新日志

### v1.0.0 (2024-01-01)
- 添加JavaScript/TypeScript示例
- 添加Python示例
- 添加cURL示例
- 实现错误处理机制
- 添加最佳实践指南 