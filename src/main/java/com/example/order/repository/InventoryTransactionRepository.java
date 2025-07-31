package com.example.order.repository;

import com.example.order.entity.InventoryTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 库存变动数据访问层
 * 
 * 功能: 提供库存变动数据的CRUD操作
 * 逻辑链: 查询条件 -> 数据过滤 -> 结果返回 -> 分页处理
 * 注意事项: 需要处理产品和关联类型的关联关系
 * 
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    /**
     * 根据产品ID查找库存变动记录
     * 
     * @param productId 产品ID
     * @return 库存变动记录列表
     */
    List<InventoryTransaction> findByProductId(Long productId);

    /**
     * 根据产品ID查找库存变动记录（分页）
     * 
     * @param productId 产品ID
     * @param pageable 分页参数
     * @return 库存变动记录分页结果
     */
    Page<InventoryTransaction> findByProductId(Long productId, Pageable pageable);

    /**
     * 根据变动类型查找库存变动记录
     * 
     * @param transactionType 变动类型
     * @return 库存变动记录列表
     */
    List<InventoryTransaction> findByTransactionType(InventoryTransaction.TransactionType transactionType);

    /**
     * 根据关联类型查找库存变动记录
     * 
     * @param referenceType 关联类型
     * @return 库存变动记录列表
     */
    List<InventoryTransaction> findByReferenceType(InventoryTransaction.ReferenceType referenceType);

    /**
     * 根据关联ID查找库存变动记录
     * 
     * @param referenceId 关联ID
     * @return 库存变动记录列表
     */
    List<InventoryTransaction> findByReferenceId(Long referenceId);

    /**
     * 根据产品ID和变动类型查找库存变动记录
     * 
     * @param productId 产品ID
     * @param transactionType 变动类型
     * @return 库存变动记录列表
     */
    List<InventoryTransaction> findByProductIdAndTransactionType(Long productId, 
                                                               InventoryTransaction.TransactionType transactionType);

    /**
     * 根据产品ID和关联类型查找库存变动记录
     * 
     * @param productId 产品ID
     * @param referenceType 关联类型
     * @return 库存变动记录列表
     */
    List<InventoryTransaction> findByProductIdAndReferenceType(Long productId, 
                                                              InventoryTransaction.ReferenceType referenceType);

    /**
     * 根据变动数量范围查找库存变动记录
     * 
     * @param minQuantity 最小变动数量
     * @param maxQuantity 最大变动数量
     * @return 库存变动记录列表
     */
    List<InventoryTransaction> findByQuantityBetween(Integer minQuantity, Integer maxQuantity);

    /**
     * 根据创建时间范围查找库存变动记录
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 库存变动记录列表
     */
    List<InventoryTransaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 根据创建时间范围查找库存变动记录（分页）
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param pageable 分页参数
     * @return 库存变动记录分页结果
     */
    Page<InventoryTransaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 查找入库记录
     * 
     * @return 入库记录列表
     */
    @Query("SELECT it FROM InventoryTransaction it WHERE it.transactionType = 'IN'")
    List<InventoryTransaction> findInTransactions();

    /**
     * 查找出库记录
     * 
     * @return 出库记录列表
     */
    @Query("SELECT it FROM InventoryTransaction it WHERE it.transactionType = 'OUT'")
    List<InventoryTransaction> findOutTransactions();

    /**
     * 查找调整记录
     * 
     * @return 调整记录列表
     */
    @Query("SELECT it FROM InventoryTransaction it WHERE it.transactionType = 'ADJUSTMENT'")
    List<InventoryTransaction> findAdjustmentTransactions();

    /**
     * 查找订单相关的库存变动记录
     * 
     * @return 订单相关的库存变动记录列表
     */
    @Query("SELECT it FROM InventoryTransaction it WHERE it.referenceType = 'ORDER'")
    List<InventoryTransaction> findOrderRelatedTransactions();

    /**
     * 查找采购相关的库存变动记录
     * 
     * @return 采购相关的库存变动记录列表
     */
    @Query("SELECT it FROM InventoryTransaction it WHERE it.referenceType = 'PURCHASE'")
    List<InventoryTransaction> findPurchaseRelatedTransactions();

    /**
     * 查找退货相关的库存变动记录
     * 
     * @return 退货相关的库存变动记录列表
     */
    @Query("SELECT it FROM InventoryTransaction it WHERE it.referenceType = 'RETURN'")
    List<InventoryTransaction> findReturnRelatedTransactions();

    /**
     * 根据产品ID统计入库数量
     * 
     * @param productId 产品ID
     * @return 入库数量
     */
    @Query("SELECT SUM(it.quantity) FROM InventoryTransaction it " +
           "WHERE it.productId = :productId AND it.transactionType = 'IN'")
    Long sumInQuantityByProductId(@Param("productId") Long productId);

    /**
     * 根据产品ID统计出库数量
     * 
     * @param productId 产品ID
     * @return 出库数量
     */
    @Query("SELECT SUM(it.quantity) FROM InventoryTransaction it " +
           "WHERE it.productId = :productId AND it.transactionType = 'OUT'")
    Long sumOutQuantityByProductId(@Param("productId") Long productId);

    /**
     * 根据产品ID统计调整数量
     * 
     * @param productId 产品ID
     * @return 调整数量
     */
    @Query("SELECT SUM(it.quantity) FROM InventoryTransaction it " +
           "WHERE it.productId = :productId AND it.transactionType = 'ADJUSTMENT'")
    Long sumAdjustmentQuantityByProductId(@Param("productId") Long productId);

    /**
     * 根据产品ID计算净库存变动
     * 
     * @param productId 产品ID
     * @return 净库存变动数量
     */
    @Query("SELECT " +
           "(COALESCE(SUM(CASE WHEN it.transactionType = 'IN' THEN it.quantity ELSE 0 END), 0) + " +
           "COALESCE(SUM(CASE WHEN it.transactionType = 'ADJUSTMENT' THEN it.quantity ELSE 0 END), 0)) - " +
           "COALESCE(SUM(CASE WHEN it.transactionType = 'OUT' THEN it.quantity ELSE 0 END), 0) " +
           "FROM InventoryTransaction it WHERE it.productId = :productId")
    Long calculateNetInventoryChange(@Param("productId") Long productId);

    /**
     * 统计各变动类型的记录数量
     * 
     * @return 变动类型统计结果
     */
    @Query("SELECT it.transactionType, COUNT(it) FROM InventoryTransaction it GROUP BY it.transactionType")
    List<Object[]> countByTransactionType();

    /**
     * 统计各关联类型的记录数量
     * 
     * @return 关联类型统计结果
     */
    @Query("SELECT it.referenceType, COUNT(it) FROM InventoryTransaction it GROUP BY it.referenceType")
    List<Object[]> countByReferenceType();

    /**
     * 查找最近的产品库存变动记录
     * 
     * @param productId 产品ID
     * @param limit 限制数量
     * @return 最近的库存变动记录
     */
    @Query("SELECT it FROM InventoryTransaction it WHERE it.productId = :productId " +
           "ORDER BY it.createdAt DESC")
    List<InventoryTransaction> findRecentTransactionsByProductId(@Param("productId") Long productId, 
                                                               @Param("limit") int limit);

    /**
     * 查找指定时间范围内的库存变动记录
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param pageable 分页参数
     * @return 库存变动记录分页结果
     */
    @Query("SELECT it FROM InventoryTransaction it WHERE it.createdAt BETWEEN :startDate AND :endDate")
    Page<InventoryTransaction> findByCreatedAtBetween(@Param("startDate") String startDate,
                                                     @Param("endDate") String endDate,
                                                     Pageable pageable);

    /**
     * 统计各产品的库存变动情况
     * 
     * @return 产品库存变动统计结果
     */
    @Query("SELECT it.productId, " +
           "COUNT(it) as transactionCount, " +
           "SUM(CASE WHEN it.transactionType = 'IN' THEN it.quantity ELSE 0 END) as totalIn, " +
           "SUM(CASE WHEN it.transactionType = 'OUT' THEN it.quantity ELSE 0 END) as totalOut, " +
           "SUM(CASE WHEN it.transactionType = 'ADJUSTMENT' THEN it.quantity ELSE 0 END) as totalAdjustment " +
           "FROM InventoryTransaction it " +
           "GROUP BY it.productId")
    List<Object[]> getProductInventoryStatistics();
} 