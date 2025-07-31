package com.example.order.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单实体类
 * 
 * 功能: 存储订单主信息
 * 逻辑链: 订单创建 -> 信息验证 -> 数据持久化 -> 状态跟踪
 * 注意事项: 订单编号需要唯一性验证，金额需要数值验证
 * 
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_number", columnList = "order_number"),
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_order_date", columnList = "order_date"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_payment_status", columnList = "payment_status"),
    @Index(name = "idx_created_by", columnList = "created_by")
})
public class Order extends BaseEntity {

    /**
     * 订单ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单编号
     */
    @NotBlank(message = "订单编号不能为空")
    @Size(max = 20, message = "订单编号长度不能超过20个字符")
    @Column(name = "order_number", nullable = false, unique = true, length = 20)
    private String orderNumber;

    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /**
     * 客户信息（关联查询）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    /**
     * 订单日期
     */
    @NotNull(message = "订单日期不能为空")
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    /**
     * 交货日期
     */
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    /**
     * 订单状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * 订单总金额
     */
    @NotNull(message = "订单总金额不能为空")
    @DecimalMin(value = "0.01", message = "订单总金额必须大于0")
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * 折扣金额
     */
    @DecimalMin(value = "0.00", message = "折扣金额不能为负数")
    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * 税额
     */
    @DecimalMin(value = "0.00", message = "税额不能为负数")
    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    /**
     * 最终金额
     */
    @NotNull(message = "最终金额不能为空")
    @DecimalMin(value = "0.01", message = "最终金额必须大于0")
    @Column(name = "final_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal finalAmount = BigDecimal.ZERO;

    /**
     * 货币
     */
    @Size(max = 3, message = "货币代码长度不能超过3个字符")
    @Column(name = "currency", length = 3)
    private String currency = "CNY";

    /**
     * 支付状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    /**
     * 支付方式
     */
    @Size(max = 50, message = "支付方式长度不能超过50个字符")
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    /**
     * 备注
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * 订单明细列表
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * 订单状态枚举
     */
    public enum OrderStatus {
        PENDING("待确认"),
        CONFIRMED("已确认"),
        PROCESSING("处理中"),
        SHIPPED("已发货"),
        DELIVERED("已送达"),
        CANCELLED("已取消");

        private final String description;

        OrderStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 支付状态枚举
     */
    public enum PaymentStatus {
        UNPAID("未支付"),
        PARTIAL("部分支付"),
        PAID("已支付"),
        REFUNDED("已退款");

        private final String description;

        PaymentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 计算订单总金额
     */
    public void calculateTotalAmount() {
        this.totalAmount = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 计算最终金额
     */
    public void calculateFinalAmount() {
        this.finalAmount = totalAmount
                .subtract(discountAmount)
                .add(taxAmount);
    }

    /**
     * 添加订单明细
     * 
     * @param orderItem 订单明细
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    /**
     * 移除订单明细
     * 
     * @param orderItem 订单明细
     */
    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    /**
     * 检查订单是否可以取消
     * 
     * @return 是否可以取消
     */
    public boolean canCancel() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    /**
     * 检查订单是否可以发货
     * 
     * @return 是否可以发货
     */
    public boolean canShip() {
        return status == OrderStatus.CONFIRMED || status == OrderStatus.PROCESSING;
    }
} 