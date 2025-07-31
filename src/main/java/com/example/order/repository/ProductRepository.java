package com.example.order.repository;

import com.example.order.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 产品数据访问层
 * 
 * 功能: 提供产品数据的CRUD操作
 * 逻辑链: 查询条件 -> 数据过滤 -> 结果返回 -> 分页处理
 * 注意事项: 需要处理产品编码的唯一性约束
 * 
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 根据产品编码查找产品
     * 
     * @param productCode 产品编码
     * @return 产品信息
     */
    Optional<Product> findByProductCode(String productCode);

    /**
     * 根据产品名称查找产品
     * 
     * @param name 产品名称
     * @return 产品信息
     */
    Optional<Product> findByName(String name);

    /**
     * 检查产品编码是否存在
     * 
     * @param productCode 产品编码
     * @return 是否存在
     */
    boolean existsByProductCode(String productCode);

    /**
     * 检查产品名称是否存在
     * 
     * @param name 产品名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 根据状态查找产品
     * 
     * @param status 产品状态
     * @return 产品列表
     */
    List<Product> findByStatus(Product.ProductStatus status);

    /**
     * 根据分类查找产品
     * 
     * @param category 产品分类
     * @return 产品列表
     */
    List<Product> findByCategory(String category);

    /**
     * 根据状态和分类查找产品
     * 
     * @param status 产品状态
     * @param category 产品分类
     * @return 产品列表
     */
    List<Product> findByStatusAndCategory(Product.ProductStatus status, String category);

    /**
     * 根据产品名称模糊查询
     * 
     * @param name 产品名称
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * 根据分类模糊查询
     * 
     * @param category 产品分类
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    Page<Product> findByCategoryContainingIgnoreCase(String category, Pageable pageable);

    /**
     * 根据状态和产品名称查询
     * 
     * @param status 产品状态
     * @param name 产品名称
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    Page<Product> findByStatusAndNameContainingIgnoreCase(Product.ProductStatus status, 
                                                         String name, Pageable pageable);

    /**
     * 查找所有激活状态的产品
     * 
     * @return 激活产品列表
     */
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE'")
    List<Product> findAllActiveProducts();

    /**
     * 查找库存不足的产品
     * 
     * @return 库存不足产品列表
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.minStock")
    List<Product> findLowStockProducts();

    /**
     * 查找缺货产品
     * 
     * @return 缺货产品列表
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0")
    List<Product> findOutOfStockProducts();

    /**
     * 根据价格范围查找产品
     * 
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    @Query("SELECT p FROM Product p WHERE p.unitPrice BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByUnitPriceBetween(@Param("minPrice") BigDecimal minPrice,
                                        @Param("maxPrice") BigDecimal maxPrice,
                                        Pageable pageable);

    /**
     * 根据库存范围查找产品
     * 
     * @param minStock 最小库存
     * @param maxStock 最大库存
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity BETWEEN :minStock AND :maxStock")
    Page<Product> findByStockQuantityBetween(@Param("minStock") Integer minStock,
                                            @Param("maxStock") Integer maxStock,
                                            Pageable pageable);

    /**
     * 根据创建时间范围查找产品
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    @Query("SELECT p FROM Product p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    Page<Product> findByCreatedAtBetween(@Param("startDate") String startDate,
                                        @Param("endDate") String endDate,
                                        Pageable pageable);

    /**
     * 统计各状态产品数量
     * 
     * @return 状态统计结果
     */
    @Query("SELECT p.status, COUNT(p) FROM Product p GROUP BY p.status")
    List<Object[]> countByStatus();

    /**
     * 统计各分类产品数量
     * 
     * @return 分类统计结果
     */
    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category")
    List<Object[]> countByCategory();

    /**
     * 统计库存分布
     * 
     * @return 库存统计结果
     */
    @Query("SELECT " +
           "CASE " +
           "  WHEN p.stockQuantity = 0 THEN '缺货' " +
           "  WHEN p.stockQuantity <= p.minStock THEN '库存不足' " +
           "  WHEN p.stockQuantity <= 100 THEN '库存充足' " +
           "  ELSE '库存丰富' " +
           "END, COUNT(p) " +
           "FROM Product p GROUP BY " +
           "CASE " +
           "  WHEN p.stockQuantity = 0 THEN '缺货' " +
           "  WHEN p.stockQuantity <= p.minStock THEN '库存不足' " +
           "  WHEN p.stockQuantity <= 100 THEN '库存充足' " +
           "  ELSE '库存丰富' " +
           "END")
    List<Object[]> countByStockLevel();

    /**
     * 查找需要补货的产品
     * 
     * @return 需要补货的产品列表
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.minStock AND p.status = 'ACTIVE'")
    List<Product> findProductsNeedingRestock();
} 