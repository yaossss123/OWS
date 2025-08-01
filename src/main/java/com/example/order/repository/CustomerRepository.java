package com.example.order.repository;

import com.example.order.entity.Customer;
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
 * 客户数据访问层
 * 
 * 功能: 提供客户数据的CRUD操作
 * 逻辑链: 查询条件 -> 数据过滤 -> 结果返回 -> 分页处理
 * 注意事项: 需要处理客户编码的唯一性约束
 * 
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * 根据客户编码查找客户
     * 
     * @param customerCode 客户编码
     * @return 客户信息
     */
    Optional<Customer> findByCustomerCode(String customerCode);

    /**
     * 根据邮箱查找客户
     * 
     * @param email 邮箱
     * @return 客户信息
     */
    Optional<Customer> findByEmail(String email);

    /**
     * 根据手机号查找客户
     * 
     * @param phone 手机号
     * @return 客户信息
     */
    Optional<Customer> findByPhone(String phone);

    /**
     * 检查客户编码是否存在
     * 
     * @param customerCode 客户编码
     * @return 是否存在
     */
    boolean existsByCustomerCode(String customerCode);

    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     * 
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 根据状态查找客户
     * 
     * @param status 客户状态
     * @return 客户列表
     */
    List<Customer> findByStatus(Customer.CustomerStatus status);

    /**
     * 根据客户名称模糊查询
     * 
     * @param name 客户名称
     * @param pageable 分页参数
     * @return 客户分页结果
     */
    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * 根据联系人模糊查询
     * 
     * @param contactPerson 联系人
     * @param pageable 分页参数
     * @return 客户分页结果
     */
    Page<Customer> findByContactPersonContainingIgnoreCase(String contactPerson, Pageable pageable);

    /**
     * 根据状态和客户名称查询
     * 
     * @param status 客户状态
     * @param name 客户名称
     * @param pageable 分页参数
     * @return 客户分页结果
     */
    Page<Customer> findByStatusAndNameContainingIgnoreCase(Customer.CustomerStatus status, 
                                                          String name, Pageable pageable);

    /**
     * 查找所有激活状态的客户
     * 
     * @return 激活客户列表
     */
    @Query("SELECT c FROM Customer c WHERE c.status = 'ACTIVE'")
    List<Customer> findAllActiveCustomers();

    /**
     * 根据信用额度范围查找客户
     * 
     * @param minCreditLimit 最小信用额度
     * @param maxCreditLimit 最大信用额度
     * @param pageable 分页参数
     * @return 客户分页结果
     */
    @Query("SELECT c FROM Customer c WHERE c.creditLimit BETWEEN :minCreditLimit AND :maxCreditLimit")
    Page<Customer> findByCreditLimitBetween(@Param("minCreditLimit") BigDecimal minCreditLimit,
                                           @Param("maxCreditLimit") BigDecimal maxCreditLimit,
                                           Pageable pageable);

    /**
     * 根据创建时间范围查找客户
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param pageable 分页参数
     * @return 客户分页结果
     */
    @Query("SELECT c FROM Customer c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    Page<Customer> findByCreatedAtBetween(@Param("startDate") String startDate,
                                         @Param("endDate") String endDate,
                                         Pageable pageable);

    /**
     * 统计各状态客户数量
     * 
     * @return 状态统计结果
     */
    @Query("SELECT c.status, COUNT(c) FROM Customer c GROUP BY c.status")
    List<Object[]> countByStatus();

    /**
     * 统计信用额度分布
     * 
     * @return 信用额度统计结果
     */
    @Query("SELECT " +
           "CASE " +
           "  WHEN c.creditLimit = 0 THEN '无信用额度' " +
           "  WHEN c.creditLimit <= 10000 THEN '低信用额度' " +
           "  WHEN c.creditLimit <= 50000 THEN '中信用额度' " +
           "  ELSE '高信用额度' " +
           "END, COUNT(c) " +
           "FROM Customer c GROUP BY " +
           "CASE " +
           "  WHEN c.creditLimit = 0 THEN '无信用额度' " +
           "  WHEN c.creditLimit <= 10000 THEN '低信用额度' " +
           "  WHEN c.creditLimit <= 50000 THEN '中信用额度' " +
           "  ELSE '高信用额度' " +
           "END")
    List<Object[]> countByCreditLimitRange();
} 