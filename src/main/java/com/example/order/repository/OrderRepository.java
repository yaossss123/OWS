package com.example.order.repository;

import com.example.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问层
 * 
 * 功能: 提供订单数据的CRUD操作
 * 逻辑链: 查询条件 -> 数据过滤 -> 结果返回 -> 分页处理
 * 注意事项: 需要处理订单编号的唯一性约束
 * 
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 根据订单编号查找订单
     * 
     * @param orderNumber 订单编号
     * @return 订单信息
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * 检查订单编号是否存在
     * 
     * @param orderNumber 订单编号
     * @return 是否存在
     */
    boolean existsByOrderNumber(String orderNumber);

    /**
     * 根据客户ID查找订单
     * 
     * @param customerId 客户ID
     * @return 订单列表
     */
    List<Order> findByCustomerId(Long customerId);

    /**
     * 根据客户ID查找订单（分页）
     * 
     * @param customerId 客户ID
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * 根据订单状态查找订单
     * 
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> findByStatus(Order.OrderStatus status);

    /**
     * 根据支付状态查找订单
     * 
     * @param paymentStatus 支付状态
     * @return 订单列表
     */
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);

    /**
     * 根据订单状态和支付状态查找订单
     * 
     * @param status 订单状态
     * @param paymentStatus 支付状态
     * @return 订单列表
     */
    List<Order> findByStatusAndPaymentStatus(Order.OrderStatus status, Order.PaymentStatus paymentStatus);

    /**
     * 根据订单日期查找订单
     * 
     * @param orderDate 订单日期
     * @return 订单列表
     */
    List<Order> findByOrderDate(LocalDate orderDate);

    /**
     * 根据订单日期范围查找订单
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单列表
     */
    List<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 根据订单日期范围查找订单（分页）
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * 根据订单编号模糊查询
     * 
     * @param orderNumber 订单编号
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<Order> findByOrderNumberContainingIgnoreCase(String orderNumber, Pageable pageable);

    /**
     * 根据状态和订单编号查询
     * 
     * @param status 订单状态
     * @param orderNumber 订单编号
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<Order> findByStatusAndOrderNumberContainingIgnoreCase(Order.OrderStatus status, 
                                                              String orderNumber, Pageable pageable);

    /**
     * 根据客户ID和状态查找订单
     * 
     * @param customerId 客户ID
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> findByCustomerIdAndStatus(Long customerId, Order.OrderStatus status);

    /**
     * 根据客户ID和支付状态查找订单
     * 
     * @param customerId 客户ID
     * @param paymentStatus 支付状态
     * @return 订单列表
     */
    List<Order> findByCustomerIdAndPaymentStatus(Long customerId, Order.PaymentStatus paymentStatus);

    /**
     * 查找所有待确认的订单
     * 
     * @return 待确认订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING'")
    List<Order> findPendingOrders();

    /**
     * 查找所有已发货的订单
     * 
     * @return 已发货订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'SHIPPED'")
    List<Order> findShippedOrders();

    /**
     * 查找所有已送达的订单
     * 
     * @return 已送达订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'DELIVERED'")
    List<Order> findDeliveredOrders();

    /**
     * 查找所有已取消的订单
     * 
     * @return 已取消订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'CANCELLED'")
    List<Order> findCancelledOrders();

    /**
     * 根据金额范围查找订单
     * 
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    @Query("SELECT o FROM Order o WHERE o.finalAmount BETWEEN :minAmount AND :maxAmount")
    Page<Order> findByFinalAmountBetween(@Param("minAmount") BigDecimal minAmount,
                                        @Param("maxAmount") BigDecimal maxAmount,
                                        Pageable pageable);

    /**
     * 根据创建时间范围查找订单
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    Page<Order> findByCreatedAtBetween(@Param("startDate") String startDate,
                                      @Param("endDate") String endDate,
                                      Pageable pageable);

    /**
     * 统计各状态订单数量
     * 
     * @return 状态统计结果
     */
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countByStatus();

    /**
     * 统计各支付状态订单数量
     * 
     * @return 支付状态统计结果
     */
    @Query("SELECT o.paymentStatus, COUNT(o) FROM Order o GROUP BY o.paymentStatus")
    List<Object[]> countByPaymentStatus();

    /**
     * 统计订单金额分布
     * 
     * @return 金额分布统计结果
     */
    @Query("SELECT " +
           "CASE " +
           "  WHEN o.finalAmount <= 1000 THEN '小额订单' " +
           "  WHEN o.finalAmount <= 10000 THEN '中额订单' " +
           "  WHEN o.finalAmount <= 100000 THEN '大额订单' " +
           "  ELSE '超大额订单' " +
           "END, COUNT(o) " +
           "FROM Order o GROUP BY " +
           "CASE " +
           "  WHEN o.finalAmount <= 1000 THEN '小额订单' " +
           "  WHEN o.finalAmount <= 10000 THEN '中额订单' " +
           "  WHEN o.finalAmount <= 100000 THEN '大额订单' " +
           "  ELSE '超大额订单' " +
           "END")
    List<Object[]> countByAmountRange();

    /**
     * 计算指定日期范围内的订单总金额
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单总金额
     */
    @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal sumFinalAmountByOrderDateBetween(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 查找指定客户的最新订单
     * 
     * @param customerId 客户ID
     * @return 最新订单
     */
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId ORDER BY o.createdAt DESC")
    List<Order> findLatestOrdersByCustomerId(@Param("customerId") Long customerId);
} 