package com.example.order.service;

import com.example.order.dto.OrderDTO;
import com.example.order.dto.OrderItemDTO;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.entity.Product;
import com.example.order.repository.OrderItemRepository;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单服务类
 *
 * 功能: 提供订单相关的业务逻辑处理
 * 逻辑链: 请求接收 -> 数据验证 -> 业务处理 -> 结果返回
 * 注意事项: 需要处理订单状态转换、金额计算和库存管理
 *
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    /**
     * 创建订单
     *
     * @param orderDTO 订单DTO
     * @param orderItems 订单项列表
     * @return 创建的订单DTO
     */
    public OrderDTO createOrder(OrderDTO orderDTO, List<OrderItemDTO> orderItems) {
        log.info("开始创建订单，订单编号: {}", orderDTO.getOrderNumber());

        // 验证订单编号唯一性
        if (orderRepository.existsByOrderNumber(orderDTO.getOrderNumber())) {
            throw new RuntimeException("订单编号已存在");
        }

        // 验证库存
        for (OrderItemDTO item : orderItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("产品不存在"));
            
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("产品库存不足: " + product.getName());
            }
        }

        // 保存订单
        Order order = orderDTO.toEntity();
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentStatus(Order.PaymentStatus.UNPAID);
        order.setCurrency("CNY");

        Order savedOrder = orderRepository.save(order);

        // 保存订单项并计算金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemDTO itemDTO : orderItems) {
            OrderItem item = itemDTO.toEntity();
            item.setOrderId(savedOrder.getId());
            
            // 计算小计
            BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            if (item.getDiscountRate() != null && item.getDiscountRate().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discountAmount = subtotal.multiply(item.getDiscountRate()).divide(BigDecimal.valueOf(100));
                item.setDiscountAmount(discountAmount);
                subtotal = subtotal.subtract(discountAmount);
            }
            item.setSubtotal(subtotal);
            totalAmount = totalAmount.add(subtotal);
            
            orderItemRepository.save(item);
            
            // 减少库存
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("产品不存在"));
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        // 更新订单金额
        savedOrder.setTotalAmount(totalAmount);
        savedOrder.setFinalAmount(totalAmount.subtract(savedOrder.getDiscountAmount()).add(savedOrder.getTaxAmount()));
        orderRepository.save(savedOrder);

        log.info("订单创建成功，订单ID: {}", savedOrder.getId());
        return OrderDTO.fromEntity(savedOrder);
    }

    /**
     * 根据ID查找订单
     *
     * @param id 订单ID
     * @return 订单DTO
     */
    @Transactional(readOnly = true)
    public Optional<OrderDTO> findById(Long id) {
        log.debug("查找订单，订单ID: {}", id);
        return orderRepository.findById(id).map(OrderDTO::fromEntity);
    }

    /**
     * 根据订单编号查找订单
     *
     * @param orderNumber 订单编号
     * @return 订单DTO
     */
    @Transactional(readOnly = true)
    public Optional<OrderDTO> findByOrderNumber(String orderNumber) {
        log.debug("根据订单编号查找订单，订单编号: {}", orderNumber);
        return orderRepository.findByOrderNumber(orderNumber).map(OrderDTO::fromEntity);
    }

    /**
     * 更新订单信息
     *
     * @param id 订单ID
     * @param orderDTO 订单DTO
     * @return 更新后的订单DTO
     */
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        log.info("开始更新订单信息，订单ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 检查订单编号唯一性（排除当前订单）
        if (!order.getOrderNumber().equals(orderDTO.getOrderNumber()) &&
            orderRepository.existsByOrderNumber(orderDTO.getOrderNumber())) {
            throw new RuntimeException("订单编号已存在");
        }

        // 更新订单信息
        order.setOrderNumber(orderDTO.getOrderNumber());
        order.setCustomerId(orderDTO.getCustomerId());
        order.setOrderDate(orderDTO.getOrderDate());
        order.setDeliveryDate(orderDTO.getDeliveryDate());
        order.setStatus(orderDTO.getStatus());
        order.setDiscountAmount(orderDTO.getDiscountAmount());
        order.setTaxAmount(orderDTO.getTaxAmount());
        order.setPaymentStatus(orderDTO.getPaymentStatus());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setNotes(orderDTO.getNotes());

        // 重新计算最终金额
        order.setFinalAmount(order.getTotalAmount().subtract(order.getDiscountAmount()).add(order.getTaxAmount()));

        Order updatedOrder = orderRepository.save(order);
        log.info("订单信息更新成功，订单ID: {}", updatedOrder.getId());

        return OrderDTO.fromEntity(updatedOrder);
    }

    /**
     * 更新订单状态
     *
     * @param id 订单ID
     * @param status 新状态
     * @return 更新后的订单DTO
     */
    public OrderDTO updateOrderStatus(Long id, Order.OrderStatus status) {
        log.info("更新订单状态，订单ID: {}, 新状态: {}", id, status);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 验证状态转换的合法性
        if (!isValidStatusTransition(order.getStatus(), status)) {
            throw new RuntimeException("无效的状态转换");
        }

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        log.info("订单状态更新成功，订单ID: {}, 状态: {}", updatedOrder.getId(), status);

        return OrderDTO.fromEntity(updatedOrder);
    }

    /**
     * 更新支付状态
     *
     * @param id 订单ID
     * @param paymentStatus 新支付状态
     * @return 更新后的订单DTO
     */
    public OrderDTO updatePaymentStatus(Long id, Order.PaymentStatus paymentStatus) {
        log.info("更新支付状态，订单ID: {}, 新支付状态: {}", id, paymentStatus);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        order.setPaymentStatus(paymentStatus);
        Order updatedOrder = orderRepository.save(order);
        log.info("支付状态更新成功，订单ID: {}, 支付状态: {}", updatedOrder.getId(), paymentStatus);

        return OrderDTO.fromEntity(updatedOrder);
    }

    /**
     * 删除订单
     *
     * @param id 订单ID
     */
    public void deleteOrder(Long id) {
        log.info("开始删除订单，订单ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 检查订单状态
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("只能删除待处理状态的订单");
        }

        // 恢复库存
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);
        for (OrderItem item : orderItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("产品不存在"));
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        // 删除订单项
        orderItemRepository.deleteByOrderId(id);

        // 删除订单
        orderRepository.delete(order);
        log.info("订单删除成功，订单ID: {}", id);
    }

    /**
     * 分页查询订单
     *
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> findAll(Pageable pageable) {
        log.debug("分页查询订单，页码: {}, 大小: {}", pageable.getPageNumber(), pageable.getPageSize());
        return orderRepository.findAll(pageable).map(OrderDTO::fromEntity);
    }

    /**
     * 根据客户ID查找订单
     *
     * @param customerId 客户ID
     * @return 订单列表
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> findByCustomerId(Long customerId) {
        log.debug("根据客户ID查找订单，客户ID: {}", customerId);
        return orderRepository.findByCustomerId(customerId).stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据状态查找订单
     *
     * @param status 订单状态
     * @return 订单列表
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> findByStatus(Order.OrderStatus status) {
        log.debug("根据状态查找订单，状态: {}", status);
        return orderRepository.findByStatus(status).stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据支付状态查找订单
     *
     * @param paymentStatus 支付状态
     * @return 订单列表
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> findByPaymentStatus(Order.PaymentStatus paymentStatus) {
        log.debug("根据支付状态查找订单，支付状态: {}", paymentStatus);
        return orderRepository.findByPaymentStatus(paymentStatus).stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据日期范围查找订单
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> findByOrderDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.debug("根据日期范围查找订单，范围: {} - {}", startDate, endDate);
        return orderRepository.findByOrderDateBetween(startDate, endDate, pageable)
                .map(OrderDTO::fromEntity);
    }

    /**
     * 检查订单编号是否存在
     *
     * @param orderNumber 订单编号
     * @return 是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByOrderNumber(String orderNumber) {
        return orderRepository.existsByOrderNumber(orderNumber);
    }

    /**
     * 统计各状态订单数量
     *
     * @return 状态统计结果
     */
    @Transactional(readOnly = true)
    public List<Object[]> countByStatus() {
        log.debug("统计各状态订单数量");
        return orderRepository.countByStatus();
    }

    /**
     * 统计各支付状态订单数量
     *
     * @return 支付状态统计结果
     */
    @Transactional(readOnly = true)
    public List<Object[]> countByPaymentStatus() {
        log.debug("统计各支付状态订单数量");
        return orderRepository.countByPaymentStatus();
    }

    /**
     * 计算指定日期范围的订单总金额
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单总金额
     */
    @Transactional(readOnly = true)
    public BigDecimal sumFinalAmountByOrderDateBetween(LocalDate startDate, LocalDate endDate) {
        log.debug("计算指定日期范围的订单总金额，范围: {} - {}", startDate, endDate);
        return orderRepository.sumFinalAmountByOrderDateBetween(startDate, endDate);
    }

    /**
     * 验证状态转换的合法性
     *
     * @param currentStatus 当前状态
     * @param newStatus 新状态
     * @return 是否合法
     */
    private boolean isValidStatusTransition(Order.OrderStatus currentStatus, Order.OrderStatus newStatus) {
        switch (currentStatus) {
            case PENDING:
                return newStatus == Order.OrderStatus.CONFIRMED || newStatus == Order.OrderStatus.CANCELLED;
            case CONFIRMED:
                return newStatus == Order.OrderStatus.PROCESSING || newStatus == Order.OrderStatus.CANCELLED;
            case PROCESSING:
                return newStatus == Order.OrderStatus.SHIPPED || newStatus == Order.OrderStatus.CANCELLED;
            case SHIPPED:
                return newStatus == Order.OrderStatus.DELIVERED;
            case DELIVERED:
                return false; // 已送达是终态
            case CANCELLED:
                return false; // 已取消是终态
            default:
                return false;
        }
    }
} 