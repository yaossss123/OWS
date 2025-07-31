# 更新日志

本项目遵循 [Semantic Versioning](https://semver.org/lang/zh-CN/) 语义化版本规范。

## [未发布]

### 新增
- 项目基础架构搭建
- 文档结构创建
- Git版本控制初始化
- 开发规范制定

### 变更
- 无

### 修复
- 无

## [0.1.0] - 2024-01-01

### 新增
- 项目初始化
  - 创建README.md项目说明文档
  - 创建.cursorrules开发规范文档
  - 创建.cursorignore忽略文件配置
  - 创建.gitignore版本控制忽略规则
- 文档结构建立
  - 创建docs/README.md文档说明
  - 创建docs/decisions.md技术决策记录
  - 创建docs/todos.md待办事项列表
- Git版本控制
  - 初始化Git仓库
  - 提交初始项目结构

### 技术决策
- ADR-001: 选择Spring Boot作为主要框架
- ADR-002: 使用MySQL作为主数据库
- ADR-003: 采用分层架构设计
- ADR-004: 使用JWT进行身份认证
- ADR-005: 选择Maven作为构建工具

### 开发规范
- 代码规范: 遵循Google Java Style Guide
- 命名规范: 类名PascalCase，方法名camelCase
- 注释规范: 使用Javadoc格式
- Git提交规范: 使用Angular Commit Message格式

## 版本说明

### 版本号格式
- 主版本号.次版本号.修订号 (例如: 1.0.0)
- 主版本号: 不兼容的API修改
- 次版本号: 向下兼容的功能性新增
- 修订号: 向下兼容的问题修正

### 版本类型
- **Alpha**: 内部测试版本
- **Beta**: 公开测试版本
- **RC**: 候选发布版本
- **Release**: 正式发布版本

### 更新日志格式
- **新增**: 新功能
- **变更**: 功能变更
- **修复**: 问题修复
- **移除**: 功能移除
- **废弃**: 功能废弃

## 贡献指南

### 提交信息格式
```
<type>(<scope>): <subject>

<body>

<footer>
```

### 类型说明
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

### 示例
```
feat(order): 添加订单创建功能

- 实现订单创建API接口
- 添加订单验证逻辑
- 集成库存检查功能

Closes #123
``` 