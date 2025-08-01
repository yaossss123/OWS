package com.example.order.controller;

import com.example.order.dto.CustomerDTO;
import com.example.order.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 客户管理控制器
 * 
 * @author Order Management System
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Validated
@Tag(name = "客户管理", description = "客户相关API接口")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 创建客户
     * 
     * @param customerDTO 客户信息
     * @return 创建的客户信息
     */
    @PostMapping
    @Operation(summary = "创建客户", description = "创建新客户")
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        
        log.info("开始创建客户，客户编码: {}", customerDTO.getCustomerCode());
        
        try {
            CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
            log.info("客户创建成功，客户ID: {}", createdCustomer.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
        } catch (Exception e) {
            log.error("客户创建失败，错误信息: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 根据ID查询客户
     * 
     * @param id 客户ID
     * @return 客户信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询客户", description = "根据客户ID获取客户详细信息")
    public ResponseEntity<CustomerDTO> getCustomerById(
            @Parameter(description = "客户ID") @PathVariable @NotNull Long id) {
        
        log.info("查询客户，客户ID: {}", id);
        
        return customerService.findById(id)
                .map(customer -> {
                    log.info("客户查询成功，客户ID: {}", id);
                    return ResponseEntity.ok(customer);
                })
                .orElseGet(() -> {
                    log.warn("客户不存在，客户ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 根据客户编码查询客户
     * 
     * @param customerCode 客户编码
     * @return 客户信息
     */
    @GetMapping("/code/{customerCode}")
    @Operation(summary = "根据客户编码查询客户", description = "根据客户编码获取客户详细信息")
    public ResponseEntity<CustomerDTO> getCustomerByCode(
            @Parameter(description = "客户编码") @PathVariable @NotNull String customerCode) {
        
        log.info("根据客户编码查询客户，客户编码: {}", customerCode);
        
        return customerService.findByCustomerCode(customerCode)
                .map(customer -> {
                    log.info("客户查询成功，客户编码: {}", customerCode);
                    return ResponseEntity.ok(customer);
                })
                .orElseGet(() -> {
                    log.warn("客户不存在，客户编码: {}", customerCode);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 根据邮箱查询客户
     * 
     * @param email 邮箱
     * @return 客户信息
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "根据邮箱查询客户", description = "根据邮箱获取客户详细信息")
    public ResponseEntity<CustomerDTO> getCustomerByEmail(
            @Parameter(description = "邮箱") @PathVariable @NotNull String email) {
        
        log.info("根据邮箱查询客户，邮箱: {}", email);
        
        return customerService.findByEmail(email)
                .map(customer -> {
                    log.info("客户查询成功，邮箱: {}", email);
                    return ResponseEntity.ok(customer);
                })
                .orElseGet(() -> {
                    log.warn("客户不存在，邮箱: {}", email);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 更新客户信息
     * 
     * @param id 客户ID
     * @param customerDTO 更新的客户信息
     * @return 更新后的客户信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新客户", description = "更新客户基本信息")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Parameter(description = "客户ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody CustomerDTO customerDTO) {
        
        log.info("更新客户，客户ID: {}", id);
        
        try {
            CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
            log.info("客户更新成功，客户ID: {}", id);
            
            return ResponseEntity.ok(updatedCustomer);
        } catch (Exception e) {
            log.error("客户更新失败，客户ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 删除客户
     * 
     * @param id 客户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除客户", description = "删除指定客户")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "客户ID") @PathVariable @NotNull Long id) {
        
        log.info("删除客户，客户ID: {}", id);
        
        try {
            customerService.deleteCustomer(id);
            log.info("客户删除成功，客户ID: {}", id);
            
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("客户删除失败，客户ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 分页查询所有客户
     * 
     * @param pageable 分页参数
     * @return 客户分页结果
     */
    @GetMapping
    @Operation(summary = "分页查询客户", description = "分页查询所有客户")
    public ResponseEntity<Page<CustomerDTO>> getAllCustomers(
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("分页查询客户，页码: {}, 大小: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<CustomerDTO> customers = customerService.findAll(pageable);
        log.info("客户查询完成，总记录数: {}", customers.getTotalElements());
        
        return ResponseEntity.ok(customers);
    }

    /**
     * 根据名称搜索客户
     * 
     * @param name 客户名称关键词
     * @param pageable 分页参数
     * @return 客户分页结果
     */
    @GetMapping("/search")
    @Operation(summary = "根据名称搜索客户", description = "根据客户名称关键词搜索客户")
    public ResponseEntity<Page<CustomerDTO>> searchCustomersByName(
            @Parameter(description = "客户名称关键词") @RequestParam @NotNull String name,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("根据名称搜索客户，关键词: {}", name);
        
        Page<CustomerDTO> customers = customerService.findByNameContaining(name, pageable);
        log.info("客户搜索完成，关键词: {}, 总记录数: {}", name, customers.getTotalElements());
        
        return ResponseEntity.ok(customers);
    }

    /**
     * 根据状态查询客户
     * 
     * @param status 客户状态
     * @return 客户列表
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态查询客户", description = "查询指定状态的所有客户")
    public ResponseEntity<List<CustomerDTO>> getCustomersByStatus(
            @Parameter(description = "客户状态") @PathVariable @NotNull String status) {
        
        log.info("根据状态查询客户，状态: {}", status);
        
        List<CustomerDTO> customers = customerService.findByStatus(
                com.example.order.entity.Customer.CustomerStatus.valueOf(status));
        log.info("状态客户查询完成，状态: {}, 客户数量: {}", status, customers.size());
        
        return ResponseEntity.ok(customers);
    }

    /**
     * 查询所有活跃客户
     * 
     * @return 活跃客户列表
     */
    @GetMapping("/active")
    @Operation(summary = "查询活跃客户", description = "查询所有状态为活跃的客户")
    public ResponseEntity<List<CustomerDTO>> getActiveCustomers() {
        
        log.info("查询活跃客户");
        
        List<CustomerDTO> customers = customerService.findAllActiveCustomers();
        log.info("活跃客户查询完成，客户数量: {}", customers.size());
        
        return ResponseEntity.ok(customers);
    }

    /**
     * 检查客户编码是否存在
     * 
     * @param customerCode 客户编码
     * @return 是否存在
     */
    @GetMapping("/exists/code/{customerCode}")
    @Operation(summary = "检查客户编码是否存在", description = "检查指定客户编码是否已存在")
    public ResponseEntity<Boolean> checkCustomerCodeExists(
            @Parameter(description = "客户编码") @PathVariable @NotNull String customerCode) {
        
        log.info("检查客户编码是否存在，客户编码: {}", customerCode);
        
        boolean exists = customerService.existsByCustomerCode(customerCode);
        log.info("客户编码检查完成，客户编码: {}, 存在: {}", customerCode, exists);
        
        return ResponseEntity.ok(exists);
    }

    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    @GetMapping("/exists/email/{email}")
    @Operation(summary = "检查邮箱是否存在", description = "检查指定邮箱是否已存在")
    public ResponseEntity<Boolean> checkEmailExists(
            @Parameter(description = "邮箱") @PathVariable @NotNull String email) {
        
        log.info("检查邮箱是否存在，邮箱: {}", email);
        
        boolean exists = customerService.existsByEmail(email);
        log.info("邮箱检查完成，邮箱: {}, 存在: {}", email, exists);
        
        return ResponseEntity.ok(exists);
    }

    /**
     * 统计客户状态分布
     * 
     * @return 状态统计结果
     */
    @GetMapping("/statistics/status")
    @Operation(summary = "统计客户状态分布", description = "统计各状态客户数量")
    public ResponseEntity<List<Object[]>> getCustomerStatusStatistics() {
        
        log.info("统计客户状态分布");
        
        List<Object[]> statistics = customerService.countByStatus();
        log.info("客户状态统计完成，统计项数量: {}", statistics.size());
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * 根据信用额度范围查询客户
     * 
     * @param minCredit 最小信用额度
     * @param maxCredit 最大信用额度
     * @param pageable 分页参数
     * @return 客户分页结果
     */
    @GetMapping("/credit-range")
    @Operation(summary = "根据信用额度范围查询客户", description = "查询指定信用额度范围内的客户")
    public ResponseEntity<Page<CustomerDTO>> getCustomersByCreditRange(
            @Parameter(description = "最小信用额度") @RequestParam @NotNull BigDecimal minCredit,
            @Parameter(description = "最大信用额度") @RequestParam @NotNull BigDecimal maxCredit,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("根据信用额度范围查询客户，最小额度: {}, 最大额度: {}", minCredit, maxCredit);
        
        Page<CustomerDTO> customers = customerService.findByCreditLimitBetween(minCredit, maxCredit, pageable);
        log.info("信用额度范围客户查询完成，总记录数: {}", customers.getTotalElements());
        
        return ResponseEntity.ok(customers);
    }
} 