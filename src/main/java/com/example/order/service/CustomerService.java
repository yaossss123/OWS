package com.example.order.service;

import com.example.order.dto.CustomerDTO;
import com.example.order.entity.Customer;
import com.example.order.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 客户服务类
 *
 * 功能: 提供客户相关的业务逻辑处理
 * 逻辑链: 请求接收 -> 数据验证 -> 业务处理 -> 结果返回
 * 注意事项: 需要处理客户编码的唯一性验证和信用额度管理
 *
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * 创建客户
     *
     * @param customerDTO 客户DTO
     * @return 创建的客户DTO
     */
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        log.info("开始创建客户，客户编码: {}", customerDTO.getCustomerCode());

        // 验证客户编码唯一性
        if (customerRepository.existsByCustomerCode(customerDTO.getCustomerCode())) {
            throw new RuntimeException("客户编码已存在");
        }

        // 验证邮箱唯一性（如果提供）
        if (customerDTO.getEmail() != null && !customerDTO.getEmail().isEmpty() &&
            customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        // 转换为实体并保存
        Customer customer = customerDTO.toEntity();
        customer.setStatus(Customer.CustomerStatus.ACTIVE);

        Customer savedCustomer = customerRepository.save(customer);
        log.info("客户创建成功，客户ID: {}", savedCustomer.getId());

        return CustomerDTO.fromEntity(savedCustomer);
    }

    /**
     * 根据ID查找客户
     *
     * @param id 客户ID
     * @return 客户DTO
     */
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> findById(Long id) {
        log.debug("查找客户，客户ID: {}", id);
        return customerRepository.findById(id).map(CustomerDTO::fromEntity);
    }

    /**
     * 根据客户编码查找客户
     *
     * @param customerCode 客户编码
     * @return 客户DTO
     */
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> findByCustomerCode(String customerCode) {
        log.debug("根据客户编码查找客户，客户编码: {}", customerCode);
        return customerRepository.findByCustomerCode(customerCode).map(CustomerDTO::fromEntity);
    }

    /**
     * 根据邮箱查找客户
     *
     * @param email 邮箱
     * @return 客户DTO
     */
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> findByEmail(String email) {
        log.debug("根据邮箱查找客户，邮箱: {}", email);
        return customerRepository.findByEmail(email).map(CustomerDTO::fromEntity);
    }

    /**
     * 更新客户信息
     *
     * @param id 客户ID
     * @param customerDTO 客户DTO
     * @return 更新后的客户DTO
     */
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        log.info("开始更新客户信息，客户ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("客户不存在"));

        // 检查客户编码唯一性（排除当前客户）
        if (!customer.getCustomerCode().equals(customerDTO.getCustomerCode()) &&
            customerRepository.existsByCustomerCode(customerDTO.getCustomerCode())) {
            throw new RuntimeException("客户编码已存在");
        }

        // 检查邮箱唯一性（排除当前客户）
        if (customerDTO.getEmail() != null && !customerDTO.getEmail().isEmpty() &&
            !customerDTO.getEmail().equals(customer.getEmail()) &&
            customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        // 更新客户信息
        customer.setCustomerCode(customerDTO.getCustomerCode());
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhone(customerDTO.getPhone());
        customer.setAddress(customerDTO.getAddress());
        customer.setContactPerson(customerDTO.getContactPerson());
        customer.setContactPhone(customerDTO.getContactPhone());
        customer.setStatus(customerDTO.getStatus());
        customer.setCreditLimit(customerDTO.getCreditLimit());

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("客户信息更新成功，客户ID: {}", updatedCustomer.getId());

        return CustomerDTO.fromEntity(updatedCustomer);
    }

    /**
     * 删除客户
     *
     * @param id 客户ID
     */
    public void deleteCustomer(Long id) {
        log.info("开始删除客户，客户ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("客户不存在"));

        customerRepository.delete(customer);
        log.info("客户删除成功，客户ID: {}", id);
    }

    /**
     * 分页查询客户
     *
     * @param pageable 分页参数
     * @return 客户分页结果
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findAll(Pageable pageable) {
        log.debug("分页查询客户，页码: {}, 大小: {}", pageable.getPageNumber(), pageable.getPageSize());
        return customerRepository.findAll(pageable).map(CustomerDTO::fromEntity);
    }

    /**
     * 根据姓名模糊查询客户
     *
     * @param name 客户姓名
     * @param pageable 分页参数
     * @return 客户分页结果
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findByNameContaining(String name, Pageable pageable) {
        log.debug("根据姓名模糊查询客户，姓名: {}", name);
        return customerRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(CustomerDTO::fromEntity);
    }

    /**
     * 根据状态查找客户
     *
     * @param status 客户状态
     * @return 客户列表
     */
    @Transactional(readOnly = true)
    public List<CustomerDTO> findByStatus(Customer.CustomerStatus status) {
        log.debug("根据状态查找客户，状态: {}", status);
        return customerRepository.findByStatus(status).stream()
                .map(CustomerDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 查找所有激活状态的客户
     *
     * @return 激活客户列表
     */
    @Transactional(readOnly = true)
    public List<CustomerDTO> findAllActiveCustomers() {
        log.debug("查找所有激活状态的客户");
        return customerRepository.findAllActiveCustomers().stream()
                .map(CustomerDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 检查客户编码是否存在
     *
     * @param customerCode 客户编码
     * @return 是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByCustomerCode(String customerCode) {
        return customerRepository.existsByCustomerCode(customerCode);
    }

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    /**
     * 统计各状态客户数量
     *
     * @return 状态统计结果
     */
    @Transactional(readOnly = true)
    public List<Object[]> countByStatus() {
        log.debug("统计各状态客户数量");
        return customerRepository.countByStatus();
    }

    /**
     * 根据信用额度范围查找客户
     *
     * @param minCredit 最小信用额度
     * @param maxCredit 最大信用额度
     * @param pageable 分页参数
     * @return 客户分页结果
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findByCreditLimitBetween(java.math.BigDecimal minCredit,
                                                      java.math.BigDecimal maxCredit,
                                                      Pageable pageable) {
        log.debug("根据信用额度范围查找客户，范围: {} - {}", minCredit.toString(), maxCredit.toString());
        return customerRepository.findByCreditLimitBetween(minCredit, maxCredit, pageable)
                .map(CustomerDTO::fromEntity);
    }
} 