package com.example.order.repository;

import com.example.order.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层
 * 
 * 功能: 提供用户数据的CRUD操作
 * 逻辑链: 查询条件 -> 数据过滤 -> 结果返回 -> 分页处理
 * 注意事项: 需要处理用户名和邮箱的唯一性约束
 * 
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据用户名或邮箱查找用户
     * 
     * @param username 用户名
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据状态查找用户
     * 
     * @param status 用户状态
     * @return 用户列表
     */
    List<User> findByStatus(User.UserStatus status);

    /**
     * 根据角色查找用户
     * 
     * @param role 用户角色
     * @return 用户列表
     */
    List<User> findByRole(User.UserRole role);

    /**
     * 根据状态和角色查找用户
     * 
     * @param status 用户状态
     * @param role 用户角色
     * @return 用户列表
     */
    List<User> findByStatusAndRole(User.UserStatus status, User.UserRole role);

    /**
     * 根据姓名模糊查询用户
     * 
     * @param fullName 姓名
     * @param pageable 分页参数
     * @return 用户分页结果
     */
    Page<User> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    /**
     * 根据手机号查找用户
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    Optional<User> findByPhone(String phone);

    /**
     * 查找所有激活状态的用户
     * 
     * @return 激活用户列表
     */
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    List<User> findAllActiveUsers();

    /**
     * 根据创建时间范围查找用户
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param pageable 分页参数
     * @return 用户分页结果
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    Page<User> findByCreatedAtBetween(@Param("startDate") String startDate, 
                                     @Param("endDate") String endDate, 
                                     Pageable pageable);

    /**
     * 统计各状态用户数量
     * 
     * @return 状态统计结果
     */
    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> countByStatus();

    /**
     * 统计各角色用户数量
     * 
     * @return 角色统计结果
     */
    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> countByRole();
} 