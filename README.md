# Order Management System

## 项目概述

基于Spring Boot的订单管理系统，提供完整的订单生命周期管理功能。

### 核心功能
- 订单创建、查询、修改、删除
- 客户信息管理
- 产品目录管理
- 订单状态跟踪
- 数据统计报表

### 技术栈
- **后端框架**: Spring Boot 2.7.x
- **数据库**: MySQL 8.0
- **构建工具**: Maven
- **API文档**: Swagger/OpenAPI
- **安全认证**: Spring Security + JWT

## 快速启动指南

### 环境要求
- JDK 11+
- Maven 3.6+
- MySQL 8.0+

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd order-management-system
```

2. **配置数据库**
```bash
# 创建数据库
CREATE DATABASE order_management;

# 修改application.yml中的数据库连接信息
```

3. **启动应用**
```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

4. **访问应用**
- 应用地址: http://localhost:8080
- API文档: http://localhost:8080/swagger-ui.html

### 开发环境设置

1. **IDE配置**
- 推荐使用IntelliJ IDEA或Eclipse
- 导入Maven项目
- 配置JDK 11

2. **数据库配置**
- 安装MySQL 8.0
- 创建数据库和用户
- 更新`application.yml`配置

## 项目结构

```
order-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/example/order/
│   │   │   ├── controller/    # 控制器层
│   │   │   ├── service/       # 业务逻辑层
│   │   │   ├── repository/    # 数据访问层
│   │   │   ├── entity/        # 实体类
│   │   │   ├── dto/           # 数据传输对象
│   │   │   ├── config/        # 配置类
│   │   │   └── util/          # 工具类
│   │   └── resources/
│   │       ├── application.yml # 配置文件
│   │       └── db/            # 数据库脚本
│   └── test/                  # 测试代码
├── docs/                      # 项目文档
├── .cursorrules              # Cursor规则配置
└── README.md                 # 项目说明
```

## 贡献指南

请参考`.cursorrules`文件中的开发规范。

## 许可证

MIT License 