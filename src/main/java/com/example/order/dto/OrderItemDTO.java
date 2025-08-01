package com.example.order.dto;

import com.example.order.entity.OrderItem;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项数据传输对象
 *
 * 功能: 用于订单项数据的传输和展示
 * 逻辑链: 数据接收 -> 验证转换 -> 业务处理 -> 结果返回
 * 注意事项: 需要验证数量和价格的合理性，以及折扣率的范围
 *
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Data
public class OrderItemDTO {

    /**
     * 订单项ID
     */
    private Long id;

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空")
    private Long productId;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "1", message = "数量必须大于0")
    private Integer quantity;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    private BigDecimal unitPrice;

    /**
     * 折扣率
     */
    @DecimalMin(value = "0.0", message = "折扣率不能为负数")
    private BigDecimal discountRate;

    /**
     * 折扣金额
     */
    @DecimalMin(value = "0.0", message = "折扣金额不能为负数")
    private BigDecimal discountAmount;

    /**
     * 小计金额
     */
    @DecimalMin(value = "0.0", message = "小计金额不能为负数")
    private BigDecimal subtotal;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
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
     * @param orderItem 订单项实体
     * @return 订单项DTO
     */
    public static OrderItemDTO fromEntity(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setOrderId(orderItem.getOrderId());
        dto.setProductId(orderItem.getProductId());
        dto.setQuantity(orderItem.getQuantity());
        dto.setUnitPrice(orderItem.getUnitPrice());
        dto.setDiscountRate(orderItem.getDiscountRate());
        dto.setDiscountAmount(orderItem.getDiscountAmount());
        dto.setSubtotal(orderItem.getSubtotal());
        dto.setNotes(orderItem.getNotes());
        dto.setCreatedAt(orderItem.getCreatedAt());
        dto.setUpdatedAt(orderItem.getUpdatedAt());

        return dto;
    }

    /**
     * 转换为实体对象
     *
     * @return 订单项实体
     */
    public OrderItem toEntity() {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(this.id);
        orderItem.setOrderId(this.orderId);
        orderItem.setProductId(this.productId);
        orderItem.setQuantity(this.quantity);
        orderItem.setUnitPrice(this.unitPrice);
        orderItem.setDiscountRate(this.discountRate);
        orderItem.setDiscountAmount(this.discountAmount);
        orderItem.setSubtotal(this.subtotal);
        orderItem.setNotes(this.notes);

        return orderItem;
    }
} 