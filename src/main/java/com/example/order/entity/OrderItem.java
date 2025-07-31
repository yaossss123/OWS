package com.example.order.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 订单明细实体类
 * 
 * 功能: 存储订单产品明细信息
 * 逻辑链: 明细创建 -> 信息验证 -> 数据持久化 -> 金额计算
 * 注意事项: 数量和单价需要数值验证，小计金额需要自动计算
 * 
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_product_id", columnList = "product_id")
})
public class OrderItem extends BaseEntity {

    /**
     * 明细ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /**
     * 订单信息（关联查询）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;

    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * 产品信息（关联查询）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "1", message = "数量必须大于0")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /**
     * 折扣率
     */
    @DecimalMin(value = "0.00", message = "折扣率不能为负数")
    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate = BigDecimal.ZERO;

    /**
     * 折扣金额
     */
    @DecimalMin(value = "0.00", message = "折扣金额不能为负数")
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * 小计金额
     */
    @NotNull(message = "小计金额不能为空")
    @DecimalMin(value = "0.01", message = "小计金额必须大于0")
    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    /**
     * 备注
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * 计算小计金额
     */
    public void calculateSubtotal() {
        BigDecimal lineTotal = unitPrice.multiply(new BigDecimal(quantity));
        this.discountAmount = lineTotal.multiply(discountRate.divide(new BigDecimal("100")));
        this.subtotal = lineTotal.subtract(discountAmount);
    }

    /**
     * 设置折扣率并重新计算
     * 
     * @param discountRate 折扣率
     */
    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
        calculateSubtotal();
    }

    /**
     * 设置数量并重新计算
     * 
     * @param quantity 数量
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    /**
     * 设置单价并重新计算
     * 
     * @param unitPrice 单价
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }
} 