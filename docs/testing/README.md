# 测试文档

## 概述

本文档描述了订单管理系统的测试策略、测试类型和运行方法。我们采用分层测试策略，确保系统的质量和稳定性。

## 测试策略

### 测试金字塔
```
    E2E Tests (少量)
       /\
      /  \
   Integration Tests (中等)
      /\
     /  \
  Unit Tests (大量)
```

### 测试原则
- **单元测试**: 测试单个方法或类的功能
- **集成测试**: 测试组件间的交互
- **端到端测试**: 测试完整的业务流程
- **测试覆盖率**: 目标 > 80%

## 测试类型

### 1. 单元测试 (Unit Tests)

#### 测试范围
- Service层业务逻辑
- 工具类方法
- 数据验证逻辑

#### 测试文件
- `OrderServiceTest.java` - 订单服务测试
- `CustomerServiceTest.java` - 客户服务测试
- `ProductServiceTest.java` - 产品服务测试
- `JwtUtilTest.java` - JWT工具类测试

#### 测试特点
- 使用Mockito进行依赖模拟
- 快速执行（毫秒级）
- 高覆盖率
- 独立性强

### 2. 集成测试 (Integration Tests)

#### 测试范围
- Controller层API接口
- 安全认证流程
- 数据持久化

#### 测试文件
- `OrderControllerTest.java` - 订单控制器测试
- `AuthControllerTest.java` - 认证控制器测试

#### 测试特点
- 使用@WebMvcTest注解
- 模拟HTTP请求
- 验证响应状态和内容
- 测试安全配置

### 3. 端到端测试 (E2E Tests)

#### 测试范围
- 完整的业务流程
- 用户认证和授权
- 数据流转

#### 测试文件
- `OrderManagementIntegrationTest.java` - 完整业务流程测试

#### 测试特点
- 使用@SpringBootTest注解
- 真实数据库交互
- 完整的HTTP请求链
- 验证业务场景

## 测试环境

### 测试配置
- **数据库**: H2内存数据库
- **配置文件**: `application-test.yml`
- **测试数据**: 自动初始化

### 测试数据
```yaml
test:
  data:
    users:
      - username: admin
        password: 123456
        role: ROLE_ADMIN
      - username: user
        password: 123456
        role: ROLE_USER
    customers:
      - name: 测试客户1
        email: customer1@test.com
    products:
      - name: 测试产品1
        productCode: TEST001
```

## 运行测试

### 1. 运行所有测试
```bash
mvn test
```

### 2. 运行单元测试
```bash
mvn test -Dtest=*Test
```

### 3. 运行集成测试
```bash
mvn test -Dtest=*IntegrationTest
```

### 4. 运行特定测试类
```bash
mvn test -Dtest=OrderServiceTest
```

### 5. 生成测试报告
```bash
mvn surefire-report:report
```

### 6. 使用Windows脚本
```bash
run-tests.bat
```

## 测试覆盖率

### 覆盖率目标
- **单元测试**: > 90%
- **集成测试**: > 80%
- **端到端测试**: > 70%

### 生成覆盖率报告
```bash
mvn jacoco:report
```

## 测试最佳实践

### 1. 测试命名
```java
@Test
void testCreateOrder_Success() { }
@Test
void testCreateOrder_CustomerNotFound() { }
@Test
void testCreateOrder_InvalidRequest() { }
```

### 2. 测试结构 (AAA模式)
```java
@Test
void testMethod() {
    // Arrange (准备)
    when(repository.findById(1L)).thenReturn(Optional.of(entity));
    
    // Act (执行)
    Result result = service.method(1L);
    
    // Assert (断言)
    assertNotNull(result);
    assertEquals(expected, result.getValue());
}
```

### 3. Mock使用
```java
@Mock
private OrderRepository orderRepository;

@InjectMocks
private OrderService orderService;

@Test
void testMethod() {
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    verify(orderRepository).findById(1L);
}
```

## 测试数据管理

### 1. 测试数据初始化
```java
@BeforeEach
void setUp() {
    // 初始化测试数据
    testOrder = new Order();
    testOrder.setId(1L);
    testOrder.setOrderNumber("ORD001");
}
```

### 2. 测试数据清理
```java
@AfterEach
void tearDown() {
    // 清理测试数据
    repository.deleteAll();
}
```

## 持续集成

### GitHub Actions配置
```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
      - name: Run tests
        run: mvn test
      - name: Generate test report
        run: mvn surefire-report:report
```

## 故障排除

### 常见问题

#### 1. 测试失败 - 数据库连接
**问题**: H2数据库连接失败
**解决**: 检查`application-test.yml`配置

#### 2. 测试失败 - 安全配置
**问题**: 认证测试失败
**解决**: 检查测试用户配置和JWT设置

#### 3. 测试失败 - 依赖注入
**问题**: Bean注入失败
**解决**: 检查@MockBean和@InjectMocks注解

### 调试技巧

#### 1. 启用详细日志
```yaml
logging:
  level:
    com.example.order: DEBUG
    org.springframework.test: DEBUG
```

#### 2. 使用@DirtiesContext
```java
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
```

#### 3. 使用@TestPropertySource
```java
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
```

## 测试报告

### 报告位置
- **Surefire报告**: `target/surefire-reports/`
- **HTML报告**: `target/site/surefire-report.html`
- **覆盖率报告**: `target/site/jacoco/`

### 报告内容
- 测试执行结果
- 失败测试详情
- 测试覆盖率统计
- 执行时间统计

## 更新日志

### v1.0.0 (2024-01-01)
- 完成单元测试框架搭建
- 实现Service层测试
- 实现Controller层测试
- 实现端到端测试
- 配置测试环境和数据
- 创建测试文档

## 联系方式

- **测试负责人**: 开发团队
- **问题反馈**: 通过GitHub Issues
- **文档更新**: 2024-01-01 