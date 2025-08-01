package com.example.order.service;

import com.example.order.dto.UserDTO;
import com.example.order.entity.User;
import com.example.order.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户服务类
 * 
 * 功能: 提供用户相关的业务逻辑处理
 * 逻辑链: 请求接收 -> 数据验证 -> 业务处理 -> 结果返回
 * 注意事项: 需要处理密码加密、唯一性验证等安全相关逻辑
 * 
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 创建用户
     * 
     * @param userDTO 用户DTO
     * @return 创建的用户DTO
     */
    public UserDTO createUser(UserDTO userDTO) {
        log.info("开始创建用户，用户名: {}", userDTO.getUsername());
        
        // 验证用户名唯一性
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 验证邮箱唯一性
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 转换为实体并保存
        User user = userDTO.toEntity();
        user.setPassword(passwordEncoder.encode("123456")); // 默认密码
        user.setStatus(User.UserStatus.ACTIVE);
        user.setRole(User.UserRole.USER);
        
        User savedUser = userRepository.save(user);
        log.info("用户创建成功，用户ID: {}", savedUser.getId());
        
        return UserDTO.fromEntity(savedUser);
    }

    /**
     * 根据ID查找用户
     * 
     * @param id 用户ID
     * @return 用户DTO
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> findById(Long id) {
        log.debug("查找用户，用户ID: {}", id);
        return userRepository.findById(id).map(UserDTO::fromEntity);
    }

    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户DTO
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> findByUsername(String username) {
        log.debug("根据用户名查找用户，用户名: {}", username);
        return userRepository.findByUsername(username).map(UserDTO::fromEntity);
    }

    /**
     * 根据邮箱查找用户
     * 
     * @param email 邮箱
     * @return 用户DTO
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> findByEmail(String email) {
        log.debug("根据邮箱查找用户，邮箱: {}", email);
        return userRepository.findByEmail(email).map(UserDTO::fromEntity);
    }

    /**
     * 更新用户信息
     * 
     * @param id 用户ID
     * @param userDTO 用户DTO
     * @return 更新后的用户DTO
     */
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("开始更新用户信息，用户ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查用户名唯一性（排除当前用户）
        if (!user.getUsername().equals(userDTO.getUsername()) &&
            userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱唯一性（排除当前用户）
        if (!user.getEmail().equals(userDTO.getEmail()) &&
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 更新用户信息
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setPhone(userDTO.getPhone());
        user.setStatus(userDTO.getStatus());
        user.setRole(userDTO.getRole());
        
        User updatedUser = userRepository.save(user);
        log.info("用户信息更新成功，用户ID: {}", updatedUser.getId());
        
        return UserDTO.fromEntity(updatedUser);
    }

    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    public void deleteUser(Long id) {
        log.info("开始删除用户，用户ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        userRepository.delete(user);
        log.info("用户删除成功，用户ID: {}", id);
    }

    /**
     * 分页查询用户
     * 
     * @param pageable 分页参数
     * @return 用户分页结果
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        log.debug("分页查询用户，页码: {}, 大小: {}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable).map(UserDTO::fromEntity);
    }

    /**
     * 根据姓名模糊查询用户
     * 
     * @param fullName 姓名
     * @param pageable 分页参数
     * @return 用户分页结果
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> findByFullNameContaining(String fullName, Pageable pageable) {
        log.debug("根据姓名模糊查询用户，姓名: {}", fullName);
        return userRepository.findByFullNameContainingIgnoreCase(fullName, pageable)
                .map(UserDTO::fromEntity);
    }

    /**
     * 根据状态查找用户
     * 
     * @param status 用户状态
     * @return 用户列表
     */
    @Transactional(readOnly = true)
    public List<UserDTO> findByStatus(User.UserStatus status) {
        log.debug("根据状态查找用户，状态: {}", status);
        return userRepository.findByStatus(status).stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据角色查找用户
     * 
     * @param role 用户角色
     * @return 用户列表
     */
    @Transactional(readOnly = true)
    public List<UserDTO> findByRole(User.UserRole role) {
        log.debug("根据角色查找用户，角色: {}", role);
        return userRepository.findByRole(role).stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 查找所有激活状态的用户
     * 
     * @return 激活用户列表
     */
    @Transactional(readOnly = true)
    public List<UserDTO> findAllActiveUsers() {
        log.debug("查找所有激活状态的用户");
        return userRepository.findAllActiveUsers().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 更新用户最后登录时间
     * 
     * @param id 用户ID
     */
    public void updateLastLoginTime(Long id) {
        log.debug("更新用户最后登录时间，用户ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 统计各状态用户数量
     * 
     * @return 状态统计结果
     */
    @Transactional(readOnly = true)
    public List<Object[]> countByStatus() {
        log.debug("统计各状态用户数量");
        return userRepository.countByStatus();
    }

    /**
     * 统计各角色用户数量
     * 
     * @return 角色统计结果
     */
    @Transactional(readOnly = true)
    public List<Object[]> countByRole() {
        log.debug("统计各角色用户数量");
        return userRepository.countByRole();
    }
} 