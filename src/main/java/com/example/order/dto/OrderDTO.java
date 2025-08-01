package com.example.order.dto;

import com.example.order.entity.Order;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单数据传输对象
 *
 * 功能: 用于订单数据的传输和展示
 * 逻辑链: 数据接收 -> 验证转换 -> 业务处理 -> 结果返回
 * 注意事项: 需要验证订单金额的合理性和状态转换的合法性
 *
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Data
public class OrderDTO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单编号
     */
    @NotNull(message = "订单编号不能为空")
    @Size(max = 50, message = "订单编号长度不能超过50个字符")
    private String orderNumber;

    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    /**
     * 订单日期
     */
    @NotNull(message = "订单日期不能为空")
    private LocalDate orderDate;

    /**
     * 交货日期
     */
    private LocalDate deliveryDate;

    /**
     * 订单状态
     */
    private Order.OrderStatus status;

    /**
     * 订单总金额
     */
    @DecimalMin(value = "0.0", message = "订单总金额不能为负数")
    private BigDecimal totalAmount;

    /**
     * 折扣金额
     */
    @DecimalMin(value = "0.0", message = "折扣金额不能为负数")
    private BigDecimal discountAmount;

    /**
     * 税费金额
     */
    @DecimalMin(value = "0.0", message = "税费金额不能为负数")
    private BigDecimal taxAmount;

    /**
     * 最终金额
     */
    @DecimalMin(value = "0.0", message = "最终金额不能为负数")
    private BigDecimal finalAmount;

    /**
     * 货币
     */
    @Size(max = 10, message = "货币长度不能超过10个字符")
    private String currency;

    /**
     * 支付状态
     */
    private Order.PaymentStatus paymentStatus;

    /**
     * 支付方式
     */
    @Size(max = 50, message = "支付方式长度不能超过50个字符")
    private String paymentMethod;

    /**
     * 备注
     */
    @Size(max = 1000, message = "备注长度不能超过1000个字符")
    private String notes;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从实体转换为DTO
     *
     * @param order 订单实体
     * @return 订单DTO
     */
    public static OrderDTO fromEntity(Order order) {
        if (order == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerId(order.getCustomerId());
        dto.setOrderDate(order.getOrderDate());
        dto.setDeliveryDate(order.getDeliveryDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setTaxAmount(order.getTaxAmount());
        dto.setFinalAmount(order.getFinalAmount());
        dto.setCurrency(order.getCurrency());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setNotes(order.getNotes());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        return dto;
    }

    /**
     * 转换为实体对象
     *
     * @return 订单实体
     */
    public Order toEntity() {
        Order order = new Order();
        order.setId(this.id);
        order.setOrderNumber(this.orderNumber);
        order.setCustomerId(this.customerId);
        order.setOrderDate(this.orderDate);
        order.setDeliveryDate(this.deliveryDate);
        order.setStatus(this.status);
        order.setTotalAmount(this.totalAmount);
        order.setDiscountAmount(this.discountAmount);
        order.setTaxAmount(this.taxAmount);
        order.setFinalAmount(this.finalAmount);
        order.setCurrency(this.currency);
        order.setPaymentStatus(this.paymentStatus);
        order.setPaymentMethod(this.paymentMethod);
        order.setNotes(this.notes);

        return order;
    }
} 