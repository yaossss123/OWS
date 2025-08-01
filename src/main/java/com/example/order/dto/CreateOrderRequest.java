package com.example.order.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单请求DTO
 *
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Data
public class CreateOrderRequest {

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotBlank(message = "收货地址不能为空")
    private String shippingAddress;

    private String notes;

    @NotEmpty(message = "订单项不能为空")
    private List<OrderItemRequest> items;

    /**
     * 订单项请求DTO
     */
    @Data
    public static class OrderItemRequest {
        @NotNull(message = "产品ID不能为空")
        private Long productId;

        @NotNull(message = "数量不能为空")
        private Integer quantity;

        @NotNull(message = "单价不能为空")
        private BigDecimal unitPrice;
    }
} 