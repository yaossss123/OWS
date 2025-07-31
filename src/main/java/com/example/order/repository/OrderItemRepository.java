package com.example.order.repository;

import com.example.order.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单明细数据访问层
 * 
 * 功能: 提供订单明细数据的CRUD操作
 * 逻辑链: 查询条件 -> 数据过滤 -> 结果返回 -> 分页处理
 * 注意事项: 需要处理订单和产品的关联关系
 * 
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * 根据订单ID查找订单明细
     * 
     * @param orderId 订单ID
     * @return 订单明细列表
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * 根据产品ID查找订单明细
     * 
     * @param productId 产品ID
     * @return 订单明细列表
     */
    List<OrderItem> findByProductId(Long productId);

    /**
     * 根据订单ID和产品ID查找订单明细
     * 
     * @param orderId 订单ID
     * @param productId 产品ID
     * @return 订单明细列表
     */
    List<OrderItem> findByOrderIdAndProductId(Long orderId, Long productId);

    /**
     * 根据订单ID查找订单明细（分页）
     * 
     * @param orderId 订单ID
     * @param pageable 分页参数
     * @return 订单明细分页结果
     */
    Page<OrderItem> findByOrderId(Long orderId, Pageable pageable);

    /**
     * 根据产品ID查找订单明细（分页）
     * 
     * @param productId 产品ID
     * @param pageable 分页参数
     * @return 订单明细分页结果
     */
    Page<OrderItem> findByProductId(Long productId, Pageable pageable);

    /**
     * 根据数量范围查找订单明细
     * 
     * @param minQuantity 最小数量
     * @param maxQuantity 最大数量
     * @return 订单明细列表
     */
    List<OrderItem> findByQuantityBetween(Integer minQuantity, Integer maxQuantity);

    /**
     * 根据单价范围查找订单明细
     * 
     * @param minUnitPrice 最低单价
     * @param maxUnitPrice 最高单价
     * @return 订单明细列表
     */
    List<OrderItem> findByUnitPriceBetween(BigDecimal minUnitPrice, BigDecimal maxUnitPrice);

    /**
     * 根据小计金额范围查找订单明细
     * 
     * @param minSubtotal 最小小计金额
     * @param maxSubtotal 最大小计金额
     * @return 订单明细列表
     */
    List<OrderItem> findBySubtotalBetween(BigDecimal minSubtotal, BigDecimal maxSubtotal);

    /**
     * 根据折扣率范围查找订单明细
     * 
     * @param minDiscountRate 最小折扣率
     * @param maxDiscountRate 最大折扣率
     * @return 订单明细列表
     */
    List<OrderItem> findByDiscountRateBetween(BigDecimal minDiscountRate, BigDecimal maxDiscountRate);

    /**
     * 查找有折扣的订单明细
     * 
     * @return 有折扣的订单明细列表
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.discountRate > 0")
    List<OrderItem> findItemsWithDiscount();

    /**
     * 查找无折扣的订单明细
     * 
     * @return 无折扣的订单明细列表
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.discountRate = 0")
    List<OrderItem> findItemsWithoutDiscount();

    /**
     * 根据订单ID统计订单明细数量
     * 
     * @param orderId 订单ID
     * @return 订单明细数量
     */
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.orderId = :orderId")
    Long countByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据订单ID计算订单总金额
     * 
     * @param orderId 订单ID
     * @return 订单总金额
     */
    @Query("SELECT SUM(oi.subtotal) FROM OrderItem oi WHERE oi.orderId = :orderId")
    BigDecimal sumSubtotalByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据产品ID计算产品总销售额
     * 
     * @param productId 产品ID
     * @return 产品总销售额
     */
    @Query("SELECT SUM(oi.subtotal) FROM OrderItem oi WHERE oi.productId = :productId")
    BigDecimal sumSubtotalByProductId(@Param("productId") Long productId);

    /**
     * 根据产品ID统计产品总销售数量
     * 
     * @param productId 产品ID
     * @return 产品总销售数量
     */
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.productId = :productId")
    Long sumQuantityByProductId(@Param("productId") Long productId);

    /**
     * 查找销量最高的产品
     * 
     * @param limit 限制数量
     * @return 销量最高的产品ID和数量
     */
    @Query("SELECT oi.productId, SUM(oi.quantity) as totalQuantity " +
           "FROM OrderItem oi " +
           "GROUP BY oi.productId " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingProducts(@Param("limit") int limit);

    /**
     * 查找销售额最高的产品
     * 
     * @param limit 限制数量
     * @return 销售额最高的产品ID和金额
     */
    @Query("SELECT oi.productId, SUM(oi.subtotal) as totalSales " +
           "FROM OrderItem oi " +
           "GROUP BY oi.productId " +
           "ORDER BY totalSales DESC")
    List<Object[]> findTopRevenueProducts(@Param("limit") int limit);

    /**
     * 根据创建时间范围查找订单明细
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param pageable 分页参数
     * @return 订单明细分页结果
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.createdAt BETWEEN :startDate AND :endDate")
    Page<OrderItem> findByCreatedAtBetween(@Param("startDate") String startDate,
                                          @Param("endDate") String endDate,
                                          Pageable pageable);

    /**
     * 统计各产品的销售情况
     * 
     * @return 产品销售统计结果
     */
    @Query("SELECT oi.productId, COUNT(oi) as orderCount, " +
           "SUM(oi.quantity) as totalQuantity, " +
           "SUM(oi.subtotal) as totalSales " +
           "FROM OrderItem oi " +
           "GROUP BY oi.productId")
    List<Object[]> getProductSalesStatistics();

    /**
     * 查找指定订单中金额最高的明细
     * 
     * @param orderId 订单ID
     * @return 金额最高的订单明细
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.orderId = :orderId ORDER BY oi.subtotal DESC")
    List<OrderItem> findTopValueItemsByOrderId(@Param("orderId") Long orderId);
} 