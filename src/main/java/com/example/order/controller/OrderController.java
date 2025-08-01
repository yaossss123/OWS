package com.example.order.controller;

import com.example.order.dto.OrderDTO;
import com.example.order.dto.OrderItemDTO;
import com.example.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 订单管理控制器
 * 
 * @author Order Management System
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "订单管理", description = "订单相关API接口")
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     * 
     * @param orderDTO 订单信息
     * @param orderItems 订单项列表
     * @return 创建的订单信息
     */
    @PostMapping
    @Operation(summary = "创建订单", description = "创建新订单，包含订单项信息")
    public ResponseEntity<OrderDTO> createOrder(
            @Valid @RequestBody OrderDTO orderDTO,
            @RequestBody(required = false) List<OrderItemDTO> orderItems) {
        
        log.info("开始创建订单，客户ID: {}", orderDTO.getCustomerId());
        
        try {
            OrderDTO createdOrder = orderService.createOrder(orderDTO, orderItems);
            log.info("订单创建成功，订单ID: {}", createdOrder.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (Exception e) {
            log.error("订单创建失败，错误信息: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 根据ID查询订单
     * 
     * @param id 订单ID
     * @return 订单信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询订单", description = "根据订单ID获取订单详细信息")
    public ResponseEntity<OrderDTO> getOrderById(
            @Parameter(description = "订单ID") @PathVariable @NotNull Long id) {
        
        log.info("查询订单，订单ID: {}", id);
        
        return orderService.findById(id)
                .map(order -> {
                    log.info("订单查询成功，订单ID: {}", id);
                    return ResponseEntity.ok(order);
                })
                .orElseGet(() -> {
                    log.warn("订单不存在，订单ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 根据订单编号查询订单
     * 
     * @param orderNumber 订单编号
     * @return 订单信息
     */
    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "根据订单编号查询订单", description = "根据订单编号获取订单详细信息")
    public ResponseEntity<OrderDTO> getOrderByNumber(
            @Parameter(description = "订单编号") @PathVariable @NotNull String orderNumber) {
        
        log.info("根据订单编号查询订单，订单编号: {}", orderNumber);
        
        return orderService.findByOrderNumber(orderNumber)
                .map(order -> {
                    log.info("订单查询成功，订单编号: {}", orderNumber);
                    return ResponseEntity.ok(order);
                })
                .orElseGet(() -> {
                    log.warn("订单不存在，订单编号: {}", orderNumber);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 更新订单信息
     * 
     * @param id 订单ID
     * @param orderDTO 更新的订单信息
     * @return 更新后的订单信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新订单", description = "更新订单基本信息")
    public ResponseEntity<OrderDTO> updateOrder(
            @Parameter(description = "订单ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody OrderDTO orderDTO) {
        
        log.info("更新订单，订单ID: {}", id);
        
        try {
            OrderDTO updatedOrder = orderService.updateOrder(id, orderDTO);
            log.info("订单更新成功，订单ID: {}", id);
            
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            log.error("订单更新失败，订单ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 更新订单状态
     * 
     * @param id 订单ID
     * @param status 新状态
     * @return 更新后的订单信息
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "更新订单状态", description = "更新订单的处理状态")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @Parameter(description = "订单ID") @PathVariable @NotNull Long id,
            @Parameter(description = "订单状态") @RequestParam @NotNull String status) {
        
        log.info("更新订单状态，订单ID: {}, 新状态: {}", id, status);
        
        try {
            OrderDTO updatedOrder = orderService.updateOrderStatus(id, 
                    com.example.order.entity.Order.OrderStatus.valueOf(status));
            log.info("订单状态更新成功，订单ID: {}", id);
            
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            log.error("订单状态更新失败，订单ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 更新支付状态
     * 
     * @param id 订单ID
     * @param paymentStatus 新支付状态
     * @return 更新后的订单信息
     */
    @PatchMapping("/{id}/payment-status")
    @Operation(summary = "更新支付状态", description = "更新订单的支付状态")
    public ResponseEntity<OrderDTO> updatePaymentStatus(
            @Parameter(description = "订单ID") @PathVariable @NotNull Long id,
            @Parameter(description = "支付状态") @RequestParam @NotNull String paymentStatus) {
        
        log.info("更新支付状态，订单ID: {}, 新支付状态: {}", id, paymentStatus);
        
        try {
            OrderDTO updatedOrder = orderService.updatePaymentStatus(id, 
                    com.example.order.entity.Order.PaymentStatus.valueOf(paymentStatus));
            log.info("支付状态更新成功，订单ID: {}", id);
            
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            log.error("支付状态更新失败，订单ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 删除订单
     * 
     * @param id 订单ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除订单", description = "删除指定订单")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "订单ID") @PathVariable @NotNull Long id) {
        
        log.info("删除订单，订单ID: {}", id);
        
        try {
            orderService.deleteOrder(id);
            log.info("订单删除成功，订单ID: {}", id);
            
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("订单删除失败，订单ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 分页查询所有订单
     * 
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    @GetMapping
    @Operation(summary = "分页查询订单", description = "分页查询所有订单")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("分页查询订单，页码: {}, 大小: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<OrderDTO> orders = orderService.findAll(pageable);
        log.info("订单查询完成，总记录数: {}", orders.getTotalElements());
        
        return ResponseEntity.ok(orders);
    }

    /**
     * 根据客户ID查询订单
     * 
     * @param customerId 客户ID
     * @return 订单列表
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "根据客户ID查询订单", description = "查询指定客户的所有订单")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomerId(
            @Parameter(description = "客户ID") @PathVariable @NotNull Long customerId) {
        
        log.info("根据客户ID查询订单，客户ID: {}", customerId);
        
        List<OrderDTO> orders = orderService.findByCustomerId(customerId);
        log.info("客户订单查询完成，客户ID: {}, 订单数量: {}", customerId, orders.size());
        
        return ResponseEntity.ok(orders);
    }

    /**
     * 根据状态查询订单
     * 
     * @param status 订单状态
     * @return 订单列表
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态查询订单", description = "查询指定状态的所有订单")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(
            @Parameter(description = "订单状态") @PathVariable @NotNull String status) {
        
        log.info("根据状态查询订单，状态: {}", status);
        
        List<OrderDTO> orders = orderService.findByStatus(
                com.example.order.entity.Order.OrderStatus.valueOf(status));
        log.info("状态订单查询完成，状态: {}, 订单数量: {}", status, orders.size());
        
        return ResponseEntity.ok(orders);
    }

    /**
     * 根据支付状态查询订单
     * 
     * @param paymentStatus 支付状态
     * @return 订单列表
     */
    @GetMapping("/payment-status/{paymentStatus}")
    @Operation(summary = "根据支付状态查询订单", description = "查询指定支付状态的所有订单")
    public ResponseEntity<List<OrderDTO>> getOrdersByPaymentStatus(
            @Parameter(description = "支付状态") @PathVariable @NotNull String paymentStatus) {
        
        log.info("根据支付状态查询订单，支付状态: {}", paymentStatus);
        
        List<OrderDTO> orders = orderService.findByPaymentStatus(
                com.example.order.entity.Order.PaymentStatus.valueOf(paymentStatus));
        log.info("支付状态订单查询完成，支付状态: {}, 订单数量: {}", paymentStatus, orders.size());
        
        return ResponseEntity.ok(orders);
    }

    /**
     * 根据日期范围查询订单
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    @GetMapping("/date-range")
    @Operation(summary = "根据日期范围查询订单", description = "查询指定日期范围内的订单")
    public ResponseEntity<Page<OrderDTO>> getOrdersByDateRange(
            @Parameter(description = "开始日期") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("根据日期范围查询订单，开始日期: {}, 结束日期: {}", startDate, endDate);
        
        Page<OrderDTO> orders = orderService.findByOrderDateBetween(startDate, endDate, pageable);
        log.info("日期范围订单查询完成，总记录数: {}", orders.getTotalElements());
        
        return ResponseEntity.ok(orders);
    }

    /**
     * 检查订单编号是否存在
     * 
     * @param orderNumber 订单编号
     * @return 是否存在
     */
    @GetMapping("/exists/{orderNumber}")
    @Operation(summary = "检查订单编号是否存在", description = "检查指定订单编号是否已存在")
    public ResponseEntity<Boolean> checkOrderNumberExists(
            @Parameter(description = "订单编号") @PathVariable @NotNull String orderNumber) {
        
        log.info("检查订单编号是否存在，订单编号: {}", orderNumber);
        
        boolean exists = orderService.existsByOrderNumber(orderNumber);
        log.info("订单编号检查完成，订单编号: {}, 存在: {}", orderNumber, exists);
        
        return ResponseEntity.ok(exists);
    }

    /**
     * 统计订单状态分布
     * 
     * @return 状态统计结果
     */
    @GetMapping("/statistics/status")
    @Operation(summary = "统计订单状态分布", description = "统计各状态订单数量")
    public ResponseEntity<List<Object[]>> getOrderStatusStatistics() {
        
        log.info("统计订单状态分布");
        
        List<Object[]> statistics = orderService.countByStatus();
        log.info("订单状态统计完成，统计项数量: {}", statistics.size());
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * 统计支付状态分布
     * 
     * @return 支付状态统计结果
     */
    @GetMapping("/statistics/payment-status")
    @Operation(summary = "统计支付状态分布", description = "统计各支付状态订单数量")
    public ResponseEntity<List<Object[]>> getPaymentStatusStatistics() {
        
        log.info("统计支付状态分布");
        
        List<Object[]> statistics = orderService.countByPaymentStatus();
        log.info("支付状态统计完成，统计项数量: {}", statistics.size());
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * 计算日期范围内的订单总金额
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总金额
     */
    @GetMapping("/statistics/amount")
    @Operation(summary = "计算日期范围内订单总金额", description = "计算指定日期范围内订单的最终金额总和")
    public ResponseEntity<BigDecimal> getOrderAmountByDateRange(
            @Parameter(description = "开始日期") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("计算日期范围内订单总金额，开始日期: {}, 结束日期: {}", startDate, endDate);
        
        BigDecimal totalAmount = orderService.sumFinalAmountByOrderDateBetween(startDate, endDate);
        log.info("订单总金额计算完成，总金额: {}", totalAmount);
        
        return ResponseEntity.ok(totalAmount);
    }
} 