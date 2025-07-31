package com.example.order.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 产品实体类
 * 
 * 功能: 存储产品信息
 * 逻辑链: 产品创建 -> 信息验证 -> 数据持久化 -> 库存管理
 * 注意事项: 产品编码需要唯一性验证，价格和库存需要数值验证
 * 
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_code", columnList = "product_code"),
    @Index(name = "idx_name", columnList = "name"),
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_by", columnList = "created_by")
})
public class Product extends BaseEntity {

    /**
     * 产品ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 产品编码
     */
    @NotBlank(message = "产品编码不能为空")
    @Size(max = 20, message = "产品编码长度不能超过20个字符")
    @Column(name = "product_code", nullable = false, unique = true, length = 20)
    private String productCode;

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空")
    @Size(max = 100, message = "产品名称长度不能超过100个字符")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 产品描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 产品分类
     */
    @Size(max = 50, message = "产品分类长度不能超过50个字符")
    @Column(name = "category", length = 50)
    private String category;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /**
     * 成本价
     */
    @DecimalMin(value = "0.00", message = "成本价不能为负数")
    @Column(name = "cost_price", precision = 10, scale = 2)
    private BigDecimal costPrice;

    /**
     * 库存数量
     */
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    /**
     * 最小库存
     */
    @Column(name = "min_stock", nullable = false)
    private Integer minStock = 0;

    /**
     * 单位
     */
    @Size(max = 20, message = "单位长度不能超过20个字符")
    @Column(name = "unit", length = 20)
    private String unit = "个";

    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProductStatus status = ProductStatus.ACTIVE;

    /**
     * 产品状态枚举
     */
    public enum ProductStatus {
        ACTIVE("激活"),
        INACTIVE("未激活"),
        DISCONTINUED("已停用");

        private final String description;

        ProductStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 检查库存是否充足
     * 
     * @param requiredQuantity 需要的数量
     * @return 库存是否充足
     */
    public boolean hasSufficientStock(int requiredQuantity) {
        return stockQuantity >= requiredQuantity;
    }

    /**
     * 检查是否需要补货
     * 
     * @return 是否需要补货
     */
    public boolean needsRestock() {
        return stockQuantity <= minStock;
    }

    /**
     * 减少库存
     * 
     * @param quantity 减少的数量
     * @throws IllegalArgumentException 库存不足时抛出异常
     */
    public void decreaseStock(int quantity) {
        if (stockQuantity < quantity) {
            throw new IllegalArgumentException("库存不足");
        }
        stockQuantity -= quantity;
    }

    /**
     * 增加库存
     * 
     * @param quantity 增加的数量
     */
    public void increaseStock(int quantity) {
        if (quantity > 0) {
            stockQuantity += quantity;
        }
    }
} 