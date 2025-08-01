# 安全认证文档

## 概述

本系统采用JWT（JSON Web Token）进行身份认证和授权，结合Spring Security提供完整的安全解决方案。

## 技术栈

- **Spring Security**: 安全框架
- **JWT**: 无状态身份认证
- **BCrypt**: 密码加密
- **CORS**: 跨域资源共享

## 架构设计

### 认证流程

1. **用户登录**: 用户提供用户名和密码
2. **身份验证**: Spring Security验证用户凭据
3. **令牌生成**: 生成访问令牌和刷新令牌
4. **令牌验证**: 后续请求通过JWT过滤器验证令牌
5. **权限控制**: 基于角色进行访问控制

### 组件说明

#### JWT工具类 (JwtUtil)

```java
@Component
public class JwtUtil {
    // 生成访问令牌
    public String generateAccessToken(UserDetails userDetails)
    
    // 生成刷新令牌
    public String generateRefreshToken(UserDetails userDetails)
    
    // 验证令牌
    public Boolean validateToken(String token, UserDetails userDetails)
    
    // 提取用户名
    public String extractUsername(String token)
}
```

#### JWT认证过滤器 (JwtAuthenticationFilter)

- 拦截所有HTTP请求
- 从Authorization头部提取JWT令牌
- 验证令牌有效性
- 设置Security上下文

#### Spring Security配置 (SecurityConfig)

- 配置认证规则
- 设置CORS策略
- 配置密码编码器
- 定义访问控制

## API接口

### 认证接口

#### 用户登录
```http
POST /api/v1/auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "password"
}
```

响应示例：
```json
{
    "message": "登录成功",
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "username": "admin"
}
```

#### 刷新令牌
```http
POST /api/v1/auth/refresh?refreshToken=eyJhbGciOiJIUzUxMiJ9...
```

#### 验证令牌
```http
GET /api/v1/auth/validate?token=eyJhbGciOiJIUzUxMiJ9...
```

#### 用户注册
```http
POST /api/v1/auth/register
Content-Type: application/json

{
    "username": "newuser",
    "email": "newuser@example.com",
    "fullName": "New User"
}
```

### 受保护的接口

所有业务接口都需要在请求头中包含有效的JWT令牌：

```http
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## 权限控制

### 角色定义

- **ROLE_USER**: 普通用户，可以访问基本功能
- **ROLE_ADMIN**: 管理员，可以访问所有功能

### 访问控制

```java
// 公开接口
.antMatchers("/api/v1/auth/**").permitAll()
.antMatchers("/actuator/**").permitAll()
.antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

// 需要认证的接口
.antMatchers("/api/v1/orders/**").hasAnyRole("USER", "ADMIN")
.antMatchers("/api/v1/customers/**").hasAnyRole("USER", "ADMIN")
.antMatchers("/api/v1/products/**").hasAnyRole("USER", "ADMIN")

// 管理员接口
.antMatchers("/api/v1/admin/**").hasRole("ADMIN")
```

## 配置说明

### JWT配置

```yaml
jwt:
  secret: ${JWT_SECRET:orderManagementSecretKey2024}
  expiration: 86400000  # 24小时
  refresh-expiration: 604800000  # 7天
```

### 安全配置

```yaml
spring:
  security:
    user:
      name: admin
      password: admin123
```

## 安全最佳实践

### 1. 令牌管理

- 访问令牌有效期：24小时
- 刷新令牌有效期：7天
- 令牌存储在客户端，服务端无状态

### 2. 密码安全

- 使用BCrypt加密算法
- 密码强度要求：至少8位，包含字母和数字
- 定期更换密码

### 3. 跨域安全

- 配置CORS策略
- 限制允许的源和方法
- 设置适当的缓存时间

### 4. 错误处理

- 不暴露敏感信息
- 统一的错误响应格式
- 详细的日志记录

## 测试

### 单元测试

```java
@SpringBootTest
class JwtUtilTest {
    @Test
    void testGenerateAccessToken() {
        // 测试令牌生成
    }
    
    @Test
    void testValidateToken() {
        // 测试令牌验证
    }
}
```

### 集成测试

```java
@SpringBootTest
@AutoConfigureTestDatabase
class AuthControllerTest {
    @Test
    void testLogin() {
        // 测试登录接口
    }
}
```

## 故障排除

### 常见问题

1. **令牌过期**
   - 使用刷新令牌获取新的访问令牌
   - 重新登录

2. **权限不足**
   - 检查用户角色
   - 确认接口权限配置

3. **CORS错误**
   - 检查CORS配置
   - 确认请求源是否在允许列表中

### 调试技巧

1. 启用调试日志
```yaml
logging:
  level:
    org.springframework.security: DEBUG
    com.example.order.security: DEBUG
```

2. 检查令牌内容
```java
// 使用JWT调试工具解析令牌
String token = "your-jwt-token";
Claims claims = Jwts.parserBuilder()
    .setSigningKey(getSigningKey())
    .build()
    .parseClaimsJws(token)
    .getBody();
```

## 更新日志

### v0.1.0
- 实现JWT认证
- 配置Spring Security
- 添加权限控制
- 实现用户认证接口

## 参考资料

- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/)
- [BCrypt](https://en.wikipedia.org/wiki/Bcrypt) 