# 数据库设计文档

## 概述

本文档描述了订单管理系统的数据库设计，包括表结构、字段定义、索引和约束。

## 数据库信息

- **数据库名称**: order_management
- **字符集**: utf8mb4
- **排序规则**: utf8mb4_unicode_ci
- **引擎**: InnoDB

## 表结构设计

### 1. 用户表 (users)

用户认证和授权信息表。

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    full_name VARCHAR(100) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) COMMENT '手机号',
    status ENUM('ACTIVE', 'INACTIVE', 'LOCKED') DEFAULT 'ACTIVE' COMMENT '状态',
    role ENUM('ADMIN', 'USER', 'MANAGER') DEFAULT 'USER' COMMENT '角色',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_at TIMESTAMP NULL COMMENT '最后登录时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

### 2. 客户表 (customers)

客户基本信息表。

```sql
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '客户ID',
    customer_code VARCHAR(20) NOT NULL UNIQUE COMMENT '客户编码',
    name VARCHAR(100) NOT NULL COMMENT '客户名称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    address TEXT COMMENT '地址',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE' COMMENT '状态',
    credit_limit DECIMAL(15,2) DEFAULT 0.00 COMMENT '信用额度',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT COMMENT '创建人ID',
    updated_by BIGINT COMMENT '更新人ID',
    INDEX idx_customer_code (customer_code),
    INDEX idx_name (name),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_created_by (created_by),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (updated_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户表';
```

### 3. 产品表 (products)

产品信息表。

```sql
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '产品ID',
    product_code VARCHAR(20) NOT NULL UNIQUE COMMENT '产品编码',
    name VARCHAR(100) NOT NULL COMMENT '产品名称',
    description TEXT COMMENT '产品描述',
    category VARCHAR(50) COMMENT '产品分类',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    cost_price DECIMAL(10,2) COMMENT '成本价',
    stock_quantity INT DEFAULT 0 COMMENT '库存数量',
    min_stock INT DEFAULT 0 COMMENT '最小库存',
    unit VARCHAR(20) DEFAULT '个' COMMENT '单位',
    status ENUM('ACTIVE', 'INACTIVE', 'DISCONTINUED') DEFAULT 'ACTIVE' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT COMMENT '创建人ID',
    updated_by BIGINT COMMENT '更新人ID',
    INDEX idx_product_code (product_code),
    INDEX idx_name (name),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_created_by (created_by),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (updated_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品表';
```

### 4. 订单表 (orders)

订单主表。

```sql
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_number VARCHAR(20) NOT NULL UNIQUE COMMENT '订单编号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    order_date DATE NOT NULL COMMENT '订单日期',
    delivery_date DATE COMMENT '交货日期',
    status ENUM('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING' COMMENT '订单状态',
    total_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
    discount_amount DECIMAL(15,2) DEFAULT 0.00 COMMENT '折扣金额',
    tax_amount DECIMAL(15,2) DEFAULT 0.00 COMMENT '税额',
    final_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '最终金额',
    currency VARCHAR(3) DEFAULT 'CNY' COMMENT '货币',
    payment_status ENUM('UNPAID', 'PARTIAL', 'PAID', 'REFUNDED') DEFAULT 'UNPAID' COMMENT '支付状态',
    payment_method VARCHAR(50) COMMENT '支付方式',
    notes TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT COMMENT '创建人ID',
    updated_by BIGINT COMMENT '更新人ID',
    INDEX idx_order_number (order_number),
    INDEX idx_customer_id (customer_id),
    INDEX idx_order_date (order_date),
    INDEX idx_status (status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_created_by (created_by),
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (updated_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';
```

### 5. 订单明细表 (order_items)

订单产品明细表。

```sql
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    discount_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '折扣率',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '折扣金额',
    subtotal DECIMAL(15,2) NOT NULL COMMENT '小计金额',
    notes TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';
```

### 6. 库存变动表 (inventory_transactions)

库存变动记录表。

```sql
CREATE TABLE inventory_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '变动ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    transaction_type ENUM('IN', 'OUT', 'ADJUSTMENT') NOT NULL COMMENT '变动类型',
    quantity INT NOT NULL COMMENT '变动数量',
    before_quantity INT NOT NULL COMMENT '变动前数量',
    after_quantity INT NOT NULL COMMENT '变动后数量',
    reference_type ENUM('ORDER', 'PURCHASE', 'ADJUSTMENT', 'RETURN') COMMENT '关联类型',
    reference_id BIGINT COMMENT '关联ID',
    notes TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    created_by BIGINT COMMENT '操作人ID',
    INDEX idx_product_id (product_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_reference_type (reference_type),
    INDEX idx_reference_id (reference_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存变动表';
```

## 索引设计

### 主键索引
- 所有表都使用自增的BIGINT类型主键

### 唯一索引
- users.username
- users.email
- customers.customer_code
- products.product_code
- orders.order_number

### 普通索引
- 外键字段
- 常用查询字段
- 状态字段
- 时间字段

## 约束设计

### 外键约束
- customers.created_by -> users.id
- customers.updated_by -> users.id
- products.created_by -> users.id
- products.updated_by -> users.id
- orders.customer_id -> customers.id
- orders.created_by -> users.id
- orders.updated_by -> users.id
- order_items.order_id -> orders.id
- order_items.product_id -> products.id
- inventory_transactions.product_id -> products.id
- inventory_transactions.created_by -> users.id

### 检查约束
- 金额字段不能为负数
- 数量字段不能为负数
- 状态字段使用预定义枚举值

## 数据字典

### 状态枚举

#### 用户状态 (users.status)
- ACTIVE: 激活
- INACTIVE: 未激活
- LOCKED: 锁定

#### 用户角色 (users.role)
- ADMIN: 管理员
- USER: 普通用户
- MANAGER: 经理

#### 客户状态 (customers.status)
- ACTIVE: 激活
- INACTIVE: 未激活

#### 产品状态 (products.status)
- ACTIVE: 激活
- INACTIVE: 未激活
- DISCONTINUED: 已停用

#### 订单状态 (orders.status)
- PENDING: 待确认
- CONFIRMED: 已确认
- PROCESSING: 处理中
- SHIPPED: 已发货
- DELIVERED: 已送达
- CANCELLED: 已取消

#### 支付状态 (orders.payment_status)
- UNPAID: 未支付
- PARTIAL: 部分支付
- PAID: 已支付
- REFUNDED: 已退款

#### 库存变动类型 (inventory_transactions.transaction_type)
- IN: 入库
- OUT: 出库
- ADJUSTMENT: 调整

## 性能优化建议

1. **索引优化**
   - 为常用查询字段创建复合索引
   - 定期分析索引使用情况

2. **分区策略**
   - 大表考虑按时间分区
   - 历史数据归档

3. **缓存策略**
   - 产品信息缓存
   - 用户权限缓存

4. **监控指标**
   - 慢查询监控
   - 连接数监控
   - 磁盘空间监控 